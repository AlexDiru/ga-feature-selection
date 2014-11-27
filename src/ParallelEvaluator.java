import libsvm.svm_model;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.FastVector;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 17/11/14.
 */
public class ParallelEvaluator implements Runnable {

    DataReader dataReader;
    List<BitStringChromosome> population;

    //Returns a list with all the accuracies
    //For OVA: just a list of one element
    //For OVO: Average Accuracy, HCvsAD, MCIvsAD, MCIvsHC
    public List<Double> svm(BitStringChromosome chromosome) {
        if (GeneticParameters.USE_OVO) {
            //ONE-VERSUS-ONE
            double MCIvsADTotal = 0;
            double MCIvsHCTotal = 0;
            double HCvsADTotal = 0;
            //MCI vs AD
            for (int j = 0; j < dataReader.getTestHCandAD().length; j++) {
                Instances[] trInstancesArr = new Instances[3];
                Instances[] teInstancesArr = new Instances[3];

                trInstancesArr[0] = chromosome.getFeatureSubset(dataReader.getTrainHCandAD()[j]);
                trInstancesArr[1] = chromosome.getFeatureSubset(dataReader.getTrainMCIandAD()[j]);
                trInstancesArr[2] = chromosome.getFeatureSubset(dataReader.getTrainMCIandHC()[j]);

                teInstancesArr[0] = chromosome.getFeatureSubset(dataReader.getTestHCandAD()[j]);
                teInstancesArr[1] = chromosome.getFeatureSubset(dataReader.getTestMCIandAD()[j]);
                teInstancesArr[2] = chromosome.getFeatureSubset(dataReader.getTestMCIandHC()[j]);

                svm_model[] models = new svm_model[3];
                for (int i = 0; i < 3; i++)
                    models[i] = SVM.create(trInstancesArr[i]);

                HCvsADTotal += SVM.eval(models[0], teInstancesArr[0]);
                MCIvsADTotal += SVM.eval(models[1], teInstancesArr[1]);
                MCIvsHCTotal += SVM.eval(models[2], teInstancesArr[2]);
            }

            double totalAverage = (HCvsADTotal + MCIvsADTotal + MCIvsHCTotal) / (dataReader.getTestHCandAD().length * 3);

            HCvsADTotal /= dataReader.getTestHCandAD().length;
            MCIvsADTotal /= dataReader.getTestHCandAD().length;
            MCIvsHCTotal /= dataReader.getTestHCandAD().length;

            List<Double> aList = new ArrayList<Double>();
            aList.add(HCvsADTotal);
            aList.add(MCIvsADTotal);
            aList.add(MCIvsHCTotal);
            aList.add(totalAverage);
            return aList;
        } else {
            //ONE-VERSUS-ALL
            double accuracyTotal = 0;
            for (int j = 0; j < dataReader.getTrainingInstances().length; j++) {
                Instances trInstances = chromosome.getFeatureSubset(dataReader.getTrainingInstances()[j]);
                Instances teInstances = chromosome.getFeatureSubset(dataReader.getTestInstances()[j]);

                svm_model model = SVM.create(trInstances);
                accuracyTotal += SVM.eval(model, teInstances);
            }

            accuracyTotal /= dataReader.getTrainingInstances().length;
            List<Double> aList = new ArrayList<Double>();
            aList.add(accuracyTotal * 100);
            return aList;
        }
    }

    public List<Double> decisionTree(BitStringChromosome chromosome) {
        return wekaGeneric(new J48(), chromosome);
    }

    private List<Double> wekaGeneric(Classifier model, BitStringChromosome chromosome) {

        if (!GeneticParameters.USE_OVO) {
            FastVector predictions = new FastVector();

            for (int j = 0; j < dataReader.getTrainingInstances().length; j++) {
                Instances trInstances = chromosome.getFeatureSubset(dataReader.getTrainingInstances()[j]);
                Instances teInstances = chromosome.getFeatureSubset(dataReader.getTestInstances()[j]);

                Evaluation validation = classify(model, trInstances, teInstances);
                predictions.appendElements(validation.predictions());
            }

            List<Double> aList = new ArrayList<Double>();
            aList.add(calculateAccuracy(predictions));
            return aList;
        } else {
            //ONE-VERSUS-ONE
            double MCIvsADAcc = 0;
            double MCIvsHCAcc = 0;
            double HCvsADAcc = 0;

            FastVector[] predictions = new FastVector[3];
            Arrays.fill(predictions, new FastVector());

            for (int j = 0; j < dataReader.getTestHCandAD().length; j++) {
                Instances[] trInstancesArr = new Instances[3];
                Instances[] teInstancesArr = new Instances[3];

                trInstancesArr[0] = chromosome.getFeatureSubset(dataReader.getTrainHCandAD()[j]);
                trInstancesArr[1] = chromosome.getFeatureSubset(dataReader.getTrainMCIandAD()[j]);
                trInstancesArr[2] = chromosome.getFeatureSubset(dataReader.getTrainMCIandHC()[j]);

                teInstancesArr[0] = chromosome.getFeatureSubset(dataReader.getTestHCandAD()[j]);
                teInstancesArr[1] = chromosome.getFeatureSubset(dataReader.getTestMCIandAD()[j]);
                teInstancesArr[2] = chromosome.getFeatureSubset(dataReader.getTestMCIandHC()[j]);

                Evaluation[] validations = new Evaluation[3];
                for (int i = 0; i < 3; i++) {
                    validations[i] = classify(model, trInstancesArr[i], teInstancesArr[i]);
                    predictions[i].appendElements(validations[i].predictions());
                }
            }

            HCvsADAcc = calculateAccuracy(predictions[0]);
            MCIvsADAcc = calculateAccuracy(predictions[1]);
            MCIvsHCAcc = calculateAccuracy(predictions[2]);

            double totalAverage = (HCvsADAcc + MCIvsADAcc + MCIvsHCAcc) / (dataReader.getTestHCandAD().length*3);

            List<Double> aList = new ArrayList<Double>();
            aList.add(HCvsADAcc);
            aList.add(MCIvsADAcc);
            aList.add(MCIvsHCAcc);
            aList.add(totalAverage);
            return aList;
        }
    }

    public List<Double> logisticRegression(BitStringChromosome chromosome) { return wekaGeneric(new Logistic(), chromosome);}

    public List<Double> mlp(BitStringChromosome chromosome) { return wekaGeneric(new MultilayerPerceptron(), chromosome); }

    public List<Double> knn(BitStringChromosome chromosome) { return wekaGeneric(new IBk(), chromosome); }

    public List<Double> randomForest(BitStringChromosome chromosome) {
        RandomForest rf = new RandomForest();
        rf.setNumTrees(50);
        return wekaGeneric(rf, chromosome);
    }

    @Override
    public void run() {
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).getFitness() >= 0)
                continue;

            double accuracyTest = 0;

            if (GeneticParameters.classificationMethod == GeneticParameters.__SVM) {
                accuracyTest = svm(population.get(i)).get(0);
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__DECISION_TREE) {
                accuracyTest = decisionTree(population.get(i)).get(0);
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__RANDOM_FOREST) {
                accuracyTest = randomForest(population.get(i)).get(0);
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__LOGISTIC) {
                accuracyTest = logisticRegression(population.get(i)).get(0);
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__MLP) {
                accuracyTest = mlp(population.get(i)).get(0);
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__KNN)
                accuracyTest = knn(population.get(i)).get(0);
            else
                throw new NotImplementedException();

            population.get(i).setFitness(accuracyTest);
        }
    }

    public static Evaluation classify(Classifier model,
                                      Instances trainingSet, Instances testingSet) {
        try {
            Evaluation evaluation = new Evaluation(trainingSet);

            model.buildClassifier(trainingSet);
            evaluation.evaluateModel(model, testingSet);

            return evaluation;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }

    public ParallelEvaluator(DataReader dataReader, List<BitStringChromosome> chromosomes) {
        this.dataReader = dataReader;
        population = chromosomes;
    }
}
