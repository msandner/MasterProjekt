import com.hsh.parser.Node;

import java.util.ArrayList;

public class Bee {

    private int ID;
    private int iteration;

    private BCO bco = new BCO();
    int alpha = bco.alpha;
    int beta = bco.beta;
    double gamma = bco.gamma;

    //favorisierte nächste Stadt F
    private Node favouredCity;
    //Set mit möglichen nächsten Städten A
    private ArrayList<Node> allowedCities = new ArrayList<>();

    public Bee(int ID) {
        this.ID = ID;
        this.iteration = 0;
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
