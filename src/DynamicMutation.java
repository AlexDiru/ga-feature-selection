/**
 * Created by alex on 19/11/14.
 */
public class DynamicMutation {


    public static double backAndSchutz(int currentGeneration, int totalGenerations, int numFeatures) {
        double p = numFeatures - 2;
        p /= totalGenerations - 1;
        p *= currentGeneration;
        p += 2;
        return (double)1/p;
    }

    //Fogarty - Varying the probability of mutation in the Genetic Algorithm (1989)
    public static double fogarty(int currentGeneration) {
        double p = Math.pow(2d,(double)currentGeneration);
        p = 0.11375 / p;
        p += (double)1/240;
        return p;
    }
}
