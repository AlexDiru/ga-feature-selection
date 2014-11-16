/**
 * Created by alex on 16/11/14.
 */
public class ArrayHelper {

    public static int[] getRandomUniqueIndices(int count, int upperBoundExclusive) {
        int[] retval = new int[count];

        retval[0] = (int)(Math.random() * count);

        for (int i = 1; i < count; i++) {
            while (true) {
                retval[i] = (int) (Math.random() * count);
                //check if already exists
                for (int j = 0; j < i; j++)
                    if (retval[j] == retval[i])
                        //pick another number
                        continue;

                break;
            }
        }

        return retval;

    }
}
