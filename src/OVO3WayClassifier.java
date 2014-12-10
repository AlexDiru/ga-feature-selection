import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by alex on 29/11/14.
 */
public abstract class OVO3WayClassifier {

    protected String[] features;

    public abstract int predictMCIorAD(Instance instance);

    public abstract int predictMCIorHC(Instance instance);

    public abstract int predictHCorAD(Instance instance);

    protected OVO3WayClassifier(Instances allInstances, String featuresMCIxAD, String featuresMCIxHC, String featuresHCxAD) {
        features = new String[] {featuresMCIxAD, featuresMCIxHC, featuresHCxAD};
    }

    public abstract void learn(Instances instancesMCIxAD, Instances instancesMCIxHC, Instances instancesHCxAD);

    public abstract Instances[] convertInstances(Instances allInstances);
}
