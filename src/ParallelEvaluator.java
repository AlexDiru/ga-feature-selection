import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;

import java.util.List;

/**
 * Created by alex on 17/11/14.
 */
public class ParallelEvaluator implements Runnable {

    DataReader dataReader;
    List<IChromosome> population;

    @Override
    public void run() {
        for (int i = 0; i < population.size(); i++) {
            double accuracyTrain = 0;
            double accuracyTest = 0;

            if (GeneticParameters.classificationMethod == GeneticParameters.__SVM) {
                //svm_model model = SVM.create(dataReader, population.get(i).getFeatureIndices());
                //accuracyTrain = SVM.eval(model, population.get(i).getFeatureIndices(), dataReader.getTrainingRecords());
            } else if (GeneticParameters.classificationMethod == GeneticParameters.__DECISION_TREE) {
                Classifier model = new J48();
                FastVector predictions = new FastVector();

                for (int j = 0; j < dataReader.getTrainingInstances().length; j++) {
                    Instances trInstances = population.get(i).getFeatureSubset(dataReader.getTrainingInstances()[j]);
                    Instances teInstances = population.get(i).getFeatureSubset(dataReader.getTestInstances()[j]);

                    Evaluation validation = classify(model, trInstances, teInstances);
                    predictions.appendElements(validation.predictions());
                }

                accuracyTest = calculateAccuracy(predictions);
            }

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

    public ParallelEvaluator(DataReader dataReader, List<IChromosome> chromosomes) {
        this.dataReader = dataReader;
        population = chromosomes;
    }
}
