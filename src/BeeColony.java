import com.hsh.Evaluable;
import com.hsh.Fitness;

import java.io.IOException;
import java.util.ArrayList;

public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony = new ArrayList<>();

    public BeeColony(int beeCount) throws IOException {
        this.beeCount = beeCount;
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i));
        }
    }

    public Bee getBee(int index) {
        return colony.get(index);
    }

    public boolean shouldBeeDance() {
        //ToDo
        return false;
    }

    public int danceDuration() {
        //ToDo
        return 0;
    }


}
