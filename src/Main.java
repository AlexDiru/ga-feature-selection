import libsvm.svm;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class Main {

    //https://github.com/arnaudsj/libsvm/blob/master/java/svm_toy.java

    public static void main(String[] args) {

        FeatureSelector fs = new FeatureSelector(500,new TerminationCriteria(),0,0,0,0.1f);
        fs.run();

    }

}
