import org.apache.commons.io.FileUtils;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    public static final int K_FOLDS = 10;

    public Instances[] getTrainingInstances() {
        return trainingInstances;
    }

    public Instances[] getTestInstances() {
        return testInstances;
    }

    private Instances[] trainingInstances;
    private Instances[] testInstances;
    private Instances allInstances;

    public Instances getAllInstances() {
        return allInstances;
    }


    public DataReader(String dataFile) {
        try {
            //Sort out 'Instances' for Weka and k-fold cross validation
            allInstances = (new ConverterUtils.DataSource("knimeout.csv")).getDataSet();
            allInstances.setClassIndex(allInstances.numAttributes() - 1);

            //10-split CV
            Instances[][] split = crossValidationSplit(allInstances, K_FOLDS);

            trainingInstances = split[0];
            testInstances = split[1];

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
