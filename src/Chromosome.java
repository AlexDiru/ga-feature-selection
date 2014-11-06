import java.util.ArrayList;
import java.util.List;

public class Chromosome {

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
}
