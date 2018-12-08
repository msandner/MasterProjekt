import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;

import java.util.*;

public class Bee {

    private int ID;
    private int iteration;
    private Integer[] path;
    private final int cities;
    private BeeColony colony;
    private Fitness fitness;
    private Dataset dataset;
    private double insArc;

    private BCO bco = new BCO();

    //favorisierter Pfad
    private Integer[] favouredPath;
    //Set mit möglichen nächsten Städten A
    private Integer[] allowedCities;
    private int leftAllow;
    //Pfad mit bereits besuchten Städten -> am Ende der neue gefundene Pfad
    private Integer[] newPath;

    public Bee(int ID, BeeColony colony, Dataset dataset) {
        this.ID = ID;
        this.colony = colony;
        this.dataset = dataset;

        fitness = new Fitness(dataset, false);
        cities = dataset.getSize();

        iteration = 0;
        path = new Integer[cities];

        if(colony.defaultArray == null) {
            colony.setDefaultArray(cities);
        }

        setInitialPath();
        setAllowedCities();

        newPath = new Integer[cities];
    }

    public void mainProcedure() {
        observeDance();
        searchNewPath();
        performWaggleDance();
        //printFitnessOfPath();
    }

    private void setInitialPath() {
        path = initializePath();
    }

    private Integer[] initializePath() {
        Integer[] pathArray = colony.defaultArray.clone();
        Collections.shuffle(Arrays.asList(pathArray));
        colony.addArrayToBestPath(pathArray);

        Path initPath = new Path(pathArray);
        colony.addPathToResultPaths(initPath);

        return pathArray;
    }

    private void observeDance() {
        favouredPath = observedPath();
    }

    //aus ArrayList einen zufälligen Pfad wählen
    public Integer[] observedPath() {
        List<Integer[]> possiblePaths = colony.getBestPaths();

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
        } else {
            return path;
        }
    }

    //alle Städte sind von überall erreichbar oder nicht?
    private void setAllowedCities() {
        allowedCities = new Integer[cities];

        allowedCities = colony.defaultArray.clone();
        leftAllow = cities;
    }

    private Integer[] searchNewPath() {
        setAllowedCities();

        ArrayList<Integer> allowed = new ArrayList(colony.defaultArrayList);
        ArrayList<Double> randArray = new ArrayList();
        double bestProb = 0.0;
        double result = 0.0;
        double foundProb = 0.0;
        int bestNode = 0;

        //Der Startknoten des neuen Pfades ist auch der Startknoten des gespeicherten Pfades der Biene
        newPath[0] = favouredPath[0];

        //Der Startknoten wird in allowedCities als besucht markiert
        for (int x = 0; x < allowedCities.length; x++) {
            if (allowedCities[x] != -2) {
                int a = allowedCities[x];
                int b = newPath[0];

                if (a == b) {
                    allowedCities[x] = -2;
                    allowed.remove(x);
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
                    randArray.add(foundProb);

                    /*
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
                    */
                }
            }

            for (int x=0; x<randArray.size(); ++x) {
              result += randArray.get(x);
            }
            //System.out.println("randArray:" +result);

            for (int y=0; y< randArray.size(); ++y) {
                randArray.set(y, randArray.get(y)/result);
                //System.out.println(randArray.get(y));
            }
            result = 0.0;
            double rand = Math.random();
            double randResult = 0.0;
            int index = 0;
            for (int z=0; z< randArray.size(); ++z) {

                randResult += randArray.get(z);
                //System.out.println(rand + "   " + randResult);
                if(randResult >= rand) {
                    index = z;
                    break;
                }
            }
            randArray.clear();
            //System.out.println("Arcfitness:" +insArc);
            insArc = 0;
            //System.out.println(index);
                newPath[i + 1] = allowed.get(index);

                for (int a=0; a<allowedCities.length; ++a) {
                    int d = allowed.get(index);
                    int e = allowedCities[a];
                    if(d == e) {
                        allowedCities[a] = -2;
                        break;
                    }
                }

                //allowedCities[allowed.get(index) - 1] = -2;
                allowed.remove(index);
                leftAllow--;

        }

        //System.out.println(Arrays.equals(favouredPath, newPath));
        //System.out.println(Arrays.toString(favouredPath));
        //System.out.println(Arrays.toString(newPath));
        iteration++;
        return newPath;
    }

    private double arcfitness(Node cityj, int i) {
        int AunionF = 0;

        /*
        if(Arrays.stream(allowedCities).anyMatch(favouredPath[i]::equals)) {
            AunionF = 1;
        }
        */

        for(int j = 0; j < allowedCities.length; j++) {
            if(allowedCities[j] == favouredPath[i]) {
                AunionF = 1;
                break;
            }
        }


        if ((cityj.getId() == favouredPath[i])) {
            return bco.getLambda();
        } else if (leftAllow > 1) {
            double a = 1.0 - bco.getLambda() * AunionF;
            double b = leftAllow - AunionF;
            return a / b;
        } else {
            return 1;
        }
    }

    //cityi = aktuelle Stadt, cityj = Stadt mit der verglichen werden soll
    private double stateTransitionProbability(Node cityi, Node cityj, int i) {
        double arcfitness = Math.pow(arcfitness(cityj, i), bco.getAlpha());
        double distance = Math.pow(1.0/cityi.distance(cityj), bco.getBeta());

        //System.out.println(distance);
        double result1 = arcfitness * distance;


        insArc += arcfitness;

        double result2 = 0.0;
        double result3 = 0.0;

        for (int j = 0; j < allowedCities.length; j++) {
            if (allowedCities[j] != -2 && cityi.getId() != allowedCities[j]) {
                result2 += arcfitness(dataset.getNodeByID(allowedCities[j]), i);
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
        return (foundPath.getFitness() <= oldPath.getFitness());
    }

    //Path in ArrayList schreiben
    private void performWaggleDance() {
        if (shouldBeeDance()) {
            path = newPath;
            colony.addPathToResultPaths(new Path(newPath));
            colony.addArrayToNewBestPaths(path);
        }
    }

    public Integer[] getPath() {
        return path;
    }

    private void printFitnessOfPath() {
        ArrayList<Evaluable> ev = new ArrayList<>();
        Path path = new Path(newPath);
        ev.add(path);
        fitness.evaluate(ev);
        System.out.println(path.getFitness());
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
