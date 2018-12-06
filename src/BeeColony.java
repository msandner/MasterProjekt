import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import java.io.IOException;
import java.util.ArrayList;


public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony;
    private ArrayList<Integer[]> bestPaths;
    private ArrayList<Integer[]> newBestPaths;
    Fitness fitness;

    public BeeColony(int beeCount, Dataset dataset) throws IOException {
        this.beeCount = beeCount;
        colony = new ArrayList<>();
        bestPaths = new ArrayList<>();
        newBestPaths = new ArrayList<>();
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i, this, dataset));
        }
        fitness = new Fitness(dataset);
    }

    public Bee getBee(int index) {
        return colony.get(index);
    }

    public ArrayList<Integer[]> getBestPaths() {
        return bestPaths;
    }

    public ArrayList<Integer[]> getNewBestPaths() {
        return newBestPaths;
    }

    public ArrayList<Evaluable> getBestPathsAsEvaluable() {
        ArrayList<Evaluable> ev = new ArrayList<>();

        //Alle neuen Pfade der Bienen in die zu evaluierende Liste schreiben
        //Notwendig, damit Bienen nicht bereits auf die neu gefunden Pfade der vorherigen Bienen der gleichen Iteration zugreifen können
        setBestPathsToNewBestPaths();
        clearNewBestPaths();


        for(int i = 0; i < bestPaths.size(); i++) {
            Path a = new Path(bestPaths.get(i));
            ev.add(a);
        }
        return ev;
    }

    public void addArrayToBestPath(Integer[] path) {
        bestPaths.add(path);
    }

    public void addArrayToNewBestPaths(Integer[] path) {
        newBestPaths.add(path);
    }

    public void clearNewBestPaths() {
        newBestPaths.clear();
        newBestPaths = new ArrayList<>();
    }

    public void setBestPathsToNewBestPaths() {
        ArrayList<Integer[]> bestArray = new ArrayList<>();

        for( int i=0; i <= bestPaths.size(); ++i) {

        }

        bestPaths.clear();
        bestPaths.addAll(newBestPaths);
    }

    public int danceDuration() {
        //ToDo
        return 0;
    }
}
