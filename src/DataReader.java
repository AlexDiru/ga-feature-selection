import org.apache.commons.io.FileUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    public static final int K_FOLDS = 10;

    public Instances[] getTrainingInstances() {
        switch(GeneticParameters.classesToUse) {
            case ALL:
                return trainingInstances;
            case MCIxAD:
                return getTrainMCIandAD();
            case MCIxHC:
                return getTrainMCIandHC();
            case HCxAD:
                return getTrainHCandAD();
        }

        throw new NotImplementedException();
    }

    public Instances[] getTestInstances() {

        switch(GeneticParameters.classesToUse) {
            case ALL:
                return testInstances;
            case MCIxAD:
                return getTestMCIandAD();
            case MCIxHC:
                return getTestMCIandHC();
            case HCxAD:
                return getTestHCandAD();
        }

        throw new NotImplementedException();
    }

    //OVA Classifier
    private Instances[] trainingInstances;
    private Instances[] testInstances;
    private Instances allInstances;

    //OVO Classifier
    private Instances allMCIandAD;
    private Instances allMCIandHC;
    private Instances allHCandAD;

    public Instances[] getTrainMCIandAD() {
        return trainMCIandAD;
    }

    public Instances[] getTestMCIandAD() {
        return testMCIandAD;
    }

    public Instances[] getTrainMCIandHC() {
        return trainMCIandHC;
    }

    public Instances[] getTestMCIandHC() {
        return testMCIandHC;
    }

    public Instances[] getTrainHCandAD() {
        return trainHCandAD;
    }

    public Instances[] getTestHCandAD() {
        return testHCandAD;
    }

    private Instances[] trainMCIandAD;
    private Instances[] testMCIandAD;
    private Instances[] trainMCIandHC;
    private Instances[] testMCIandHC;
    private Instances[] trainHCandAD;
    private Instances[] testHCandAD;

    public Instances getAllInstances() {
        return allInstances;
    }

    public DataReader(String dataFile) {
        try {
            //Sort out 'Instances' for Weka and k-fold cross validation
            allInstances = (new ConverterUtils.DataSource("knimeout.csv")).getDataSet();
            allInstances.setClassIndex(allInstances.numAttributes() - 1);

            //***OVA***
            Instances[][] split = crossValidationSplit(allInstances, K_FOLDS);
            trainingInstances = split[0];
            testInstances = split[1];

            //***OVO***
            allMCIandAD = new Instances(allInstances);
            allHCandAD = new Instances(allInstances);
            allMCIandHC = new Instances(allInstances);
            allMCIandAD.delete();
            allHCandAD.delete();
            allMCIandHC.delete();

            //Split data by class
            for (int i = 0; i < allInstances.numInstances();i++)
                switch ((int)allInstances.instance(i).classValue()) {
                    case 0: //HC
                        allMCIandHC.add(allInstances.instance(i));
                        allHCandAD.add(allInstances.instance(i));
                        break;
                    case 1: //MCI
                        allMCIandHC.add(allInstances.instance(i));
                        allMCIandAD.add(allInstances.instance(i));
                        break;
                    case 2: //AD
                        allHCandAD.add(allInstances.instance(i));
                        allMCIandAD.add(allInstances.instance(i));
                        break;
                }

            //Cross validation split
            Instances[][] splitMCIandHC = crossValidationSplit(allMCIandHC, K_FOLDS);
            Instances[][] splitMCIandAD = crossValidationSplit(allMCIandAD, K_FOLDS);
            Instances[][] splitHCandAD = crossValidationSplit(allHCandAD, K_FOLDS);
            trainMCIandHC = splitMCIandHC[0];
            testMCIandHC = splitMCIandHC[1];
            trainMCIandAD = splitMCIandAD[0];
            testMCIandAD = splitMCIandAD[1];
            trainHCandAD = splitHCandAD[0];
            testHCandAD = splitHCandAD[1];

            new GeneticParameters(getFeatureCount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds) {
        Instances[][] split = new Instances[2][numberOfFolds];

        for (int i = 0; i < numberOfFolds; i++) {
            split[0][i] = data.trainCV(numberOfFolds, i);
            split[1][i] = data.testCV(numberOfFolds, i);
        }

        return split;
    }

    public int getFeatureCount() {
        return trainingInstances[0].numAttributes() - 1;
    }
}
