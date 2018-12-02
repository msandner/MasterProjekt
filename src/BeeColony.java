import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony;
    private ArrayList<Integer[]> bestPaths;
    Fitness fitness;


    public BeeColony(int beeCount, Dataset dataset) throws IOException {
        this.beeCount = beeCount;
        colony = new ArrayList<>();
        bestPaths = new ArrayList<>();
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i, this, dataset));
        }
        fitness = new Fitness(dataset);
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

    public void addArrayToBestPath(Integer[] path) {
        bestPaths.add(path);
    }

    public void setBestPathsAtIndex(int index, Integer[] path) {
        bestPaths.set(index, path);
    }

    public void getTheBestPath() {
        ArrayList<Evaluable> ev = new ArrayList<>();
        for(int i = 0; i < bestPaths.size(); i++) {
            Path a = new Path(bestPaths.get(i));
            ev.add(a);
        }
        fitness.evaluate(ev);

    }

    public void printBestPaths() {
        for(int x = 0; x < bestPaths.size(); x++) {
            System.out.println(Arrays.deepToString(bestPaths.get(x)));
        }
    }
}
