import com.hsh.parser.Node;

import java.io.IOException;


public class Bee {

    private int ID;
    private int iteration;

    private BCO bco = new BCO();
    int alpha = bco.alpha;
    int beta = bco.beta;
    double gamma = bco.gamma;
    int cities = bco.getCityCount();

    //favorisierter Pfad (durch Dance)
    private int favouredCityID = 0;
    private Integer[] favouredPath = new Integer[cities];

    //Set mit möglichen nächsten Städten A
    private Integer[] allowedCities;

    private Integer[] path;

    public Bee(int ID) throws IOException {
        this.ID = ID;
        this.iteration = 0;
        this.path = new Integer[cities];
        setInitialPath();
        setAllowedCities();
    }

    public void setInitialPath() throws IOException {
        this.path = bco.initializePath(cities);
    }

    //alle Städte sind von überall erreichbar oder nicht?
    public void setAllowedCities() {
        this.allowedCities = new Integer[cities];
        for (int i = 0; i < cities; i++) {
            allowedCities[i] = i + 1;
        }
    }

    public void observeDance() {
        //ToDo
    }

    //Biene geht durch TSP und erstellt einen neuen Path mit berechneten Werten
    public void forageByTransRule() {
        //ToDo
    }

    public void performWaggleDance() {
        //ToDo
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
            return gamma;
        } else {
            double a = 1 - gamma * AunionF;
            double b = allowedCities.length - AunionF;
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
