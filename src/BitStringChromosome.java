import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 16/11/14.
 */
public class BitStringChromosome implements Comparable<BitStringChromosome>{

    public static final int NUMBER_OF_BITS_TO_MUTATE = 1;
    public static int N_FOR_N_POINT_CROSSOVER = 1;

    private boolean[] bits;
    private double fitness;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++)
            sb.append(bits[i] ? "1" : "0");
        return sb.toString();
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return fitness;
    }


    public BitStringChromosome(int bitCount) {
        bits = new boolean[bitCount];
    }

    //Set all bits to random
    public BitStringChromosome init() {
        for (int i = 0; i < size(); i++)
            setBit(i, Math.random() < 0.5);
        return this;
    }

    public void setBit(int bitIndex, boolean value) {
        bits[bitIndex] = value;
    }

    private void flip(int bitIndex) {
        bits[bitIndex] = !bits[bitIndex];
    }

    public boolean getBit(int bitIndex) {
        return bits[bitIndex];
    }

    public void mutate(double mutationRate) {
        for (int i = 0; i < size(); i++)
            if (Math.random() < mutationRate)
                flip(i);
    }

    public Instances getFeatureSubset(final Instances instances) {

        Instances subset = new Instances(instances);

        int x = 0;
        for (int i = 0; i < size(); i++) {
            if (!bits[i]) {
                subset.deleteAttributeAt(x);
                x--;
            }
            x++;
        }
        return subset;

    }

    public int size() {
        return bits.length;
    }

    public List<Integer> getFeatureIndices() {
        List<Integer> featureList = new ArrayList<Integer>();

        for (int i = 0; i < size(); i++)
            if (getBit(i))
                featureList.add(i);

        return featureList;
    }

    //n point crossover
    public static BitStringChromosome crossover(BitStringChromosome a, BitStringChromosome b) {

        BitStringChromosome c = new BitStringChromosome(a.size());

        if (GeneticParameters.crossoverMethod == GeneticParameters.CROSSOVER_METHOD_N_POINT) {

            int[] crossoverIndices = ArrayHelper.getRandomUniqueIndices(N_FOR_N_POINT_CROSSOVER + 1, a.size() - 2);
            crossoverIndices[crossoverIndices.length - 1] = a.size() - 1; //this makes it work otherwise the next for loop exits early
            Arrays.sort(crossoverIndices);

            int prev = 0;
            for (int crossoverIndex = 0; crossoverIndex < crossoverIndices.length; crossoverIndex++)
                for (int current = prev; current < crossoverIndices[crossoverIndex] + 1; current++) {
                    boolean bitValue = crossoverIndex % 2 == 0 ? a.getBit(current) : b.getBit(current);
                    c.setBit(current, bitValue);
                    prev++;
                }

        } else if (GeneticParameters.crossoverMethod == GeneticParameters.CROSSOVER_METHOD_HALFWAY) {
            int crossoverIndex = a.size() / 2;
            for (int i = 0; i < crossoverIndex; i++)
                c.setBit(i, a.getBit(i));
            for (int i = crossoverIndex; i < a.size(); i++)
                c.setBit(i, b.getBit(i));
        }

        return c;
    }



    @Override
    public int compareTo(BitStringChromosome o) {
        return Double.compare(fitness, o.getFitness());
    }
}
