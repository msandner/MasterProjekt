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

    //wird in Main Methode abhängig von Dataset gesetzt
    private static int cities;

    public static void main(String[] args) throws IOException {
        String pathToData = "a280.tsp";
        Dataset dataset = Parser.read(pathToData);
        cities = dataset.getSize();

        BeeColony colony = new BeeColony(cities);

        Fitness fitness = new Fitness(dataset);

        //zum Testen random Pfad prüfen
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