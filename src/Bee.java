import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import java.util.*;

public class Bee {

    private int ID;
    private int iteration;

    //favorisierter Pfad
    private Integer[] favouredPath;

    private final int cities;
    private BeeColony colony;
    private Fitness fitness;
    private Dataset dataset;

    private BCO bco = new BCO();

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
        favouredPath = new Integer[cities];

        if(colony.getDefaultArray() == null) {
            colony.setDefaultArray(cities);
        }

        setInitialPath();
        setAllowedCities();

        newPath = new Integer[cities];
    }

    public void mainProcedure() {
        observeDance();
        performWaggleDance(searchNewPath());
    }

    private void setInitialPath() {
        favouredPath = initializePath();
    }

    //initialer Pfad = zufällige Anordnung der Knoten
    private Integer[] initializePath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));

        //zu BestPath ArrayList hinzufügen
        colony.addArrayToBestPath(pathArray);

        //zu ResultPaths und NewBestPaths hinzufügen
        Path initPath = new Path(pathArray);
        colony.addPathToResultPaths(initPath, pathArray);

        return pathArray;
    }

    //aus ArrayList einen zufälligen Pfad wählen
    private void observeDance() {
        //alle Pfade, die beobachtet werden können, aus BestPaths kriegen
        List<Integer[]> possiblePaths = colony.getBestPaths(10);

        int randValue = (int)(Math.random() * possiblePaths.size());
        favouredPath = possiblePaths.get(randValue);
    }

    private void setAllowedCities() {
        allowedCities = new Integer[cities];
        allowedCities = colony.getDefaultArray().clone();
        leftAllow = cities;
    }

    private Path searchNewPath() {
        setAllowedCities();
        int count;
        double rand;
        double randResult;
        int index;


        ArrayList<Integer> allowed = new ArrayList(colony.getDefaultArrayList());
        ArrayList<Double> randArray = new ArrayList();

        double result = 0.0;
        double foundProb;

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
            count = 0;
            for (int j = 0; j < allowedCities.length; ++j) {
                if (allowedCities[j] != -2) {
                    foundProb = stateTransitionProbability(dataset.getNodeByID(newPath[i]), dataset.getNodeByID(allowedCities[j]), i+1);
                    randArray.add(foundProb);
                    result += randArray.get(count);
                    count++;
                }
            }

            rand = Math.random();
            randResult = 0.0;
            index = 0;

            for (int z=0; z< randArray.size(); ++z) {
                randArray.set(z, randArray.get(z)/1.0);
                randResult += randArray.get(z);
                if(randResult >= rand) {
                    index = z;
                    break;
                }
            }

            result = 0.0;
            randArray.clear();

            newPath[i+1] = allowed.get(index);


            for (int a=0; a<allowedCities.length; ++a) {
                int d = allowed.get(index);
                int e = allowedCities[a];
                if(d == e) {
                    allowedCities[a] = -2;
                    break;
                }
            }

            allowed.remove(index);
            leftAllow--;
        }

        iteration++;

        return (new Path(newPath));

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
        Path oldPath = new Path(favouredPath);

        fitness.evaluate(foundPath, -1);
        fitness.evaluate(oldPath, -1);

        return (foundPath.getFitness() <= oldPath.getFitness());
    }

    //Path in ArrayList schreiben
    private void performWaggleDance(Path foundPath) {
        if (shouldBeeDance()) {
            favouredPath = newPath.clone();
            colony.addPathToResultPaths(foundPath, favouredPath);
        }
    }

    public Integer[] getPath() {
        return favouredPath;
    }
}
