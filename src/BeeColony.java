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
    private ArrayList<Integer[]> newBestPaths;

    //Pfade die zur Evaluation gegeben werden
    private ArrayList<Path> resultPaths = new ArrayList<>();

    //default Array mit sortierten IDs der Größe cities
    private Integer[] defaultArray;
    private ArrayList<Integer> defaultArrayList;

    private Fitness fitness;

    public BeeColony(int beeCount, Dataset dataset) throws IOException {
        colony = new ArrayList<>();
        bestPaths = new ArrayList<>();
        newBestPaths = new ArrayList<>();
        fitness = new Fitness(dataset, false);
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i, this, dataset));
        }

    }

    public void clearNewBestPaths() {
        newBestPaths.clear();
        newBestPaths = new ArrayList<>();
    }

    public void setBestPathsToNewBestPaths() {
        bestPaths.clear();
        bestPaths.addAll(newBestPaths);
    }

    public void addArrayToBestPath(Integer[] path) {
        bestPaths.add(path);
    }

    public void addArrayToNewBestPaths(Integer[] path) {
        newBestPaths.add(path);
    }

    //Fügt den gefundenen Pfad in eine nach der Fitness sortierten Liste ein
    public void addPathToResultPaths (Path foundPath) {
        //Alle neuen Pfade der Bienen in die zu evaluierende Liste schreiben
        //Notwendig, damit Bienen nicht bereits auf die neu gefunden Pfade der vorherigen Bienen der gleichen Iteration zugreifen können
        setBestPathsToNewBestPaths();
        clearNewBestPaths();

        ArrayList<Evaluable> ev = new ArrayList<>();
        Path indexPath;

        //Pfad für spätere Verwendung evaluieren
        ev.add(foundPath);
        fitness.evaluate(ev);


        //Falls in der Liste noch nichts drin steht, den gefundenen Pfad einfach einfügen
        if(resultPaths.size() == 0) {
            resultPaths.add(foundPath);
            // Sonst suche die Position an der der Pfad gespeichert werden soll
        } else {
            int loopCount = resultPaths.size();
            for (int i = 0; i < loopCount; ++i) {
                indexPath = resultPaths.get(i);

                if (foundPath.getFitness() < indexPath.getFitness()) {
                    resultPaths.add(i, foundPath);
                    break;
                }
            }
            //Falls der gefundene Pfad schlechter ist als alle anderen gefundenen, füge ihn am Ende der Liste ein
            if(loopCount == resultPaths.size()) {
                resultPaths.add(foundPath);
            }
        }
    }

    //Es werden nur die x besten Pfade zur Evaluation geschickt
    //Sollte resultPaths größer werden als x, werden die Pfade die hinten stehen einfach gelöscht
    //Da die Liste nach der Fitness sortiert ist, sind die Pfade die man löscht, die mit der schlechtesten Fitness
    public ArrayList<Path> shrinkList(ArrayList<Path> list, int size) {
        ArrayList<Path> shrinkedList = new ArrayList<>(list.subList(0, size));
        return shrinkedList;
    }

    public ArrayList<Evaluable> getResultPathsAsEvaluable(int listSize) {
        ArrayList<Evaluable> ev = new ArrayList<>();

        if(listSize < resultPaths.size()) {
            ArrayList<Path> results = new ArrayList<>(resultPaths.subList(0, listSize));
            ev.addAll(results);
            resultPaths = results;
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

    public ArrayList<Integer[]> getBestPaths() {
        return bestPaths;
    }

}