import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BCO {

    //Parameter von Wong, eigentlich alpha = 1.0, beta = 10.0, lambda = 0.99
    private double alpha = 1.0;
    private double beta = 10;
    private double lambda = 0.75;

    private static int cities;

    private static Dataset dataset;

    public static void main(String[] args) throws IOException {
        //Dataset wird als erstes Programmargument übergeben
        //String pathToData = args[0];

        //Dataset wird als Dateiname übergeben
        String pathToData = "eil101.tsp";
        dataset = Parser.read(pathToData);
        cities = dataset.getSize();

        Fitness fitness = new Fitness(dataset);

        //Colony erstellen
        int beecount = cities;
        ArrayList<Evaluable> evaluables = new ArrayList<>();
        BeeColony colony = new BeeColony(beecount, dataset);
        beecount += colony.getScoutsCounter();

        //initialer Pfad als 0. Iteration
        for (int i = 0; i < beecount; i++) {
            Path a = new Path(colony.getBee(i).getPath());
            evaluables.add(a);
        }

        fitness.evaluate(evaluables);

        //weitere Iterationen
        for(int j = 1; j <= 15; j++) {
            for (int i = 0; i < beecount; i++) {
                colony.getBee(i).mainProcedure();
            }
            fitness.evaluate(colony.getResultPathsAsEvaluable(20));

            /*Evaluable[] evas = fitness.evaluate(colony.getResultPathsAsEvaluable(30));
            for(Evaluable eva : evas){
                if(!eva.isValid()) {
                    System.out.println(eva.getErrorCode());
                }
            }*/        }
    }

    public int getCityCount() {
        return cities;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getLambda() {
        return lambda;
    }

}