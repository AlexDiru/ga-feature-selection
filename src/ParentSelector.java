import java.util.List;

public class ParentSelector {

    public static final int ROULETTE_WHEEL = 0;

    public static BitStringChromosome select(List<BitStringChromosome> parents, int method) {
        if (method == ROULETTE_WHEEL) {
            return selectRouletteWheel(parents);
        } else
            return null;
    }

    private static BitStringChromosome selectRouletteWheel(List<BitStringChromosome> parents) {

        double p = Math.random();

        if (p < 0.05)
            return selectInQuartiles(parents, 0, 0.2);
        else if (p < 0.20)
            return selectInQuartiles(parents,0.2,0.4);
        else if (p < 0.40)
            return selectInQuartiles(parents,0.4,0.6);
        else if (p < 0.65)
            return selectInQuartiles(parents,0.6,0.8);
        else
            return selectInQuartiles(parents,0.8,1);
    }

    private static BitStringChromosome selectInQuartiles(List<BitStringChromosome> parents, double lq, double uq) {
        int minSize = (int)(lq * parents.size());
        int maxSize = (int)(uq * parents.size());

        if (minSize < 0)
            minSize = 0;
        if (maxSize >= parents.size())
            maxSize = parents.size() - 1;

        return parents.get((int)(Math.random() * (maxSize - minSize))+minSize);
    }
}