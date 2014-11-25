import libsvm.svm;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Main {

    public static void main(String[] args) {
        new FeatureSelector(500,50,0.6,GeneticParameters.DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES,GeneticParameters.__KNN).run();
        System.out.println("***LR Finished***");
        //new FeatureSelector(500,50,0.6,GeneticParameters.DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES,GeneticParameters.__MLP).run();
        //System.out.println("***MLP Finished***");
    }
}
