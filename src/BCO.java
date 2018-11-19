import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;

import java.io.IOException;
import java.util.*;

public class BCO {

    //Parameter von Wong vorgegeben
    protected int alpha = 1;
    protected int beta = 10;
    protected double gamma = 0.99;
    protected static int cities;

    private static Dataset dataset;

    public static void main(String[] args) throws IOException {
        //Dataset vorbereiten
        String pathToData = "a280.tsp";
        dataset = Parser.read(pathToData);
        cities = dataset.getSize();

        Fitness fitness = new Fitness(dataset);

        Path path = new Path(initializePath(cities));
        ArrayList<Evaluable> evaluables = new ArrayList<>();
        evaluables.add(path);
        fitness.evaluate(evaluables);

        //Colony erstellen
        BeeColony colony = new BeeColony(cities);
    }

    public Node getNodeByIDFromDataSet(int id){
        return dataset.getNodeByID(id);
    }

    /*initialisiert einen random Pfad und gibt diesen als ArrayList zurück
     * notwendig für erste Iteratrionen, wo Bienen noch keinen Dance beobachten können*/
    public static Integer[] initializePath(int cities) throws IOException {
        Integer[] patharray = new Integer[cities];
        for (int i = 0; i < cities; i++) {
            patharray[i] = i+1;
        }

        Collections.shuffle(Arrays.asList(patharray));

        return patharray;
    }

    public int getCityCount() {
        return cities;
    }



}
