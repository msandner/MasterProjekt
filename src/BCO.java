import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;

public class BCO {

    public static void main(String[] args) throws IOException {
        String pathToData = "a280.tsp";
        initializePath(pathToData);
    }

    /*Initiliasierung des Pfades*/
    public static void initializePath(String pathToData) throws IOException {
        Dataset dataset = Parser.read(pathToData);
        int cities = dataset.getSize();
        Fitness fitness = new Fitness(dataset);
        ArrayList<Evaluable> initialpath = new ArrayList<>();

        int patharray[] = new int[cities];
        for(int i = 0; i < cities; i++) {
            patharray[i] = i+1;
        }

        Path initial = new Path(patharray);
        initialpath.add(initial);
        fitness.evaluate(initialpath);

    }
}

class Path extends Evaluable {
    ArrayList<Integer> path;
    public Path(int[] path) {
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