import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import java.io.IOException;
import java.util.*;

public class Bee {

    private int ID;

    private int iteration;

    private BCO bco = new BCO();

    private final int cities;

    //favorisierter Pfad
    private Integer[] favouredPath;

    //Set mit möglichen nächsten Städten A
    private Integer[] allowedCities;

    //Pfad mit bereits besuchten Städten -> am Ende der neue gefundene Pfad
    private Integer[] newPath;

    private int leftAllow;

    private static Integer[] path;

    private BeeColony colony;

    private Fitness fitness;

    private Dataset dataset;

    int favouredPathID = 0;


    public Bee(int ID, BeeColony colony, Dataset dataset) throws IOException {
        this.ID = ID;
        this.colony = colony;
        this.dataset = dataset;
        fitness = new Fitness(dataset, false);
        leftAllow = dataset.getSize();
        cities = dataset.getSize();
        iteration = 0;
        path = new Integer[cities];
        setInitialPath();
        setAllowedCities();
        favouredPath = path;
        newPath = new Integer[cities];
    }

    public int getID() {
        return ID;
    }

    private void setInitialPath() {
        path = initializePath(cities);
    }

    public void mainProcedure() {
        observeDance();
        searchNewPath();
        performWaggleDance();
    }

    //alle Städte sind von überall erreichbar oder nicht?
    private void setAllowedCities() {
        allowedCities = new Integer[cities];
        for (int i = 0; i < cities; i++) {
            allowedCities[i] = i + 1;
        }
        leftAllow = cities;
    }

    private Integer[] initializePath(int cities) {
        Integer[] pathArray = new Integer[cities];
        for (int i = 0; i < cities; i++) {
            pathArray[i] = i + 1;
        }
        Collections.shuffle(Arrays.asList(pathArray));
        colony.addArrayToBestPath(pathArray);
        return pathArray;
    }

    //aus ArrayList einen zufälligen Pfad wählen
    public Integer[] observedPath() {
        ArrayList<Integer[]> possiblePaths = colony.getBestPaths();

        Integer[] observedPath;
        ArrayList<Integer[]> betterPaths = new ArrayList<>();
        Path oldPath = new Path(path);
        Path obsPath;

        ArrayList<Evaluable> ev = new ArrayList<>();

        for(int i = 0; i < possiblePaths.size(); i++) {
            ev.clear();
            observedPath = possiblePaths.get(i);
            obsPath = new Path(observedPath);
            ev.add(oldPath);
            ev.add(obsPath);
            fitness.evaluate(ev);

            if (obsPath.getFitness() < oldPath.getFitness()) {
                betterPaths.add(observedPath);
            }
        }

        if(betterPaths.size() > 0) {
            int random = (int) (Math.random() * betterPaths.size());
            return betterPaths.get(random);
        }
        else {
            return path;
        }
    }

    private void observeDance() {
        favouredPath = observedPath();
        path = favouredPath;
    }

    private Integer[] searchNewPath() {
        setAllowedCities();

        double bestProb = 0.0;
        double foundProb = 0.0;
        int bestNode = 0;

        //Der Startknoten des neuen Pfades ist auch der Startknoten den gespeicherten Pfades der Biene
        newPath[0] = favouredPath[0];

        //Der Startknoten wird in allowedCities als besucht markiert
        for (int x = 0; x < allowedCities.length; x++) {
            if (allowedCities[x] != -2) {
                int a = allowedCities[x];
                int b = ID;

                if (a == b) {
                    allowedCities[x] = -2;
                    leftAllow--;
                    break;
                }
            }
        }

        //Fülle den neuen Pfad newPath mit Knoten
        for (int i = 0; i < (allowedCities.length-1); ++i) {
            bestNode = -1;
            bestProb = -1;

            for (int j = 0; j < allowedCities.length; ++j) {
                if (allowedCities[j] != -2) {
                    foundProb = stateTransitionProbability(dataset.getNodeByID(newPath[i]), dataset.getNodeByID(allowedCities[j]), i+1);

                    if (bestNode == -1) {
                        bestNode = j;
                    }
                    if (bestProb == -1.0) {
                        bestProb = foundProb;
                    }
                    if (foundProb >= bestProb) {
                        bestNode = j;
                        bestProb = foundProb;
                    }
                }
            }

            if (bestNode != -1) {
                newPath[i + 1] = allowedCities[bestNode];
                allowedCities[bestNode] = -2;
                leftAllow--;
            }
        }

        iteration++;
        return newPath;
    }

    private double arcfitness(Node cityj, int i) {
        int AunionF = 1;

        if ((cityj.getId() == favouredPath[i])) {
            return bco.getGamma();
        } else if (leftAllow > 1) {
            double a = 1.0 - bco.getGamma() * AunionF;
            double b = leftAllow - AunionF;
            return a / b;
        } else {
            return 1;
        }
    }

    //cityi = aktuelle Stadt, cityj = Stadt mit der verglichen werden soll
    private double stateTransitionProbability(Node cityi, Node cityj, int i) {
        double arcfitness = Math.pow(arcfitness(cityj, i), bco.getAlpha());
        double distance = Math.pow(1.0 / cityi.distance(cityj), bco.getBeta());

        double result1 = arcfitness * distance;

        double result2 = 0.0;
        double result3 = 0.0;
        for (Integer allowedCity : allowedCities) {
            if (allowedCity != -2 && cityi.getId() != allowedCity) {
                result2 += arcfitness(bco.getNodeByIDFromDataSet(allowedCity), i);
                result3 += result2 * distance;
            }

        }
        double endresult = result1 / result3;

        return endresult;
    }

    private boolean shouldBeeDance() {
        Path foundPath = new Path(newPath);
        Path oldPath = new Path(path);

        ArrayList<Evaluable> ev = new ArrayList<>();
        ev.add(foundPath);
        ev.add(oldPath);
        fitness.evaluate(ev);
        return (foundPath.getFitness() < oldPath.getFitness());
    }

    //Path in ArrayList schreiben
    private void performWaggleDance() {
        if (shouldBeeDance()) {
            path = newPath;
            colony.setBestPathsAtIndex(ID, path);
        }
    }

    public Integer[] getPath() {
        return path;
    }

    /*
    //Biene geht durch TSP und erstellt einen neuen Path mit berechneten Werten
    public Integer[] forageByTransRule() {
        double savedProb = 0.0;
        double newProb = 0.0;
        int bestPos;

        newPath[0] = path[0];

        //Lösche die erste Stadt aus allowedCities raus, damit sie nicht zweimal besucht wird
        for (int x = 0; x < allowedCities.length; x++) {
            int a = allowedCities[x];
            int b = path[0];

            if (a == b) {
                allowedCities[x] = -2;
                leftAllow--;
                break;
            }
        }

        for (int i = 0; i < (path.length - 1); ++i) {
            savedProb = stateTransitionProbability(bco.getNodeByIDFromDataSet(newPath[i]), bco.getNodeByIDFromDataSet(path[i + 1]), i);
            bestPos = -1;

            for (int j = 0; j < allowedCities.length; ++j) {
                if (allowedCities[j] != -2) {
                    newProb = stateTransitionProbability(bco.getNodeByIDFromDataSet(newPath[i]), bco.getNodeByIDFromDataSet(allowedCities[j]), i);

                    if (savedProb < newProb) {

                        for (int x = 0; x < allowedCities.length; x++) {
                            int a = allowedCities[x];
                            int b = path[i + 1];
                            if (a == b && a != -2) {
                                bestPos = x;
                                break;
                            }
                        }
                    } else {
                        bestPos = j;
                    }
                }

                if (bestPos != -1) {
                    //int q = allowedCities[bestPos];
                    //if(q != -2) {
                    //Beste Position in newPath speichern
                    newPath[i + 1] = allowedCities[bestPos];
                    //Löscht die besuchte Stadt um doppeltes Besuchen zu verhindern
                    allowedCities[bestPos] = -2;
                    leftAllow--;
                } else {
                    System.exit(-3);
                }

            }

        }
        return newPath;
    }*/
}
