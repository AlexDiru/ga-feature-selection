import java.util.List;

public class ParentSelector {

    public static final int ROULETTE_WHEEL = 0;

    public static IChromosome select(List<IChromosome> parents, int method) {
        if (method == ROULETTE_WHEEL) {
            return selectRouletteWheel(parents);
        } else
            return null;
    }

    private static IChromosome selectRouletteWheel(List<IChromosome> parents) {
        //Top 20 = 30%
        //Top 20 - 40 = 25%
        //Top 40 - 60 = 20%
        //Top 60 - 80 = 15%
        //Top 80 - 100 = 10%

        double p = Math.random();

        if (p < 0.1)
            return selectInQuartiles(parents, 0, 0.2);
        else if (p < 0.25)
            return selectInQuartiles(parents,0.2,0.4);
        else if (p < 0.45)
            return selectInQuartiles(parents,0.4,0.6);
        else if (p < 0.7)
            return selectInQuartiles(parents,0.6,0.8);
        else
            return selectInQuartiles(parents,0.8,1);
    }

    private static IChromosome selectInQuartiles(List<IChromosome> parents, double lq, double uq) {
        int minSize = (int)(lq * parents.size());
        int maxSize = (int)(uq * parents.size());

        if (minSize < 0)
            minSize = 0;
        if (maxSize >= parents.size())
            maxSize = parents.size() - 1;

        return parents.get((int)(Math.random() * (maxSize - minSize))+minSize);
    }

}
