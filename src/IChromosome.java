import java.util.List;

/**
 * Created by alex on 16/11/14.
 */
public interface IChromosome extends Comparable<IChromosome> {
    List<Integer> getFeatureIndices();

    void setFitness(double d);

    double getFitness();
}
