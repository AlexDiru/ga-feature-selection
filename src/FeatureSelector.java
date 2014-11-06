import java.util.ArrayList;
import java.util.List;

public class FeatureSelector {

    private final float mutationRate;
    private final int mutationMethod;
    private final int crossoverMethod;
    private final int fitnessFunctionMethod;
    private final TerminationCriteria terminationCriteria;
    private final int populationSize;
    private final List<Float> features;

    public FeatureSelector(List<Float> features, int populationSize, TerminationCriteria terminationCriteria, int fitnessFunctionMethod, int crossoverMethod, int mutationMethod,
                           float mutationRate) {
        this.features = features;
        this.populationSize = populationSize;
        this.terminationCriteria = terminationCriteria;
        this.fitnessFunctionMethod = fitnessFunctionMethod;
        this.crossoverMethod = crossoverMethod;
        this.mutationMethod = mutationMethod;
        this.mutationRate = mutationRate;
    }

    public void run() {

        List<List<Float>> population = initialisePopulation();
    }

    public List<List<Float>> initialisePopulation() {
        List<List<Float>> population = new ArrayList<List<Float>>();

        for (int i = 0; i < populationSize; i++)
            population.add(getSubset(features, features.size()/3, false));

        return population;
    }



    public static List<Float> getSubset(List<Float> features, int subsetSize, boolean duplicatesAllowed) {
        List<Float> subset = new ArrayList<Float>();

        for (int i = 0; i < subsetSize; i++) {
            float feature = features.get((int)Math.random() * features.size());

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
