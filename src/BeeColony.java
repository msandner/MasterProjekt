import com.hsh.Evaluable;
import com.hsh.Fitness;

import java.io.IOException;
import java.util.ArrayList;

public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony = new ArrayList<>();
    private static ArrayList<Integer[]> bestPaths= new ArrayList<>(280);


    public BeeColony(int beeCount) throws IOException {
        this.beeCount = beeCount;
        initializeBestPath();
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i));
        }
    }

    public BeeColony() {
        //initializeBestPath();
    }

    public Bee getBee(int index) {
        return colony.get(index);
    }


    public int danceDuration() {
        //ToDo
        return 0;
    }

    public static ArrayList<Integer[]> getBestPaths() {
        return bestPaths;
    }

    /*
    public void createNewPathList () {

        if(bestPaths != null) {
            bestPaths.clear();
        }
        bestPaths = new ArrayList<>(280);
    }
    */

    public void initializeBestPath() {
        Integer[] defaultPath = new Integer[1];

        for (int i=0; i < 280; ++i) {
            bestPaths.add(defaultPath);
        }
        int hello = 0;

    }
    public static void addArrayToBestPath(int index,Integer[] path) {
            bestPaths.add(index, path);

    }
    /*
    public void clearBestPaths() {
        bestPaths.clear();
    }
    */
}
