import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by alex on 16/11/14.
 */
public class GeneticParameters {

    public static final int CROSSOVER_METHOD_N_POINT = 0;
    public static final int CROSSOVER_METHOD_HALFWAY = 1;


    public static final int __SVM = 0;
    public static final int __DECISION_TREE = 1;
    public static final int __RANDOM_FOREST = 2;

    public static final int DYNAMIC_MUTATION_METHOD_CONSTANT = 0;
    public static final int DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES = 1;
    public static final int DYNAMIC_MUTATION_METHOD_FOGARTY = 2;
    public static final int DYNAMIC_MUTATION_METHOD_BACKSCHUTZ = 3;

    public static int classificationMethod = 1;
    public static int crossoverMethod = CROSSOVER_METHOD_N_POINT;
    public static int threadCount = 4;
    public static int populationSize = 50;
    public static double crossoverRate = 0.6;
    public static int dynamicMutationMethod = 0;
    public static int numberOfFeatures;

    public GeneticParameters(int numFeatures) {
        numberOfFeatures = numFeatures;
    }

    public static String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===Genetic Algorithm Feature Selector===").append("\n");
        sb.append("Classification Method: ").append(getClassificationMethodAsString()).append("\n");
        sb.append("Crossover Method: ").append(getCrossoverMethodAsString()).append("\n");
        sb.append("Validation Method: ").append(DataReader.K_FOLDS).append("-fold Cross Validation\n");
        sb.append("Population Size: ").append(populationSize).append("\n");
        sb.append("Crossover Rate: ").append(crossoverRate).append("\n");
        sb.append("(Dynamic) Mutation Method: ").append(getDynamicMutationMethodAsString()).append("\n");
        sb.append("Number of Features: ").append(numberOfFeatures);
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

    public static String getDynamicMutationMethodAsString() {
        if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_CONSTANT)
            return "Constant (0.001)";
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES)
            return "(number of features)^(-1)";
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_FOGARTY)
            return "Fogarty";
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_BACKSCHUTZ)
            return "Back Schutz";
        else
            throw new NotImplementedException();
    }

    public static double getMutationRate(int totalGenerations, int currentGeneration) {
        if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_CONSTANT)
            return 0.001;
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES)
            return 1d/numberOfFeatures;
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_FOGARTY)
            return DynamicMutation.fogarty(currentGeneration);
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_BACKSCHUTZ)
            return DynamicMutation.backAndSchutz(currentGeneration,totalGenerations,numberOfFeatures);
        else
            throw new NotImplementedException();
    }
}
