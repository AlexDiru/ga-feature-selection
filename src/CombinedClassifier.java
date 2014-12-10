import libsvm.svm_model;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.util.Arrays;

/**
 * Created by alex on 24/11/14.
 */
public class CombinedClassifier {
    private J48 decisionTree = new J48();
    private svm_model svm;
    private RandomForest randomForest = new RandomForest();
    private Logistic logisticRegression = new Logistic();
    private IBk kNN = new IBk();

    private String decisionTreeFeatures = "11000100011011001010100000011000011001010000010011111100100110001000100001000010111000010011001111100000011110100010111101001001110010001010100011010101010101100100011001001101011110011001011000100011110011110001010000100011100001110110110011000110110101001001001100111110110100011101011110100110010010010000110010011101110000011000001";
    private String randomForestFeatures = "10110000011100010110111100001011110010010110110010000010001110111101100011100000010100010101001011000110010011011010001111111001101011001100101111011111110101111011110010100000011100101011011000000001001111000111101000100010000011010111011000011110000000001111010111110110000100110101010111001010110101100001100100001010111101011011011";
    private String svmFeatures = "10110100000000001010111000001011010100010000100000100100100011001010011001110010010111110111000010010000111000110100100111101111011111100100101000010111011110000011011000011010111000000111000110011011000011000000000111001110100111010010110001000000001101100010010111100100110001101001111001011000100101101001000000110011101000001111010";
    private String logisticRegressionFeatures = "10111000010100010111010000000010011100010000001010100010001000000100100000001110000000100011101101001111001101011110010000100110000111001111011011111001011000010100111111110100101111011111101010010101101000100010011010001101111001110110100011101010011101001101011101010011011111001101101100100011000111110011000010101000011010010010010";
    private String kNNFeatures = "00000010100010111101010110001000011110001000101000101110110011101110100011001001100100010100001101010101011100110111011110010101111011011100100111010000101001010111001010001111101010011110011110000000100001001001100100000111100110100101100011111001010110001000100001000000011000000100100110111100000111001010111110101000110100010010011";

    private String allFeatures = "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";

    private DataReader dataReader;

    public CombinedClassifier(DataReader trainReader, DataReader testReader) {
        int numClassifiers = 1;

        OVO3WayClassifier dt = new OVO3WayDecisionTree(trainReader,testReader.getAllInstances());
        dt.learn(trainReader.getAllMCIandAD(), trainReader.getAllMCIandHC(), trainReader.getAllHCandAD());

        int correctClassifications = 0;

        Vote vote = new Vote(numClassifiers, 3);

        Instances[] decisionTreeInstances = dt.convertInstances(testReader.getAllInstances());

        for (int testRecord = 0; testRecord < testReader.getAllInstances().numInstances(); testRecord++) {
            int[][] predictions = new int[numClassifiers][];
            Arrays.fill(predictions, new int[3]);
            for (int i = 0; i < numClassifiers; i++) {
                predictions[i][0] = dt.predictMCIorAD(decisionTreeInstances[0].instance(testRecord));
                predictions[i][1] = dt.predictMCIorHC(decisionTreeInstances[1].instance(testRecord));
                predictions[i][2] = dt.predictHCorAD(decisionTreeInstances[2].instance(testRecord));
            }

            int predVal = vote.predict(predictions);
            int classVal = (int)testReader.getAllInstances().instance(testRecord).classValue();

            System.out.println(predictions[0][0] + " | " + predictions[0][1] + " | " + predictions[0][2]);
            System.out.println("Predicting " + classVal + " as " + predVal);

            if (predVal == classVal)
                correctClassifications++;
        }

        System.out.println("Acc: " + ((double)correctClassifications/testReader.getAllInstances().numInstances())*100);


    }
/*
    public CombinedClassifier(DataReader dataReader, DataReader testDataReader) throws Exception {
        int numberOfClassifiers = 5;

        //Setup classifier properties
        randomForest.setNumTrees(50);

        //Train each classifier on full training set (no CV needed)
        BitStringChromosome decisionTreeChromosome = new BitStringChromosome(decisionTreeFeatures);
        BitStringChromosome randomForestChromosome = new BitStringChromosome(randomForestFeatures);
        BitStringChromosome svmChromosome = new BitStringChromosome(svmFeatures);
        BitStringChromosome logisticChromosome = new BitStringChromosome(logisticRegressionFeatures);
        BitStringChromosome kNNChromosome = new BitStringChromosome(kNNFeatures);

        this.dataReader = dataReader;

        build(decisionTree, decisionTreeChromosome);
        System.out.println("Built Decision Tree");
        build(randomForest, randomForestChromosome);
        System.out.println("Built Random Forest");
        build(logisticRegression, logisticChromosome);
        System.out.println("Built Logistic Regression");
        build(kNN, kNNChromosome);
        System.out.println("Built kNN");
        svm = build(svmChromosome);
        System.out.println("Built SVM");

        //Individual classifications
        Evaluation evaluation = new Evaluation(testDataReader.getAllInstances());

        Instances kNNTestInstances = kNNChromosome.getFeatureSubset(testDataReader.getAllInstances());
        Instances decisionTreeTestInstances = decisionTreeChromosome.getFeatureSubset(testDataReader.getAllInstances());
        Instances randomForestTestInstances = randomForestChromosome.getFeatureSubset(testDataReader.getAllInstances());
        Instances svmTestInstances = svmChromosome.getFeatureSubset(testDataReader.getAllInstances());
        Instances logisticTestInstances = logisticChromosome.getFeatureSubset(testDataReader.getAllInstances());

        int[] correctPredictions = new int[numberOfClassifiers];
        Arrays.fill(correctPredictions,0);

        int combinedPredictions = 0;


        for (int i = 0; i < testDataReader.getAllInstances().numInstances(); i++) {
            System.out.println("Predicting Instance " + i + " Actual is: " + (int)testDataReader.getAllInstances().instance(i).classValue());

            int actualClass = (int)dataReader.getAllInstances().instance(i).classValue();

            int[] classOut = new int[numberOfClassifiers];
            int combinedOut;

            classOut[0] = (int)evaluation.evaluateModelOnce(decisionTree, decisionTreeTestInstances.instance(i));
            classOut[1] = (int)evaluation.evaluateModelOnce(randomForest, randomForestTestInstances.instance(i));
            classOut[2] = (int)evaluation.evaluateModelOnce(logisticRegression, logisticTestInstances.instance(i));
            classOut[3] = (int)evaluation.evaluateModelOnce(kNN, kNNTestInstances.instance(i));
            classOut[4] = (int)SVM.predict(svm, svmTestInstances.instance(i));

            //Combined
            int weightHC = 0;
            int weightMCI = 0;
            int weightAD = 0;

            for (int c = 0; c < numberOfClassifiers; c++)
            {
                if (c == 1 || c == 2)
                    continue;

                if (classOut[c] == 0)
                    weightHC++;
                if (classOut[c] == 1)
                    weightMCI++;
                if (classOut[c] == 2)
                    weightAD++;
            }

            if (weightHC > weightAD && weightHC > weightMCI)
                combinedOut = 0;
            else if (weightMCI > weightAD && weightMCI > weightHC)
                combinedOut = 1;
            else if (weightAD > weightHC && weightAD > weightMCI)
                combinedOut = 2;
            else
                combinedOut = classOut[4];

            /*System.out.println("DTr: " + (int)decisionTreeOut);
            System.out.println("RnF: " + (int)randomForestOut);
            System.out.println("Log: " + (int)logisticOut);
            System.out.println("kNN: " + (int)kNNOut);
            System.out.println("SVM: " + (int)svmOut);

            for (int j = 0; j < numberOfClassifiers; j++)
                if (classOut[j] == actualClass)
                    correctPredictions[j]++;

            if (combinedOut == actualClass)
                combinedPredictions++;

        }

        double[] accuracies = new double[numberOfClassifiers];
        for (int j = 0; j < numberOfClassifiers; j++)
            accuracies[j] = 100d * ((double)correctPredictions[j] / testDataReader.getAllInstances().numInstances());

        System.out.println("DTr: " + accuracies[0]);
        System.out.println("RnF: " + accuracies[1]);
        System.out.println("Log: " + accuracies[2]);
        System.out.println("kNN: " + accuracies[3]);
        System.out.println("SVM: " + accuracies[4]);
        System.out.println("Combined: " + 100d*((double)combinedPredictions/testDataReader.getAllInstances().numInstances()));

    }*/

    //private svm_model build( BitStringChromosome chromosome) {
     //   Instances instances = chromosome.getFeatureSubset(dataReader.getAllInstances());
     //   return SVM.create(instances);
    //}

    public Classifier build(Classifier model, BitStringChromosome chromosome) {
        Instances instances = chromosome.getFeatureSubset(dataReader.getAllInstances());
        try {
            model.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
}
