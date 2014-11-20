import libsvm.svm;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Main {

    //https://github.com/arnaudsj/libsvm/blob/master/java/svm_toy.java

    public static void main(String[] args) {


        new FeatureSelector(500,50,0.6,GeneticParameters.DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES,GeneticParameters.__DECISION_TREE).run();
        System.out.println("***Finished***");
    }

}
