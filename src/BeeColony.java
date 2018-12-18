import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import java.util.ArrayList;


class BeeColony {

    private ArrayList<Bee> beeColony;
    //Pfade die in die nächste Iteration eingehen, um für die Bienen als favouredPath zu dienen
    private ArrayList<Integer[]> bestPaths;
    //Pfade die von den Bienen gefunden werden
    private ArrayList<Integer[]> foundPaths;
    //Pfade die zur Evaluation gegeben werden
    private ArrayList<Path> resultPaths;
    //default Array mit sortierten IDs der Größe cities
    private Integer[] defaultArray;
    private ArrayList<Integer> defaultArrayList;

    private Fitness fitness;

    BeeColony(int beeCount, Dataset dataset) {
        beeColony = new ArrayList<>();
        bestPaths = new ArrayList<>();
        foundPaths = new ArrayList<>();
        resultPaths = new ArrayList<>();
        fitness = new Fitness(dataset, false);
        double[][] distanceMatrix = createDistanceMatrix(dataset);
        //Bienen und Scouts hinzufügen
        for (int i = 0; i < beeCount; i++) {
            beeColony.add(new Bee(i, this, dataset, distanceMatrix));
            //Scouts hinzufügen
            if(i <= (beeCount/2)) {
                beeColony.add(new Scout(i, this, dataset, distanceMatrix));
            }
        }
    }

    private double[][] createDistanceMatrix(Dataset dataset) {
        double[][] distanceMatrix = new double[dataset.getSize()+1][dataset.getSize()+1];
        for(int i = 1; i <= dataset.getSize(); i++) {
            for(int j = 1; j <= dataset.getSize(); j++) {
                distanceMatrix[i][j] = dataset.getNodeByID(i).distance(dataset.getNodeByID(j));
            }
        }
        return distanceMatrix;
    }

    //Setzt foundPaths auf die x besten Pfade, damit die Liste nicht immer weiter wächst
    private void shrinkFoundPaths(int listSize) {
        ArrayList<Integer[]> shrinkedList = new ArrayList<>(foundPaths.subList(0,listSize));
        foundPaths.clear();
        foundPaths = shrinkedList;
    }

    //Setzt das Array aus dem die Bienen der nächsten Iteration ihre favorisierten Pfade beziehen auf die x besten Pfade die gefunden wurden
    private void setBestPathsToFoundPaths() {
        bestPaths.clear();
        bestPaths.addAll(foundPaths);
    }

    void addArrayToBestPath(Integer[] path) {
        bestPaths.add(path);
    }

    //Fügt den gefundenen Pfad in eine nach der Fitness sortierten Liste ein
    void addPathToResultPaths(Path foundPath, Integer[] pathAsInt) {
        //Alle neuen Pfade der Bienen in die zu evaluierende Liste schreiben
        //Notwendig, damit Bienen nicht bereits auf die neu gefunden Pfade der vorherigen Bienen der gleichen Iteration zugreifen können
        Path indexPath;
        int loopCount = 0;

        //Pfad für spätere Verwendung evaluieren
        fitness.evaluate(foundPath, -1);

        if(resultPaths.size() == 0) {
            //Falls in der Liste noch nichts drin steht, den gefundenen Pfad einfach einfügen
            resultPaths.add(foundPath);
            foundPaths.add(pathAsInt);
        } else {
            // Sonst suche die Position an der der Pfad gespeichert werden soll
            loopCount = resultPaths.size();
            for (int i = 0; i < loopCount; ++i) {
                indexPath = resultPaths.get(i);
                //Der gefundene Pfad wird vor den ersten Pfad gespeichert, der eine schlechtere Fitness hat
                if (foundPath.getFitness() < indexPath.getFitness()) {
                    resultPaths.add(i, foundPath);
                    foundPaths.add(i, pathAsInt);
                    break;
                }
            }
            //Falls der gefundene Pfad schlechter ist als alle anderen gefundenen, füge ihn am Ende der Liste ein
            if(loopCount == resultPaths.size()) {
                resultPaths.add(foundPath);
                foundPaths.add(pathAsInt);
            }
        }
    }

    //Gibt die ersten x Pfade aus resultPaths zurück
    ArrayList<Evaluable> getResultPathsAsEvaluable(int listSize) {
        ArrayList<Evaluable> ev = new ArrayList<>();

        //Wenn weniger Pfade zurückgegeben werden sollen, als in resultPaths drin stehen, werden nur die ersten x (x=listSize) Pfade zurückgegeben
        if(listSize < resultPaths.size()) {
            ArrayList<Path> results = new ArrayList<>(resultPaths.subList(0, listSize));
            ev.addAll(results);
            //Setzt resultPaths aus die Teilliste, damit resultPaths nicht zu groß wird
            resultPaths.clear();
            resultPaths.addAll(results);
            //Setzt das Array aus dem die Bienen der nächsten Iteration ihre favorisierten Pfade beziehen auf die x besten Pfade die gefunden wurden
            shrinkFoundPaths(listSize);
            setBestPathsToFoundPaths();
        } else {
            //Falls in resultPaths nicht so viele Pfade drinstehen, wie angefordert wurde, wird das zurückgegeben was in der Liste drinsteht
            ev.addAll(resultPaths);
            setBestPathsToFoundPaths();
        }
        return ev;
    }

    //Erstellt sowohl eine sortierte Liste als auch ein sortiertes Array mit den IDs von 0 bis cities
    void setDefaultArray(int cities) {
        defaultArray = new Integer[cities];
        defaultArrayList = new ArrayList<>();

        for (int i=0; i<cities; ++i) {
            defaultArray[i] = i+1;
            defaultArrayList.add(i+1);
        }
    }

    //Gibt die besten Pfade von Index 0 bis Index size zurück
    //Funktioniert, da bestPaths nach der Fitness sortiert ist
    ArrayList<Integer[]> getXBestPaths(int size) {
        //Falls size kleiner ist, als die Anzahl an Elementen die in bestPaths drin steht:
        //Gebe Teilliste der Größe size zurück
        if (size < bestPaths.size()) {
            return (new ArrayList<>(bestPaths.subList(0, size)));
            //Sonst gebe das komplette Array zurück
        } else {
            return bestPaths;
        }
    }

    Integer[] getDefaultArray() {
        return defaultArray;
    }

    ArrayList<Integer> getDefaultArrayList() {
        return defaultArrayList;
    }

    Bee getBee(int index) {
        return beeColony.get(index);
    }

    ArrayList<Path> getResultPaths() {
        return  resultPaths;
    }



}