import libsvm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//http://stackoverflow.com/questions/10792576/libsvm-java-implementation

public class FeatureSelector {

    private static final int IGNORE_FIRST_N_FEATURES = 4;

    private final float mutationRate;
    private final int mutationMethod;
    private final int crossoverMethod;
    private final int fitnessFunctionMethod;
    private final TerminationCriteria terminationCriteria;
    private final int populationSize;
    private DataReader dataReader;
    private int totalFeatureCount;
    private int generation = 0;

    public FeatureSelector(int populationSize, TerminationCriteria terminationCriteria, int fitnessFunctionMethod, int crossoverMethod, int mutationMethod,
                           float mutationRate) {
        this.populationSize = populationSize;
        this.terminationCriteria = terminationCriteria;
        this.fitnessFunctionMethod = fitnessFunctionMethod;
        this.crossoverMethod = crossoverMethod;
        this.mutationMethod = mutationMethod;
        this.mutationRate = mutationRate;

        //d1 "C:\\Users\\Alex\\Dropbox\\AlexSpedding-PhD\\data\\12May2014\\ADNI_v3.csv"
        //d1 (z score) "C:\\Users\\Alex\\Desktop\\output.csv"

        //d2 C:\Users\Alex\Dropbox\AlexSpedding-PhD\data\12May2014\ADNI_EL_v3.csv
        //d2 (z score)

        dataReader = new DataReader("C:\\Users\\Alex\\Desktop\\output.csv",
                                    "C:\\Users\\Alex\\Desktop\\output2.csv");
        totalFeatureCount = dataReader.getHeader().size() - IGNORE_FIRST_N_FEATURES; //Subtract two since we are ignoring the row ID and output class as they aren't features

    }

    public void run() {

        List<Chromosome> population = initialisePopulation();


        while (generation < 50) {
            double bestAccuracy = -1;

            Chromosome.resetFitness(population);

            for (int i = 0; i < population.size(); i++) {
                svm_model model = createSVM(population.get(i).getFeatureIndices());
                double accuracyTrain = getSVMAccuracy(model, population.get(i).getFeatureIndices(), dataReader.getTrainingRecords());
                double accuracyTest = getSVMAccuracy(model, population.get(i).getFeatureIndices(), dataReader.getTestRecords());

                /*System.out.println("Features: ");
                for (Integer x : population.get(i).getFeatureIndices())
                    System.out.print(x + ", ");
                System.out.println();*/
                System.out.println("Accuracy: " + accuracyTrain + "\t" + accuracyTest);

                if (accuracyTest > bestAccuracy)
                    bestAccuracy = accuracyTest;

                population.get(i).setFitness(accuracyTest);
            }

            generation++;
            System.out.println("Best accuracy of Generation " + generation + ": " + bestAccuracy);


        }

    }

    public svm_model createSVM(List<Integer> featureIndices) {

        //Create an SVM and train it


        svm_parameter param = new svm_parameter();

        // default values

        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0.01; //All the same when this is 1, change to 0.01
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];

        svm_problem prob = new svm_problem();
        prob.l = dataReader.getTrainingRecords().size(); //Length;
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l][];

        //Assign all training records to the SVM
        for (int i = 0; i < dataReader.getTrainingRecords().size(); i++) {
            List<Double> atts = dataReader.getTrainingRecords().get(i).getAttributes();
            prob.x[i] = new svm_node[featureIndices.size()];

            //Only include the selected features here
            for (int j = 0; j < featureIndices.size(); j++) {
                svm_node node = new svm_node();
                node.index = j;
                node.value = atts.get(featureIndices.get(j));
                //System.out.println(node.value);
                prob.x[i][j] = node;
            }


            prob.y[i] = dataReader.getTrainingRecords().get(i).getClazz();
        }

        //System.out.println("Errors with SVM: " + svm.svm_check_parameter(prob,param));
        svm.svm_set_print_string_function(new svm_print_interface() {
            @Override
            public void print(String s) {
                //Don't want to fill console with garbage
                //So print nothing
            }
        });
        svm_model model = svm.svm_train(prob, param);

        return model;
    }

    public double getSVMAccuracy(svm_model model, List<Integer> featureIndices, List<TrainingRecord> testRecords){

        //Evaluate the SVM
        int correctPredictions = 0;

        for (int i = 0; i < testRecords.size(); i++) {
            List<Double> atts = testRecords.get(i).getAttributes();
            svm_node[] nodes = new svm_node[featureIndices.size()];

            //Only include the selected features here
            for (int j = 0; j < featureIndices.size(); j++) {
                svm_node node = new svm_node();
                node.index = j;
                node.value = atts.get(featureIndices.get(j));
                nodes[j] = node;
            }

            int predicted = (int)svm.svm_predict(model,nodes);

            //Correctly predicted
            if (predicted == testRecords.get(i).getClazz())
                correctPredictions++;

        }

        double accuracy = (double)correctPredictions/testRecords.size();

        return accuracy;
    }

    public List<Chromosome> initialisePopulation() {

        System.out.println("Total features: " + totalFeatureCount);


        List<Chromosome> population = new ArrayList<Chromosome>();

        for (int i = 0; i < populationSize; i++)
            population.add(new Chromosome(getSubset(totalFeatureCount, totalFeatureCount / 5, false, IGNORE_FIRST_N_FEATURES)));

        return population;
    }



    public static List<Integer> getSubset(int featureCount, int subsetSize, boolean duplicatesAllowed, int ignoreFirstNFeatures) {
        List<Integer> subset = new ArrayList<Integer>();

        for (int i = 0; i < subsetSize; i++) {
            int feature = (int)(Math.random() * (featureCount - ignoreFirstNFeatures)) + ignoreFirstNFeatures;

            if (duplicatesAllowed)
                subset.add(feature);
            else {
                //Check it's not a duplicate
                if (subset.contains(feature)) {
                    i--;
                    continue;
                } else {
                    subset.add(feature);
                }
            }
        }

        return subset;
    }

}
