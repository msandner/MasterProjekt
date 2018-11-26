import com.hsh.parser.Node;

import java.io.IOException;
import java.util.*;

public class Bee {

    private int ID;
    private int iteration;

    private BCO bco = new BCO();
    public BeeColony colony = new BeeColony();
    final int cities = bco.getCityCount();

    //favorisierter Pfad (durch Dance)
    private int favouredCityID = 0;
    private Integer[] favouredPath = new Integer[cities];

    //Set mit möglichen nächsten Städten A
    private Integer[] allowedCities;

    //Pfad mit bereits besuchten Städten
    private Integer[] newPath= new Integer[cities];
    int leftAllow = 280;

    private Integer[] path;

    public Bee(int ID) throws IOException {
        this.ID = ID;
        this.iteration = 0;
        this.path = new Integer[cities];
        setInitialPath();
        setAllowedCities();
    }

    public void setInitialPath() throws IOException {
        this.path = initializePath(cities);
    }

    public void mainProcedure() {
        if(iteration > 0) {
            observeDance();
        }
        searchNewPath();
        if(shouldBeeDance()) {
            performWaggleDance();
        }
    }

    //alle Städte sind von überall erreichbar oder nicht?
    public void setAllowedCities() {
        this.allowedCities = new Integer[cities];
        for (int i = 0; i < cities; i++) {
            allowedCities[i] = i + 1;
        }
    }

    public int getRemainingAllowedCities() {
        int counter = 0;
        for(int i = 0; i < allowedCities.length; i++) {
            if(!allowedCities[i].equals(-2))
                counter++;
        }
        return counter;
    }

    public static Integer[] initializePath(int cities) throws IOException {
        Integer[] patharray = new Integer[cities];
        for (int i = 0; i < cities; i++) {
            patharray[i] = i + 1;
        }
        Collections.shuffle(Arrays.asList(patharray));

        return patharray;
    }

    //aus ArrayList einen zufälligen Pfad wählen
    public Integer[] observedPath() {
        ArrayList<Integer[]> possiblePaths = colony.getBestPaths();
        Integer[] observedPath = new Integer[cities];

        //derzeit noch 0, da possiblePaths leer ist
        int random = (int) Math.random()*(possiblePaths.size()+1);

        observedPath = possiblePaths.get(random);

        return observedPath;
    }

    public void observeDance() {
        path = observedPath();
    }

    /*
    //Biene geht durch TSP und erstellt einen neuen Path mit berechneten Werten
    public Integer[] forageByTransRule() {
        double savedProb = 0.0;
        double newProb = 0.0;
        int bestPos;
        int hello = 0;

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

        for (int i = 0; i < (path.length-1); ++i) {
            savedProb = stateTransitionProbability(bco.getNodeByIDFromDataSet(newPath[i]), bco.getNodeByIDFromDataSet(path[i+1]), i);
            bestPos = -1;

            for (int j = 0; j < allowedCities.length; ++j) {
                if (allowedCities[j] != -2) {
                    newProb = stateTransitionProbability(bco.getNodeByIDFromDataSet(newPath[i]), bco.getNodeByIDFromDataSet(allowedCities[j]), i);

                    if (savedProb < newProb) {

                        for (int x = 0; x < allowedCities.length; x++) {
                            int a = allowedCities[x];
                            int b = path[i+1];
                            if (a == b && a != -2) {
                                bestPos = x;
                                break;
                            }
                        }
                    } else {
                        bestPos = j;
                    }
                } else if (j == 279){
                    hello = j;
                }
            }

            if(bestPos != -1) {
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
        return newPath;
    }
    */
    public Integer[] searchNewPath() {
        boolean isBetter = false;
        double bestProb = 0.0;
        double foundProb = 0.0;
        int bestNode = 0;

        //Der Startknoten des neuen Pfades ist auch der Startknoten den gespeicherten Pfades der Biene
        newPath[0] = path[0];

        //Der Startknoten wird in allowedCities als besucht markiert
        for (int x = 0; x < allowedCities.length; x++) {
            int a = allowedCities[x];
            int b = path[0];

            if (a == b) {
                allowedCities[x] = -2;
                leftAllow--;
                break;
            }
        }

        //Fülle den neuen Pfad newPath mit Knoten
        for(int i=0; i < (allowedCities.length-1); ++i) {
            bestNode = -1;
            bestProb = -1.0;
            for(int j=0; j < allowedCities.length; ++j) {
                if(allowedCities[j] != -2) {
                    if (newPath[i] == null) {
                        System.exit(-3);
                    }
                    foundProb = stateTransitionProbability(bco.getNodeByIDFromDataSet(newPath[i]), bco.getNodeByIDFromDataSet(allowedCities[j]), i);

                    if (bestNode == -1) {
                        bestNode = j;
                    }
                    if (bestProb == -1.0) {
                        bestProb = foundProb;
                    }
                    if (foundProb < bestProb) {
                        bestNode = j;
                    }
                } /*else if(j == 279) {
                    j=0;
                }*/
            }

            if(bestNode != -1) {
                newPath[i+1] = allowedCities[bestNode];
                allowedCities[bestNode] = -2;
                leftAllow--;
            }

        }
        return newPath;
    }

    /* city i = aktuelle Stadt
     * city j = Stadt mit der verglichen werden soll
     * noch nicht herausgefunden, wie t in die Gleichung mit einspielt*/
    public double arcfitness(Node cityi, Node cityj, int t) {
        /*
        * AunionF ist eigentlich immer 1, da alle Städte von überall erreicht werden können?
        int AunionF = 0;

        //wenn accessable Cities A und favoured City F eine gemeinsame Instanz haben, dann AunionF auf 1 Setzen
        if(allowedCities.contains(favouredCity)) {
            AunionF = 1;
        }
        */

        int AunionF = 1;

        if(cityj.getId() == favouredCityID+1) {
            return bco.getGamma();
        } else {
            double a = 1.0 - bco.getGamma() * AunionF;
            double b = leftAllow - AunionF;
            return a/b;
        }
    }

    //unter der Vermutung das die Summe von p_ij(t) = 1 ist
    public double stateTransitionProbability(Node cityi, Node cityj, int t) {

        if (cityi == null || cityj == null) {
            System.exit(-6);
        }
        double endresult = 0.0;
        double result1 = 0.0;
        double result2 = 0.0;
        double distance = 0.0;
        double arcfitness = 0.0;

        arcfitness = Math.pow(arcfitness(cityi, cityj, t), bco.getAlpha());

        distance= Math.pow(1.0/cityi.distance(cityj), bco.getBeta());

        result1 = arcfitness * distance;


        for(int i = 0; i < leftAllow; i++) {
            result2 += arcfitness * distance;
        }
        endresult = result1/result2;

        return endresult;
    }

    //Unter der Voraussetung, dass man von jedem Node zu jedem Node wechseln kann, wäre diese Methode überflüssig
    public boolean shouldBeeDance() {
        Path foundPath = new Path(newPath);
        Path oldPath = new Path(path);

        return (foundPath.getFitness() < oldPath.getFitness());
    }

    //Path in ArrayList schreiben
    public Path performWaggleDance() {
        Path betterPath = new Path(newPath);
        return betterPath;
    }

    public Integer[] getNewPath() {
        return newPath;
    }

    public Integer[] getPath() {
        return path;
    }
}
