import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class TrainingRecord {

    private List<Double> attributes;
    private int clazz; //HC = 0 | MCI = 1 | AD = 2

    public List<Double> getAttributes() {
        return attributes;
    }

    public int getClazz() {
        return clazz;
    }

    //Assumes class index on far right
    public TrainingRecord(Instance instance) {
        attributes = new ArrayList<Double>();

        //Class value is included as a value to ignore it
        for (int i = 0; i < instance.numValues() - 1; i++)
            attributes.add(instance.value(i));

        clazz = (int)instance.classValue();
    }

    public static List<TrainingRecord> convert(Instances instances) {
        List<TrainingRecord> records = new ArrayList<TrainingRecord>();

        for (int i = 0; i < instances.numInstances(); i++)
            records.add(new TrainingRecord(instances.instance(i)));

        return records;
    }
}
