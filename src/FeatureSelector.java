import libsvm.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//http://stackoverflow.com/questions/10792576/libsvm-java-implementation

public class FeatureSelector {

    private static final int IGNORE_FIRST_N_FEATURES = 4;

    private static final int __SVM = 0;
    private static final int __DECISION_TREE = 1;

    private static final int CLASSIFICATION_METHOD = 0;

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

        dataReader = new DataReader("output.csv",
                                    "output2.csv");
        totalFeatureCount = dataReader.getHeader().size() - IGNORE_FIRST_N_FEATURES; //Subtract two since we are ignoring the row ID and output class as they aren't features

    }

    public void run() {

        //List<Chromosome> population = initialisePopulation();
        List<IChromosome> population = new ArrayList<IChromosome>();
        for (int i = 0; i < populationSize; i++)
            population.add(new BitStringChromosome(totalFeatureCount - IGNORE_FIRST_N_FEATURES).init());

        while (generation < 50) {
            double bestAccuracy = -1;

            for (int i = 0; i < populationSize; i++)
                population.get(i).setFitness(-1);

            for (int i = 0; i < population.size(); i++) {

                double accuracyTrain = 0;
                double accuracyTest = 0;

                if (CLASSIFICATION_METHOD == __SVM) {
                    svm_model model = SVM.create(dataReader, population.get(i).getFeatureIndices());
                    accuracyTrain = SVM.eval(model, population.get(i).getFeatureIndices(), dataReader.getTrainingRecords());
                    accuracyTest = SVM.eval(model, population.get(i).getFeatureIndices(), dataReader.getTestRecords());
                } else if (CLASSIFICATION_METHOD == __DECISION_TREE) {

                }

                if (accuracyTest > bestAccuracy)
                    bestAccuracy = accuracyTest;

                population.get(i).setFitness(accuracyTest);
            }

            generation++;
            System.out.println("Best accuracy of Generation " + generation + ": " + bestAccuracy);


            //Breed using proportional roulette wheel selection
            List<IChromosome> children = new ArrayList<IChromosome>();
            //Sort chromosomes according to fitness
            Collections.sort(population);
            while (children.size() < populationSize) {
                IChromosome father, mother;
                do {
                    father = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                    mother = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                } while (father == mother);

                if (population.get(0) instanceof BitStringChromosome)
                    children.add(BitStringChromosome.crossover((BitStringChromosome) father, (BitStringChromosome) mother));
                else {
                    children.add(crossover((Chromosome) father, (Chromosome) mother));
                    ((BitStringChromosome)children.get(children.size()-1)).mutate();
                }
            }

            population = children;

        }

    }

    public Chromosome crossover(Chromosome father, Chromosome mother) {

        List<Integer> genes = new ArrayList<Integer>();

        /*int splitPointF = (int)(Math.random() * father.getFeatureIndices().size());
        int splitPointM = (int)(Math.random() * mother.getFeatureIndices().size());

        if (Math.random() < 0.5) {
            for (int i = 0; i < splitPointF; i++)
                if (!genes.contains(father.getFeatureIndices().get(i)))
                    genes.add(father.getFeatureIndices().get(i));

            for (int i = splitPointM; i < mother.getFeatureIndices().size(); i++)
                if (!genes.contains(mother.getFeatureIndices().get(i)))
                    genes.add(mother.getFeatureIndices().get(i));
        } else {
            for (int i = 0; i < splitPointM; i++)
                if (!genes.contains(mother.getFeatureIndices().get(i)))
                    genes.add(mother.getFeatureIndices().get(i));

            for (int i = splitPointF; i < father.getFeatureIndices().size(); i++)
                if (!genes.contains(father.getFeatureIndices().get(i)))
                    genes.add(father.getFeatureIndices().get(i));

        }
        */

        while (genes.size() < father.getFeatureIndices().size()) {
            int index;
            if (Math.random() < 0.5)
                index = father.getRandomFeatureIndex();
            else
                index = mother.getRandomFeatureIndex();

            if (!genes.contains(index))
                genes.add(index);
        }

        return new Chromosome(genes);


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
