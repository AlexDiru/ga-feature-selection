/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 06/11/14
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
public class TerminationCriteria {

    private int type;

    private float targetFitness;
    private int targetGeneration;

    public void conditionIsDesiredFitness(float fitness) {
        type = 0;
        targetFitness = fitness;
    }

    public void conditionIsDesiredGenerations(int generation) {
        type = 1;
        targetGeneration = generation;
    }

}
