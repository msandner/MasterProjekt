import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Parser;
import java.io.IOException;
import java.util.*;

public class BCO {

    //Parameter von Wong, eigentlich alpha = 1.0, beta = 10.0, lambda = 0.99
    private static double alpha = 1.0;
    private double beta = 8;
    private static double lambda = 0.75;

    private static int cities;

    private static Dataset dataset;

    public static void main(String[] args) throws IOException {
        //Dataset wird als erstes Programmargument übergeben
        //String pathToData = args[0];

        //Dataset wird als Dateiname übergeben
        String pathToData = "eil51.tsp";
        dataset = Parser.read(pathToData);
        cities = dataset.getSize();

        Fitness fitness = new Fitness(dataset);

        //Colony erstellen
        int beecount = cities;
        ArrayList<Evaluable> evaluables = new ArrayList<>();
        BeeColony colony = new BeeColony(beecount, dataset);
        //Hinzufügen der Scoutanzahl
        beecount += cities/2;

        //initialer Pfad als 0. Iteration
        for (int i = 0; i < beecount; i++) {
            Path a = new Path(colony.getBee(i).getPath());
            evaluables.add(a);
        }

        Evaluable[] last = fitness.evaluate(evaluables);
        int c = 0;

        //weitere Iterationen
        for(int j = 0; j < 30; j++) {
            for (int i = 0; i < beecount; i++) {
                colony.getBee(i).mainProcedure();
            }
            Evaluable[] next = fitness.evaluate(colony.getResultPathsAsEvaluable(20));

            /*
            //automatische Parameteranpassung bei Stagnation (und zurücksetzen am Ende der nächsten Iteration)
            if (last[0].getFitness() == next[0].getFitness()) {
                c++;
            }
            //wenn 2x derselbe Pfad als bester Pfad evaluiert wurde
            if(c < 2) {
                setAlpha(1);
            } else {
                setAlpha(0);
            }
            last = next;*/
        }
    }

    int getCityCount() {
        return cities;
    }

    double getAlpha() {
        return alpha;
    }

    double getBeta() {
        return beta;
    }

    double getLambda() {
        return lambda;
    }

    private static void setLambda(double n) {
        lambda = n;
    }

    private static void setAlpha(double n) {
        alpha = n;
    }

}