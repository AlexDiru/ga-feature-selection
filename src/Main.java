import libsvm.svm;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import weka.classifiers.trees.RandomForest;

public class Main {

    public static void main(String[] args) throws Exception {
        //new FeatureSelector("mci_ad_norm.csv",500,GeneticParameters.__SVM, GeneticParameters.ClassesToUse.MCIxAD).run();
        //System.out.println("*Finished*");
        new FeatureSelector("cn_ad_norm.csv",500,GeneticParameters.__SVM, GeneticParameters.ClassesToUse.ALL).run();
        System.out.println("**Finished**");
        new FeatureSelector("mci_hc_norm.csv",500,GeneticParameters.__SVM, GeneticParameters.ClassesToUse.MCIxHC).run();
        System.out.println("***Finished***");
        //new FeatureSelector(500,50,0.6,GeneticParameters.DYNAMIC_MUTATION_METHOD_INVERSE_TOTAL_FEATURES,GeneticParameters.__MLP).run();
        //System.out.println("***MLP Finished***");

       // new CombinedClassifier(new DataReader("knimeout.csv"),new DataReader("cadtrain.csv"));

        //RandomForest rf = new RandomForest();
        //System.out.println(rf.getMaxDepth());
        //System.out.println(rf.getTechnicalInformation().toString());
    }
}
