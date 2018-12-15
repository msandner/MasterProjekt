import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import java.util.*;

public class Bee {

    public int ID;
    public int iteration;
    public final int cities;
    public BeeColony colony;
    public Fitness fitness;
    public Dataset dataset;

    public BCO bco = new BCO();

    //favorisierter Pfad
    public Integer[] favouredPath;

    //Set mit möglichen nächsten Städten A
    public Integer[] allowedCities;
    public int leftAllow;

    //Pfad mit bereits besuchten Städten -> am Ende der neue gefundene Pfad
    public Integer[] newPath;

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

    public void setInitialPath() {
        favouredPath = initializePath();
    }

    //initialer Pfad = zufällige Anordnung der Knoten
    public Integer[] initializePath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));

        //zu BestPath ArrayList hinzufügen, damit die 0te Iteration observeDance verwenden kann
        colony.addArrayToBestPath(pathArray);
        //zu ResultPaths und NewBestPaths hinzufügen, damit diese ebenfalls evaluiert werden
        colony.addPathToResultPaths((new Path(pathArray)), pathArray);

        return pathArray;
    }

    //aus ArrayList einen zufälligen Pfad wählen
    public void observeDance() {
        //Holt x besten Pfade aus den gefunden Pfaden
        //Funktioniert, da die Pfade nach ihrer Fitness sortiert sind
        List<Integer[]> possiblePaths = colony.getBestPaths(10);

        //Wählt einen zufälligen Wert zwischen 0 und der Anzahl der möglichen Pfade
        int randValue = (int)(Math.random() * possiblePaths.size());
        favouredPath = possiblePaths.get(randValue);
    }

    public void setAllowedCities() {
        allowedCities = new Integer[cities];
        allowedCities = colony.getDefaultArray().clone();
        leftAllow = cities;
    }

    //Löscht das Objekt mit der ID element aus allowedCities und gibt den Index zurück, an dem das Objekt stand
    public int removeFromAllowedCities (int element) {
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

    public double arcfitness(Node cityj, int i) {
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
    public double stateTransitionProbability(Node cityi, Node cityj, int i) {
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
    //Prüft, ob der gefundene Pfad eine bessere Fitness aufweist als der favorisierte Pfad mit dem die Biene begonnen hat
    public boolean shouldBeeDance() {
        int foundProb = fitness.evaluate(new Path(newPath), -1).getFitness();
        int oldProb = fitness.evaluate(new Path(favouredPath), -1).getFitness();

        return (foundProb <= oldProb);
    }

    //Path in ArrayList schreiben
    public void performWaggleDance(Path foundPath) {
        //Sollte der gefundene Pfad besser sein, als der Pfad mit dem die Biene begonnen hat:
        if (shouldBeeDance()) {
            //Setzt den favorisierten Pfad auf den neu gefundenen Pfad
            favouredPath = newPath.clone();
            colony.addPathToResultPaths(foundPath, favouredPath);
        }
    }

    public Integer[] getPath() {
        return favouredPath;
    }
}
