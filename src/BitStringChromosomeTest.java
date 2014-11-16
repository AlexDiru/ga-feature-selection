import org.junit.Assert;
import org.junit.Test;

/**
 * Created by alex on 16/11/14.
 */
public class BitStringChromosomeTest {

    @Test
    public void testCrossover() {
        BitStringChromosome.N_FOR_N_POINT_CROSSOVER = 1;

        BitStringChromosome a = new BitStringChromosome(4);
        BitStringChromosome b = new BitStringChromosome(4);
        for (int i = 0;i < 4; i++) {
            a.setBit(i, false);
            b.setBit(i, true);
        }

        for (int i = 0; i < 100; i++) {
            BitStringChromosome c = BitStringChromosome.crossover(a, b);
            System.out.println(c.toString());
            Assert.assertTrue(!c.getBit(0));
            Assert.assertTrue(c.getBit(3));
        }
    }
}
