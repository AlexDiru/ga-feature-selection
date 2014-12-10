import java.util.Arrays;

/**
 * Created by alex on 29/11/14.
 */
public class Vote {

    private int numClassifiers;
    private int numOutputClasses;

    private double[][] weights; //weights[i][j] = the jth weight of classifier i
    private boolean defaultWeights; //Whether the weights have from changed from all 1

    public Vote(int numClassifiers, int numOutputClasses) {
        this.numClassifiers = numClassifiers;
        this.numOutputClasses = numOutputClasses;

        weights = new double[numClassifiers][];
        Arrays.fill(weights, new double[numOutputClasses]);

        //Default weights of 1
        for (int i = 0; i < numClassifiers; i++) {
            Arrays.fill(weights[i], 1);
            defaultWeights = true;
        }
    }

    //predictedClasses[i] = all pairwise classifications of classifier j
    //E.g. predictedClasses[SVM] = { MCI, AD, AD }
    //SVM predicted MCI from MCIxHC,
    //              AD from MCIxAD,
    //              AD from HCxAD
    public int predict(int[][] predictedClasses) {
        int[] count = new int[numOutputClasses];
        Arrays.fill(count, 0);

        for (int i = 0; i < predictedClasses.length; i++)
            for (int j = 0; j < predictedClasses[i].length; j++)
                count[predictedClasses[i][j]]++;

        //If one count is greater than the others, return it
        int maxIndex = -1;
        int maxCount = -1;
        boolean draw = false;
        for (int i = 0; i < numOutputClasses; i++)
        {
            if (count[i] == maxCount)
                draw = true;
            else if (count[i] > maxCount) {
                draw = false;
                maxCount = count[i];
                maxIndex = i;
            }
        }

        if (!draw)
            return maxIndex;
        else
            //Handle draw - use weights
            return 0;
    }
}
