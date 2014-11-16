import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 16/11/14.
 */
public class BitStringChromosome implements IChromosome {

    public static final int NUMBER_OF_BITS_TO_MUTATE = 3;
    public static final int N_FOR_N_POINT_CROSSOVER = 1;

    private boolean[] bits;
    private double fitness;

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

    public void mutate() {
        int[] mutatedBits = new int[NUMBER_OF_BITS_TO_MUTATE];

        for (int i = 0; i < mutatedBits.length; i++) {
            while (true) {
                int bitIndexToFlip = (int) (Math.random() * bits.length);

                //Check if already flipped - in that case choose a different bit to flip
                for (int j = 0; j < i; j++)
                    if (mutatedBits[j] == bitIndexToFlip)
                        //Choose another bit
                        continue;

                //Flip the bit
                flip(bitIndexToFlip);
                mutatedBits[i] = bitIndexToFlip;
                break;
            }
        }
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

        int[] crossoverIndices = ArrayHelper.getRandomUniqueIndices(N_FOR_N_POINT_CROSSOVER,a.size());
        Arrays.sort(crossoverIndices);

        BitStringChromosome c = new BitStringChromosome(a.size());

        int current = 0;
        for (int crossoverIndex = 0; crossoverIndex < crossoverIndices.length; crossoverIndex++)
            for (current = current; current < crossoverIndices[crossoverIndex]; current++)
                c.setBit(current, crossoverIndex % 2 == 0 ? a.getBit(current) : b.getBit(current));

        return c;
    }



    @Override
    public int compareTo(IChromosome o) {
        return Double.compare(fitness, o.getFitness());
    }
}
