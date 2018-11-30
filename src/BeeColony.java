import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony;
    private static ArrayList<Integer[]> bestPaths;


    public BeeColony(int beeCount, Dataset dataset) throws IOException {
        colony = new ArrayList<>();
        bestPaths = new ArrayList<>();
        this.beeCount = beeCount;
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i, this, dataset));
        }
    }

    public Bee getBee(int index) {
        return colony.get(index);
    }


    public int danceDuration() {
        //ToDo
        return 0;
    }

    public ArrayList<Integer[]> getBestPaths() {
        return bestPaths;
    }

    public static void addArrayToBestPath(Integer[] path) {
        bestPaths.add(path);
    }

    public void setBestPathsAtIndex(int index, Integer[] path) {
        bestPaths.set(index, path);
    }
}
