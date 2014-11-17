import libsvm.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//http://stackoverflow.com/questions/10792576/libsvm-java-implementation

public class FeatureSelector {

    private static final int NUM_THREADS = 4;

    public static final int __SVM = 0;
    public static final int __DECISION_TREE = 1;

    public static final int CLASSIFICATION_METHOD = 1;

    private final float mutationRate;
    private final int mutationMethod;
    private final int crossoverMethod;
    private final int fitnessFunctionMethod;
    private final TerminationCriteria terminationCriteria;
    private final int populationSize;
    private DataReader dataReader;
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
    }

    public void run() {

        List<IChromosome> population = new ArrayList<IChromosome>();
        for (int i = 0; i < populationSize; i++)
            population.add(new BitStringChromosome(dataReader.getFeatureCount()).init());

        while (true) {
            double bestAccuracy = -1;

            for (int i = 0; i < populationSize; i++)
                population.get(i).setFitness(-1);

            //Evaluate in parallel
            Thread[] evals = new Thread[NUM_THREADS];
            for (int i = 0; i < NUM_THREADS; i++) {
                int fromIndex = (int)(i * ((double)populationSize/NUM_THREADS));
                int toIndex = (int)((i+1) * ((double)populationSize/NUM_THREADS));
                evals[i] = new Thread(new ParallelEvaluator(dataReader, population.subList(fromIndex, toIndex)));
                evals[i].start();
            }

            for (int i = 0; i < NUM_THREADS; i++)
                try {
                    evals[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            generation++;


            //Breed using proportional roulette wheel selection
            List<IChromosome> children = new ArrayList<IChromosome>();
            //Sort chromosomes according to fitness
            Collections.sort(population);

            System.out.println("Best/Worst accuracy of Generation " + generation + ": " + population.get(0).getFitness() + "\t" + population.get(population.size() - 1).getFitness());

            while (children.size() < populationSize) {
                IChromosome father, mother;
                do {
                    father = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                    mother = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                } while (father == mother);

                if (population.get(0) instanceof BitStringChromosome) {
                    children.add(BitStringChromosome.crossover((BitStringChromosome) father, (BitStringChromosome) mother));
                    children.get(children.size() - 1).mutate();
                }
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
