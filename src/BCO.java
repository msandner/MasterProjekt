import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;

import java.io.IOException;
import java.util.*;

public class BCO {

    //Parameter von Wong vorgegeben
    private double alpha = 1.0;
    private double beta = 20.0;
    private double lambda = 0.99;
    private static int cities;

    private static Dataset dataset;

    public static void main(String[] args) throws IOException {
        //Dataset vorbereiten
        String pathToData = "a280.tsp";
        dataset = Parser.read(pathToData);
        cities = dataset.getSize();

        Fitness fitness = new Fitness(dataset);

        //Colony erstellen
        ArrayList<Evaluable> evaluables = new ArrayList<>();
        BeeColony colony = new BeeColony(20, dataset);

        //ganze Kolonie
        //initialer Pfad als 0. Iteration
        for (int i = 0; i < 20; i++) {
            Path a = new Path(colony.getBee(i).getPath());
            evaluables.add(a);
        }
        fitness.evaluate(evaluables);
        evaluables.clear();

        //weitere Iterationen
        for(int j = 0; j < 5; j++) {
            for (int i = 0; i < 20; i++) {
                colony.getBee(i).mainProcedure();
                Path b = new Path(colony.getBee(i).getPath());
                evaluables.add(b);
            }
            fitness.evaluate(evaluables);
        }

        //eine Biene
        /*Path b = new Path(colony.getBee(1).getPath());
        evaluables.add(b);
        fitness.evaluate(evaluables);
        for(int i = 0; i < 15; i++) {
            colony.getBee(1).mainProcedure();
            Path a = new Path(colony.getBee(i).getPath());
            evaluables.add(a);
            fitness.evaluate(evaluables);
            evaluables.clear();
        }*/

    }


    public Node getNodeByIDFromDataSet(int id){
        return dataset.getNodeByID(id);
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
        //return 1-(Math.random());
        return lambda;
    }




}
