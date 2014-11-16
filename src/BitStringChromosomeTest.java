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
            Assert.assertTrue(!c.getBit(0));
            Assert.assertTrue(c.getBit(3));
        }
    }

    @Test
    public void testCrossoverHard() {
        //If N_POINT is odd, first bit should be false, last true
        //If even, first false, last false


        for (int i = 0; i < 100; i++) {
            BitStringChromosome.N_FOR_N_POINT_CROSSOVER = (int)(Math.random() * 100) + 1;

            BitStringChromosome a = new BitStringChromosome(800);
            BitStringChromosome b = new BitStringChromosome(800);
            for (int x = 0;x < 800; x++) {
                a.setBit(x, false);
                b.setBit(x, true);
            }

                BitStringChromosome c = BitStringChromosome.crossover(a, b);

                if (BitStringChromosome.N_FOR_N_POINT_CROSSOVER % 2 == 1) {
                    Assert.assertTrue(!c.getBit(0));
                    Assert.assertTrue(c.getBit(799));
                } else {
                    Assert.assertTrue(!c.getBit(0));
                    Assert.assertTrue(!c.getBit(799));
                }
            }
    }
}
