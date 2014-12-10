import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by alex on 29/11/14.
 */
public abstract class OVO3WayWekaClassifier extends OVO3WayClassifier {

    protected Classifier[] models;
    protected DataReader dataReader;

    public OVO3WayWekaClassifier(DataReader dataReader, Instances allInstances, String featuresMCIxAD, String featuresMCIxHC, String featuresHCxAD) {
        super(allInstances, featuresMCIxAD, featuresMCIxHC, featuresHCxAD);
        this.dataReader = dataReader;
        models = new Classifier[3];
    }

    public static Classifier buildWekaClassifier(Classifier model, BitStringChromosome chromosome, Instances _instances) {
        Instances instances = chromosome.getFeatureSubset(_instances);
        try {
            model.buildClassifier(instances);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }
    @Override
    public void learn(Instances instancesMCIxAD, Instances instancesMCIxHC, Instances instancesHCxAD) {
        models[0] = OVO3WayWekaClassifier.buildWekaClassifier(models[0], new BitStringChromosome(features[0]), instancesMCIxAD);
        models[1] = OVO3WayWekaClassifier.buildWekaClassifier(models[1], new BitStringChromosome(features[1]), instancesMCIxHC);
        models[2] = OVO3WayWekaClassifier.buildWekaClassifier(models[2], new BitStringChromosome(features[2]), instancesHCxAD);
    }

    @Override
    public int predictMCIorAD(Instance instance) {
        try {
            Evaluation evaluation = new Evaluation(dataReader.getAllMCIandAD());
            return (int)evaluation.evaluateModelOnce(models[0], instance);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int predictMCIorHC(Instance instance) {
        try {
            Evaluation evaluation = new Evaluation(dataReader.getAllMCIandHC());
            return (int)evaluation.evaluateModelOnce(models[1], instance);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int predictHCorAD(Instance instance) {
        try {
            Evaluation evaluation = new Evaluation(dataReader.getAllHCandAD());
            return (int)evaluation.evaluateModelOnce(models[2], instance);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
