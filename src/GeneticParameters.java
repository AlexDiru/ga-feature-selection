/**
 * Created by alex on 16/11/14.
 */
public class GeneticParameters {

    public static final int CROSSOVER_METHOD_N_POINT = 0;
    public static final int CROSSOVER_METHOD_HALFWAY = 1;


    public static final int __SVM = 0;
    public static final int __DECISION_TREE = 1;
    public static final int __RANDOM_FOREST = 2;


    public static int classificationMethod = 2;
    public static int crossoverMethod = CROSSOVER_METHOD_N_POINT;
    public static int threadCount = 4;
    public static int populationSize = 50;

    public static String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===Genetic Algorithm Feature Selector===").append("\n");
        sb.append("Classification Method: ").append(getClassificationMethodAsString()).append("\n");
        sb.append("Crossover Method: ").append(getCrossoverMethodAsString()).append("\n");
        sb.append("Validation Method: ").append(DataReader.K_FOLDS).append("-fold Cross Validation\n");
        sb.append("Population Size: ").append(populationSize);
        return sb.toString();
    }


    private static String getClassificationMethodAsString() {
        if (classificationMethod == __SVM)
            return "SVM";
        else if (classificationMethod == __DECISION_TREE)
            return "Decision Tree";
        else if (classificationMethod == __RANDOM_FOREST)
            return "Random Forest";
        return "UNKNOWN";
    }

    private static String getCrossoverMethodAsString() {
        if (crossoverMethod == CROSSOVER_METHOD_N_POINT)
            return BitStringChromosome.N_FOR_N_POINT_CROSSOVER + "-point";
        else if (crossoverMethod == CROSSOVER_METHOD_HALFWAY)
            return "Halfway crossover point";
        return "UNKNOWN";
    }
}
