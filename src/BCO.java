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

        /*Path path = new Path(initializePath(cities));

        evaluables.add(path);*/

        ArrayList<Evaluable> evaluables = new ArrayList<>();

        //Colony erstellen
        BeeColony colony = new BeeColony(cities);
        for(int j = 0; j < 5; j++) {
            for (int i = 0; i < cities; i++) {
                colony.getBee(i).mainProcedure();
                Path a = new Path(colony.getBee(i).getNewPath());
                evaluables.add(a);
                colony.clearBestPaths();
            }
            fitness.evaluate(evaluables);
            evaluables.clear();
        }
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
    /*
    public static Integer[] observedPath (Path savedPath) {
        //Path observedPath = ;

        if (observedPath.getFitness() < savedPath.getFitness())
            return observedPath;
        else
            return savedPath;

    }
    */

    public int getCityCount() {
        return cities;
    }

    public int getAlpha() {
      return alpha;
    }

    public int getBeta() {
        return beta;
    }

    public double getGamma() {
        return gamma;
    }

}
