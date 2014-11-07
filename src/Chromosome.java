import java.util.ArrayList;
import java.util.List;

public class Chromosome implements Comparable<Chromosome> {

    private List<Integer> features = new ArrayList<Integer>();
    private double fitness = -1;

    public Chromosome(List<Integer> features) {
        this.features = features;
    }


    public static void resetFitness(List<Chromosome> population) {
        for (Chromosome c : population)
            c.fitness = -1;
    }

    public List<Integer> getFeatureIndices() {
        return features;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getRandomFeatureIndex() {
        return features.get((int)(Math.random() * features.size()));
    }

    @Override
    public int compareTo(Chromosome o) {
        return Double.compare(fitness, o.fitness);
    }
}
