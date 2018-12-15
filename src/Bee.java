import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import java.util.*;

public class Bee {

    private int ID;
    private int iteration;
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
        favouredPath = new Integer[cities];

        //Initialisiert ein Array und eine ArrayList mit IDs von 1 bis 280
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

        //zu BestPath ArrayList hinzufügen, damit die 0te Iteration observeDance verwenden kann
        colony.addArrayToBestPath(pathArray);
        //zu ResultPaths und NewBestPaths hinzufügen, damit diese ebenfalls evaluiert werden
        colony.addPathToResultPaths((new Path(pathArray)), pathArray);

        return pathArray;
    }

    //aus ArrayList einen zufälligen Pfad wählen, der als favouredPath genutzt wird
    private void observeDance() {
        //Holt die x besten Pfade aus den gefundenen Pfaden (sind nach ihrer Fitness sortiert)
        List<Integer[]> possiblePaths = colony.getBestPaths(10);

        //Wählt einen zufälligen Pfad aus den besten Pfaden aus
        int randValue = (int)(Math.random() * possiblePaths.size());
        favouredPath = possiblePaths.get(randValue);
    }

    private void setAllowedCities() {
        allowedCities = new Integer[cities];
        allowedCities = colony.getDefaultArray().clone();
        leftAllow = cities;
    }

    //Löscht das Objekt mit der ID element aus allowedCities und gibt den Index zurück, an dem das Objekt stand
    private int removeFromAllowedCities (int element) {
        int loopIndex = 0;
        int e;

        for (int i=0; i < allowedCities.length; ++i) {
            e = allowedCities[i];
            if(element == e) {
                allowedCities[i] = -2;
                leftAllow--;
                loopIndex = i;
                break;
            }
        }
        return loopIndex;
    }

    //Sucht basierend auf einem favorisiertem Pfad nach einem neuen Pfad
    public Path searchNewPath() {
        int count;
        int index;
        double randomValue;
        double randomProb;
        double totalProb;
        double foundProb;

        ArrayList<Double> probArray = new ArrayList();
        ArrayList<Integer> allowed = new ArrayList(colony.getDefaultArrayList());

        //Setzt allowedCities zurück auf den Startzustand (Sortierte Liste der größe cities)
        setAllowedCities();

        //Der Startknoten des neuen Pfades ist auch der Startknoten des gespeicherten Pfades der Biene
        newPath[0] = favouredPath[0];

        //Löscht den Startknoten aus den Listen der noch verfügbaren Städte
        int removeIndex = removeFromAllowedCities(newPath[0]);
        allowed.remove(removeIndex);

        //Fülle den neuen Pfad newPath mit Knoten
        for (int i = 0; i < (allowedCities.length-1); ++i) {
            probArray.clear();
            randomValue = Math.random();
            randomProb = 0.0;
            index = 0;
            totalProb = 0.0;
            count = 0;

            //Jeder Knoten wird mit allen noch verfügbaren Knoten verglichen
            for (int j = 0; j < allowedCities.length; ++j) {
                //Wenn ein Knoten in allowedCities den Wert -2 besitzt, wurde er bereits besucht
                if (allowedCities[j] != -2) {
                    //Berechne die Wahrscheinlichkeit für den Knoten j
                    foundProb = stateTransitionProbability(dataset.getNodeByID(newPath[i]), dataset.getNodeByID(allowedCities[j]), i+1);
                    //Alle Wahrscheinlichkeiten in einer Liste speichern
                    probArray.add(foundProb);
                    //Die Wahrscheinlichkeiten aufaddieren
                    totalProb += probArray.get(count);
                    count++;
                }
            }
            for (int z=0; z< probArray.size(); ++z) {
                //Die Wahrscheinlichkeiten zwischen 0 und 1 mappen
                probArray.set(z, probArray.get(z)/totalProb);
                //Die resultierenden Wahrscheinlichkeiten so lange aufaddieren bis sie den zufällig gewählten Wert übersteigen
                //Dies stellt die Zufälligkeit sicher, mit denen die Bienen ihre Pfade auswählen
                randomProb += probArray.get(z);
                if(randomProb >= randomValue) {
                    index = z;
                    break;
                }
            }
            //Fügt den neuen Knoten zum Pfad hinzu
            newPath[i+1] = allowed.get(index);
            //Löscht den besuchten Knoten aus den Listen der noch verfügbaren Städte
            removeFromAllowedCities(allowed.get(index));
            allowed.remove(index);
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
            //wenn der Knoten, der gerade getestet wird, der selbe ist wie im favouredPath, dann Lambda zurückgeben
            return bco.getLambda();
        } else if (leftAllow > 1) {
            //wenn nicht im favouredPath und noch nicht am letzten Knoten angekommen
            double a = 1.0 - bco.getLambda() * AunionF;
            double b = leftAllow - AunionF;
            return a / b;
        } else {
            //wenn am letzten Knoten angekommen
            return 1;
        }
    }

    //cityi = aktuelle Stadt, cityj = Stadt mit der verglichen werden soll
    //i = Position an der wird gerade stehen
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

    //gibt true zurück, wenn der gefundene Pfad eine bessere Fitness aufweist als der favorisierte Pfad mit dem die Biene begonnen hat
    private boolean shouldBeeDance(Path foundPath) {
        int foundProb = fitness.evaluate(foundPath, -1).getFitness();
        int oldProb = fitness.evaluate(new Path(favouredPath), -1).getFitness();

        return (foundProb <= oldProb);
    }

    //Path in ArrayList schreiben, wenn ein besserer gefunden worde
    public void performWaggleDance(Path foundPath) {
        if (shouldBeeDance(foundPath)) {
            //Setzt den favorisierten Pfad auf den neu gefundenen Pfad
            favouredPath = newPath.clone();
            colony.addPathToResultPaths(foundPath, favouredPath);
        }
    }

    public Integer[] getPath() {
        return favouredPath;
    }
}
