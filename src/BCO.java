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

        //Colony erstellen
        ArrayList<Evaluable> evaluables = new ArrayList<>();
        BeeColony colony = new BeeColony(cities, dataset);
        for (int i = 0; i < 280; i++) {
            Path a = new Path(colony.getBee(i).getPath());
            evaluables.add(a);
        }
        fitness.evaluate(evaluables);
        evaluables.clear();

        for(int j = 0; j < 5; j++) {
            for (int i = 0; i < 280; i++) {
                colony.getBee(i).mainProcedure();
                Path b = new Path(colony.getBee(i).getPath());
                evaluables.add(b);
            }
            fitness.evaluate(evaluables);
        }

    }


    public Node getNodeByIDFromDataSet(int id){
        return dataset.getNodeByID(id);
    }

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
