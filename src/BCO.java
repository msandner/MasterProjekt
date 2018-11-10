import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Parser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class BCO {

    public static void main(String[] args) throws IOException {
        String pathToData = "a280.tsp";
        Dataset dataset = Parser.read(pathToData);
        int cities = dataset.getSize();

        ArrayList<Evaluable> path = initializePath(cities);

        Fitness fitness = new Fitness(dataset);
        fitness.evaluate(path);
    }

    /*initialisiert einen Pfad und gibt diesen als List zurück*/
    public static ArrayList<Evaluable> initializePath(int cities) throws IOException {
        ArrayList<Evaluable> initialpath = new ArrayList<>();

        Integer[] patharray = new Integer[cities];

        for(int i = 0; i < cities; i++) {
            patharray[i] = i+1;
        }

        Collections.shuffle(Arrays.asList(patharray));

        Path initial = new Path(patharray);
        initialpath.add(initial);
        
        return initialpath;
    }
}

class Path extends Evaluable {
    ArrayList<Integer> path;
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