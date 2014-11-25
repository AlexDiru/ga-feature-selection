import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//http://stackoverflow.com/questions/10792576/libsvm-java-implementation

public class FeatureSelector {

    private static int targetGeneration = 500;

    private DataReader dataReader;
    private int generation = 0;

    public FeatureSelector(DataReader dataReader, int targetGeneration, int populationSize, double crossoverRate, int mutationRateMethod, int classificationMethod) {
        this.dataReader = dataReader;

        GeneticParameters.populationSize = populationSize;
        GeneticParameters.crossoverRate = crossoverRate;
        GeneticParameters.dynamicMutationMethod = mutationRateMethod;
        GeneticParameters.classificationMethod = classificationMethod;
        this.targetGeneration = targetGeneration;

        DataLogger.init();
    }

    public void run() {

        List<BitStringChromosome> population = new ArrayList<BitStringChromosome>();
        for (int i = 0; i < GeneticParameters.populationSize; i++)
            population.add(new BitStringChromosome(dataReader.getFeatureCount()).init());

        String params = GeneticParameters.toText();
        System.out.println(params);
        DataLogger.writeln(params);


        while (generation < targetGeneration) {

            //for (int i = 0; i < GeneticParameters.populationSize; i++)
            //    population.get(i).setFitness(-1);

            //Evaluate in parallel
            Thread[] evals = new Thread[GeneticParameters.threadCount];
            for (int i = 0; i < evals.length; i++) {
                int fromIndex = (int)(i * ((double)GeneticParameters.populationSize/evals.length));
                int toIndex = (int)((i+1) * ((double)GeneticParameters.populationSize/evals.length));
                evals[i] = new Thread(new ParallelEvaluator(dataReader, population.subList(fromIndex, toIndex)));
                evals[i].start();
            }

            for (int i = 0; i < evals.length; i++)
                try {
                    evals[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            generation++;


            //Breed using proportional roulette wheel selection
            List<BitStringChromosome> children = new ArrayList<BitStringChromosome>();
            //Sort chromosomes according to fitness
            Collections.sort(population);

            BitStringChromosome fittest = population.get(population.size() - 1);
            System.out.println(generation + "," + fittest.getFitness() + "," + fittest);
            DataLogger.writeln(generation + "," + fittest.getFitness() + "," + fittest);

            //How many children to generate
            int childrenCount = 0;
            for (int i = 0; i < GeneticParameters.populationSize; i++)
                if (Math.random() < GeneticParameters.crossoverRate)
                    childrenCount++;

            while (children.size() < childrenCount) {
                BitStringChromosome father, mother;
                do {
                    father = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                    mother = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                } while (father == mother);

                children.add(BitStringChromosome.crossover(father, mother));
                children.get(children.size() - 1).mutate(GeneticParameters.getMutationRate(targetGeneration, generation));
            }

            //Perform survivor selection (easier than survivor deletion since roulette wheel is already implemented) and merge children
            while (children.size() < GeneticParameters.populationSize) {
                BitStringChromosome keep = ParentSelector.select(population, ParentSelector.ROULETTE_WHEEL);
                if (!children.contains(keep))
                    children.add(keep);
            }

            population = children;
        }
    }
}
