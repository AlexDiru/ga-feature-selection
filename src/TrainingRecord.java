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

    public TrainingRecord(String line) {
        String[] cells = line.split(",");

        if (cells[1].equals("\"HC\""))
            clazz = 0;
        else if (cells[1].equals("\"MCI\""))
            clazz = 1;
        else
            clazz = 2;

        attributes = new ArrayList<Double>();

        for (int i = 2; i < cells.length; i++) {
            attributes.add(Double.parseDouble(cells[i]));
        }
    }

}
