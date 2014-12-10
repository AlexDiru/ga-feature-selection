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

    public double svm(BitStringChromosome chromosome) {

        /*double accuracyTotal = 0;
        for (int j = 0; j < dataReader.getTrainingInstances().length; j++) {
            Instances trInstances = chromosome.getFeatureSubset(dataReader.getTrainingInstances()[j]);
            Instances teInstances = chromosome.getFeatureSubset(dataReader.getTestInstances()[j]);

            svm_model model = SVM.create(trInstances);
            accuracyTotal += SVM.eval(model, teInstances);
        }

        accuracyTotal /= dataReader.getTrainingInstances().length;*/
        return SVM.create(chromosome.getFeatureSubset(dataReader.getAllInstances()));
    }

    public double decisionTree(BitStringChromosome chromosome) {
        return wekaGeneric(new J48(), chromosome);
    }

    private double wekaGeneric(Classifier model, BitStringChromosome chromosome) {

        FastVector predictions = new FastVector();

        for (int j = 0; j < dataReader.getTrainingInstances().length; j++) {
            Instances trInstances = chromosome.getFeatureSubset(dataReader.getTrainingInstances()[j]);
            Instances teInstances = chromosome.getFeatureSubset(dataReader.getTestInstances()[j]);

            Evaluation validation = classify(model, trInstances, teInstances);
            predictions.appendElements(validation.predictions());
        }

        return calculateAccuracy(predictions);
    }

    public double logisticRegression(BitStringChromosome chromosome) { return wekaGeneric(new Logistic(), chromosome);}

    public double mlp(BitStringChromosome chromosome) { return wekaGeneric(new MultilayerPerceptron(), chromosome); }

    public double knn(BitStringChromosome chromosome) { return wekaGeneric(new IBk(), chromosome); }

    public double randomForest(BitStringChromosome chromosome) {
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
                accuracyTest = svm(population.get(i));
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__DECISION_TREE) {
                accuracyTest = decisionTree(population.get(i));
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__RANDOM_FOREST) {
                accuracyTest = randomForest(population.get(i));
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__LOGISTIC) {
                accuracyTest = logisticRegression(population.get(i));
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__MLP) {
                accuracyTest = mlp(population.get(i));
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__KNN)
                accuracyTest = knn(population.get(i));
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
