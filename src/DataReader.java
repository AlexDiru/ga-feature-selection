import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

    private List<String> header;
    private List<TrainingRecord> trainingRecords;
    private List<TrainingRecord> testRecords;

    public List<String> getHeader() {
        return header;
    }

    public List<TrainingRecord> getTrainingRecords() {
        return trainingRecords;
    }

    public DataReader(String trainingDataFile, String testDataFile) {
        try {
            List<String> trainingDataLines = FileUtils.readLines(new File(trainingDataFile));
            List<String> testDataLines = FileUtils.readLines(new File(testDataFile));

            String[] headerSplit = trainingDataLines.get(0).split(",");

            header = new ArrayList<String>();

            trainingRecords = new ArrayList<TrainingRecord>();
            testRecords = new ArrayList<TrainingRecord>();

            //Ignore class output, row ID, age and gender so start at index 4
            for (int i = 4; i < headerSplit.length; i++) {
                header.add(headerSplit[i]);
            }

            //Read training data
            for (int i = 1; i < trainingDataLines.size(); i++)
                trainingRecords.add(new TrainingRecord(trainingDataLines.get(i)));

            //Read test data
            for (int i = 1; i < testDataLines.size(); i++)
                testRecords.add(new TrainingRecord(testDataLines.get(i)));


            //Move a random 10% of training into test
            int desiredSize = trainingRecords.size()/5;

            testRecords.clear();

            while (testRecords.size() < desiredSize) {
                TrainingRecord r = trainingRecords.get((int)(Math.random()*trainingRecords.size()));
                if (!testRecords.contains(r)) {
                    testRecords.add(r);
                    trainingRecords.remove(r);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TrainingRecord> getTestRecords() {
        return testRecords;
    }
}
