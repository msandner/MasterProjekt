import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import java.util.*;

public class Bee {

    //für Debugging und Testen notwendig
    private int ID;
    private int iteration;

    private final int cities;
    private BeeColony colony;
    private Fitness fitness;

    private BCO bco = new BCO();

    private double[][] distanceMatrix;

    //favorisierter Pfad
    private Integer[] favouredPath;

    //Set mit möglichen nächsten Städten A
    private Integer[] allowedCities;
    private int leftAllow;

    //Pfad mit bereits besuchten Städten -> am Ende der neue gefundene Pfad
    private Integer[] newPath;

    Bee(int ID, BeeColony colony, Dataset dataset, double[][] distanceMatrix) {
        this.ID = ID;
        this.colony = colony;
        fitness = new Fitness(dataset, false);
        cities = dataset.getSize();

        this.distanceMatrix = distanceMatrix;

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
        searchNewPath();
        performWaggleDance();
    }

    //initialer Pfad = zufällige Anordnung der Knoten
    private void setInitialPath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));

        //zu BestPath ArrayList hinzufügen, damit die 0te Iteration observeDance verwenden kann
        colony.addArrayToBestPath(pathArray);

        favouredPath = pathArray.clone();
    }

    //aus ArrayList einen zufälligen Pfad wählen, der als favouredPath genutzt wird
    private void observeDance() {
        //Holt die x besten Pfade aus den gefundenen Pfaden (sind nach ihrer Fitness sortiert)
        List<Integer[]> possiblePaths = colony.getXBestPaths(15);

        //Wählt einen zufälligen Pfad aus den besten Pfaden aus
        int randValue = (int) (Math.random() * possiblePaths.size());
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

        for (int i=0; i < allowedCities.length; i++) {
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
    void searchNewPath() {
        int count;
        int index;
        int newNode;
        double randomValue;
        double totalProb;

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
            randomValue = Math.random();
            index = 0;
            totalProb = 0.0;
            count = 0;

            //Jeder Knoten wird mit allen noch verfügbaren Knoten verglichen
            for (Integer allowedCity : allowedCities) {
                //Wenn ein Knoten in allowedCities den Wert -2 besitzt, wurde er bereits besucht
                if (allowedCity != -2) {
                    //Die Wahrscheinlichkeiten aufaddieren
                    //Die resultierenden Wahrscheinlichkeiten so lange aufaddieren bis sie den zufällig gewählten Wert erreichen oder übersteigen
                    //Dies stellt die Zufälligkeit sicher, mit denen die Bienen ihre Pfade auswählen
                    totalProb += stateTransitionProbability(newPath[i], allowedCity, i + 1);
                    if (totalProb >= randomValue) {
                        index = count;
                        break;
                    }
                    count++;
                }
            }
            //Fügt den neuen Knoten zum Pfad hinzu
            newNode = allowed.get(index);
            newPath[i+1] = newNode;
            //Löscht den besuchten Knoten aus den Listen der noch verfügbaren Städte
            removeFromAllowedCities(newNode);
            allowed.remove(index);
        }
        iteration++;
    }

    private double arcfitness(int cityjID, int i) {
        int AunionF = 0;
        //testen, ob der nächste Knoten im beobachteten Pfad auch in der Liste der besuchbaren Städte ist
        if(Arrays.asList(allowedCities).contains(favouredPath[i])) {
            AunionF = 1;
        }

        if ((cityjID == favouredPath[i])) {
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

    //cityi = aktuelle Stadt
    //cityj = Stadt mit der verglichen werden soll
    //i = Position an der die Biene gerade steht
    private double stateTransitionProbability(int cityiID, int cityjID, int i) {
        double arcfitness = Math.pow(arcfitness(cityjID, i), bco.getAlpha());
        double distance = Math.pow(1.0/(distanceMatrix[cityiID][cityjID]), bco.getBeta());

        double result1 = arcfitness * distance;

        double result2 = 0.0;
        //Summe der (Distanz * Arcfitness) über alle möglichen Städte, die noch besucht werden können
        for (Integer allowedCity : allowedCities) {
            if (allowedCity != -2 && cityiID != allowedCity) {
                result2 += Math.pow(arcfitness(allowedCity, i), bco.getAlpha()) * Math.pow(1.0 /(distanceMatrix[cityiID][allowedCity]), bco.getBeta());
            }
        }

        return result1 / result2;
    }

    //gibt true zurück, wenn der gefundene Pfad eine bessere Fitness aufweist als der favorisierte Pfad mit dem die Biene begonnen hat
    private boolean shouldBeeDance(Path foundPath) {
        int foundFitness = fitness.evaluate(foundPath, -1).getFitness();
        int oldFitness = fitness.evaluate(new Path(favouredPath), -1).getFitness();

        return (foundFitness < oldFitness);
    }

    //Path in ArrayList schreiben, wenn ein besserer gefunden worde
    void performWaggleDance() {
        //Verbesserung wenn möglich
        twoOpt();
        //benötigt, da favouredPath möglicherweise in twoOpt geändert wird und sonst der falsche foundPath eingefügt wird
        Path foundPath = new Path(newPath);
        if (shouldBeeDance(foundPath)) {
            //Setzt den favorisierten Pfad auf den neu gefundenen Pfad
            favouredPath = newPath.clone();
            //Prüfen, ob der Pfad schon in ResultPaths/foundPaths enthalten ist, da er dann nicht reingeschrieben werden soll
            if(!colony.getResultPaths().contains(foundPath)) {
                colony.addPathToResultPaths(foundPath, favouredPath);
            }
        }
    }

    //Kanten (x,y) und (u,v)
    //wenn Distanz von (x,u)(y,v) besser ist als Original (x,y)(u,v), dann Änderung der Reihenfolge
    private void twoOpt() {
        for(int i = 0; i < newPath.length-3; i+=3) {
            double xyuv = distanceMatrix[newPath[i]][newPath[i+1]] + distanceMatrix[newPath[i+2]][newPath[i+3]];
            double xuyv = distanceMatrix[newPath[i]][newPath[i+2]] + distanceMatrix[newPath[i+1]][newPath[i+3]];

            if(xyuv > xuyv) {
                int temp = newPath[i+2];
                newPath[i+2] = newPath[i+1];
                newPath[i+1] = temp;
                //System.out.println("Verbesserung von " + xyuv + " auf " + xuyv);
            }
        }
    }

    Integer[] getPath() {
        return favouredPath;
    }

}
