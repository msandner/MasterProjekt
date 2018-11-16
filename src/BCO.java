import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;

import java.io.IOException;
import java.util.*;

public class BCO {

    //Parameter von Wong vorgegeben
    private int alpha = 1;
    private int beta = 10;
    private double gamma = 0.99;

    //wird in Main Methode abhängig von Dataset gesetzt
    private static int cities;
    private static int beeNumber = cities;

    //favorisierte nächste Stadt F
    private Node favouredCity;
    //Set mit möglichen nächsten Städten A
    private ArrayList<Node> allowedCities = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String pathToData = "a280.tsp";
        Dataset dataset = Parser.read(pathToData);
        cities = dataset.getSize();

        Fitness fitness = new Fitness(dataset);

        ArrayList<Evaluable> path = initializePath();
        fitness.evaluate(path);
    }

    /*initialisiert einen random Pfad und gibt diesen als ArrayList zurück
    * notwendig für erste Iteratrionen, wo Bienen noch keinen Dance beobachten können*/
    public static ArrayList<Evaluable> initializePath() throws IOException {
        ArrayList<Evaluable> initialpath = new ArrayList<>();

        Integer[] patharray = new Integer[cities];

        for (int i = 0; i < cities; i++) {
            patharray[i] = i + 1;
        }

        Collections.shuffle(Arrays.asList(patharray));

        Path initial = new Path(patharray);
        initialpath.add(initial);

        return initialpath;
    }

    public void initializePopulation() {
        //ToDo
    }

    public void observeDance() {
        //ToDo
    }

    public void forageByTransRule() {
        //ToDo
    }

    public void performWaggleDance() {
        //ToDo
    }

    /*gibt true zurück, wenn Path kürzer ist als "previous best trials"*/
    public boolean shouldBeeDance(Fitness fitness, ArrayList<Evaluable> path) {
        //ToDo
        return false;
    }

    public int danceDuration() {
        //ToDo
        return 0;
    }

    /*city i = aktuelle Stadt
    * city j = Stadt mit der verglichen werden soll
    * noch nicht herausgefunden, wie t in die Gleichung mit einspielt*/
    public double arcfitness(Node cityi, Node cityj, int t) {
        int AunionF = 0;
        //wenn accessable Cities A und favoured City F eine gemeinsame Instanz haben, dann AunionF auf 1 Setzen
        if(allowedCities.contains(favouredCity)) {
            AunionF = 1;
        }

        if(cityj.equals(favouredCity)) {
            return gamma;
        } else {
            double a = 1 - gamma * AunionF;
            double b = allowedCities.size() - AunionF;
            return a/b;
        }
    }

    //unter der Vermutung das die Summe von p_ij(t) = 1 ist
    public double stateTransitionProbability(Node cityi, Node cityj, int t) {
        double arcfitness = Math.pow(arcfitness(cityi, cityj, t), alpha);
        double distance= Math.pow(1.0/cityi.distance(cityj), beta);
        return (arcfitness * distance) / (Math.pow(1, alpha) * distance);
    }


}

class Path extends Evaluable {
    private ArrayList<Integer> path;
    public Path(Integer[] path) {
        // wandelt int[] in eine ArrayList um
        this.path = new ArrayList<>();
        for(int x : path){
            this.path.add(x);
        }
    }

    @Override
    public ArrayList<Integer> getPath() {
        return path;
    }
}