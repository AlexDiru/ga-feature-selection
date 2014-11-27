import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by alex on 16/11/14.
 */
public class GeneticParameters {

    public enum ClassesToUse {
        ALL,
        MCIxAD,
        MCIxHC,
        HCxAD
    }

    public static ClassesToUse classesToUse = ClassesToUse.ALL;

    public static final int CROSSOVER_METHOD_N_POINT = 0;
    public static final int CROSSOVER_METHOD_HALFWAY = 1;

    public static final int __SVM = 0;
    public static final int __DECISION_TREE = 1;
    public static final int __RANDOM_FOREST = 2;
    public static final int __LOGISTIC = 3;
    public static final int __MLP = 4;
    public static final int __KNN = 5;

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
        sb.append("Number of Features: ").append(numberOfFeatures).append("\n");
        sb.append("OVA or OVO?: ").append(classesToUse.toString());
        return sb.toString();
    }


    private static String getClassificationMethodAsString() {
        if (classificationMethod == __SVM)
            return "SVM";
        else if (classificationMethod == __DECISION_TREE)
            return "Decision Tree";
        else if (classificationMethod == __RANDOM_FOREST)
            return "Random Forest";
        else if (classificationMethod == __LOGISTIC)
            return "Logistic Regression";
        else if (classificationMethod == __MLP)
            return "MLP";
        else if (classificationMethod == __KNN)
            return "KNN";
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

    public static String getAbbreviation() {
        StringBuilder sb = new StringBuilder();

        if (classificationMethod == GeneticParameters.__RANDOM_FOREST)
            sb.append("rf-");
        else if (classificationMethod == GeneticParameters.__DECISION_TREE)
            sb.append("dt-");
        else if (classificationMethod == GeneticParameters.__SVM)
            sb.append("svm-");
        else if (classificationMethod == GeneticParameters.__LOGISTIC)
            sb.append("lr-");
        else if (classificationMethod == GeneticParameters.__MLP)
            sb.append("mlp-");
        else if (classificationMethod == GeneticParameters.__KNN)
            sb.append("knn-");

        sb.append(classesToUse.toString().toLowerCase()).append("-");

        sb.append(populationSize);
        sb.append("-");

        if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_CONSTANT)
            sb.append("c");
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES)
            sb.append("itf");
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_FOGARTY)
            sb.append("f");
        else if (dynamicMutationMethod == DYNAMIC_MUTATION_METHOD_BACKSCHUTZ)
            sb.append("bs");

        return sb.toString();
    }
}
