import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import java.io.IOException;
import java.util.ArrayList;


public class BeeColony {

    private ArrayList<Bee> colony;

    //Pfade die in die nächste Iteration eingehen, um für die Bienen als favouredPath zu dienen
    private ArrayList<Integer[]> bestPaths;

    //Pfade die von den Bienen gefunden werden
    private ArrayList<Integer[]> newBestPaths = new ArrayList<>();

    //Pfade die zur Evaluation gegeben werden
    private ArrayList<Path> resultPaths = new ArrayList<>();

    //default Array mit sortierten IDs der Größe cities
    private Integer[] defaultArray;
    private ArrayList<Integer> defaultArrayList;

    private Fitness fitness;

    public BeeColony(int beeCount, Dataset dataset) {
        colony = new ArrayList<>();
        bestPaths = new ArrayList<>();
        newBestPaths = new ArrayList<>();
        fitness = new Fitness(dataset, false);
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i, this, dataset));
        }

    }

    public void clearNewBestPaths(int listSize) {
        ArrayList<Integer[]> shrinkedList = new ArrayList<>(newBestPaths.subList(0,listSize));
        newBestPaths = shrinkedList;
    }

    public void setBestPathsToNewBestPaths() {
        bestPaths.clear();
        bestPaths.addAll(newBestPaths);
    }

    public void addArrayToBestPath(Integer[] path) {
        bestPaths.add(path);
    }

    /*
    public void addArrayToNewBestPaths(Integer[] path) {
        newBestPaths.add(path);
    }
    */

    //Fügt den gefundenen Pfad in eine nach der Fitness sortierten Liste ein
    public void addPathToResultPaths (Path foundPath, Integer[] pathAsInt) {
        //Alle neuen Pfade der Bienen in die zu evaluierende Liste schreiben
        //Notwendig, damit Bienen nicht bereits auf die neu gefunden Pfade der vorherigen Bienen der gleichen Iteration zugreifen können
        //setBestPathsToNewBestPaths();
        //clearNewBestPaths();

        Path indexPath;

        //Pfad für spätere Verwendung evaluieren
        fitness.evaluate(foundPath, -1);

        //Falls in der Liste noch nichts drin steht, den gefundenen Pfad einfach einfügen
        if(resultPaths.size() == 0) {
            resultPaths.add(foundPath);
            newBestPaths.add(pathAsInt);
            // Sonst suche die Position an der der Pfad gespeichert werden soll
        } else {
            int loopCount = resultPaths.size();
            for (int i = 0; i < loopCount; ++i) {
                indexPath = resultPaths.get(i);

                if (foundPath.getFitness() < indexPath.getFitness()) {
                    resultPaths.add(i, foundPath);
                    newBestPaths.add(i, pathAsInt);
                    break;
                }
            }
            //Falls der gefundene Pfad schlechter ist als alle anderen gefundenen, füge ihn am Ende der Liste ein
            if(loopCount == resultPaths.size()) {
                resultPaths.add(foundPath);
                newBestPaths.add(pathAsInt);
            }
        }
    }

    public ArrayList<Evaluable> getResultPathsAsEvaluable(int listSize) {

        ArrayList<Evaluable> ev = new ArrayList<>();

        if(listSize < resultPaths.size()) {
            ArrayList<Path> results = new ArrayList<>(resultPaths.subList(0, listSize));
            ev.addAll(results);
            resultPaths = results;

            clearNewBestPaths(listSize);
            setBestPathsToNewBestPaths();

        } else {
            ev.addAll(resultPaths);
        }

        return ev;
    }

    public void setDefaultArray(int cities) {
        defaultArray = new Integer[cities];
        defaultArrayList = new ArrayList<>();

        for (int i=0; i<cities; ++i) {
            defaultArray[i] = i+1;
            defaultArrayList.add(i+1);
        }
    }

    public Integer[] getDefaultArray() {
        return defaultArray;
    }

    public ArrayList<Integer> getDefaultArrayList() {
        return defaultArrayList;
    }

    public Bee getBee(int index) {
        return colony.get(index);
    }

    public ArrayList<Integer[]> getBestPaths(int size) {
        if (size < bestPaths.size()) {
            return (new ArrayList<>(bestPaths.subList(0, size)));
        } else {
            return bestPaths;
        }
    }

}