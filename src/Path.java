import com.hsh.Evaluable;

import java.util.ArrayList;

public class Path extends Evaluable {
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