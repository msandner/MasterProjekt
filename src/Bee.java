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

        if(colony.getDefaultArray() == null) {
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
    }

    private void setInitialPath() {
        path = initializePath();
    }

    //initialer Pfad = zufällige Anordnung der Knoten
    private Integer[] initializePath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));

        //zu BestPath ArrayList hinzufügen
        colony.addArrayToBestPath(pathArray);

        //zu ResultPath ArrayList hinzufügen
        Path initPath = new Path(pathArray);
        colony.addPathToResultPaths(initPath);

        return pathArray;
    }

    //aus ArrayList einen zufälligen Pfad wählen
    private void observeDance() {
        //alle Pfade, die beobachtet werden können, aus BestPaths kriegen
        List<Integer[]> possiblePaths = colony.getBestPaths();

        Integer[] observedPath;
        ArrayList<Integer[]> betterPaths = new ArrayList<>();

        //alten Pfad evaluieren
        Path oldPath = new Path(path);
        fitness.evaluate(oldPath, -1);

        Path obsPath;
        for(int i = 0; i < possiblePaths.size(); i++) {
            //alle Pfade mit dem alten Pfad vergleichen, um die Besseren abzuspeichern
            observedPath = possiblePaths.get(i);
            obsPath = new Path(observedPath);
            fitness.evaluate(obsPath, -1);
            if (obsPath.getFitness() < oldPath.getFitness()) {
                betterPaths.add(observedPath);
            }
        }

        if(betterPaths.size() > 0) {
            //einen zufälligen Pfad aus allen besseren Pfaden auswählen
            int random = (int) (Math.random() * betterPaths.size());
            favouredPath = betterPaths.get(random);
        } else {
            //sonst alten Pfad behalten
            favouredPath = path.clone();
        }
    }

    private void setAllowedCities() {
        allowedCities = new Integer[cities];
        allowedCities = colony.getDefaultArray().clone();
        leftAllow = cities;
    }

    private void searchNewPath() {
        setAllowedCities();

        ArrayList<Integer> allowed = new ArrayList(colony.getDefaultArrayList());
        ArrayList<Double> randArray = new ArrayList();
        double result = 0.0;
        double foundProb = 0.0;

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
    }

    private double arcfitness(Node cityj, int i) {
        int AunionF = 0;

        //testen, ob der nächste Knoten im beobachteten Pfad auch in der Liste der besuchbaren Städte ist
        if(Arrays.asList(allowedCities).contains(favouredPath[i])) {
            AunionF = 1;
        }

        if ((cityj.getId() == favouredPath[i])) {
            //wenn der Knoten, auf den gerade getestet wird, derselber ist wie im beobachtetn Pfad, dann Lambda zurückgeben
            return bco.getLambda();
        } else if (leftAllow > 1) {
            //wenn noch nicht am letzten Knoten angekommen
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

        double result1 = arcfitness * distance;

        double result2 = 0.0;
        //Summe der (Distanz * Arcfitness) über alle möglichen Städte, die noch besucht werden können
        for (int j = 0; j < allowedCities.length; j++) {
            if (allowedCities[j] != -2 && cityi.getId() != allowedCities[j]) {
                Node n = dataset.getNodeByID(allowedCities[j]);
                result2 += arcfitness(n, i) * Math.pow(1.0/cityi.distance(n), bco.getBeta());
            }

        }
        double endresult = result1 / result2;

        return endresult;
    }

    //true, wenn der neue gefundene Pfad besser ist als der alte
    private boolean shouldBeeDance() {
        Path foundPath = new Path(newPath);
        Path oldPath = new Path(path);
        fitness.evaluate(foundPath, -1);
        fitness.evaluate(oldPath, -1);
        return (foundPath.getFitness() <= oldPath.getFitness());
    }

    //Path in ArrayList schreiben
    private void performWaggleDance() {
        if (shouldBeeDance()) {
            path = newPath.clone();
            colony.addPathToResultPaths(new Path(newPath));
            colony.addArrayToNewBestPaths(path);
        }
    }

    public Integer[] getPath() {
        return path;
    }
}
