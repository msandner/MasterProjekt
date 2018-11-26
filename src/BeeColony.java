import com.hsh.Evaluable;
import com.hsh.Fitness;

import java.io.IOException;
import java.util.ArrayList;

public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony = new ArrayList<>();
    private ArrayList<Integer[]> bestPaths = new ArrayList<>();


    public BeeColony(int beeCount) throws IOException {
        this.beeCount = beeCount;
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i));
        }
    }

    public BeeColony() {
        //empty
    }

    public Bee getBee(int index) {
        return colony.get(index);
    }


    public int danceDuration() {
        //ToDo
        return 0;
    }

    public ArrayList<Integer[]> getBestPaths() {
        return bestPaths;
    }
}
