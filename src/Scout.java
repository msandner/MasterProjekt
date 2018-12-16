import com.hsh.parser.Dataset;

import java.util.Arrays;
import java.util.Collections;

public class Scout extends Bee {

    private int ID;
    private BeeColony colony;
    private Integer[] favouredPath;

    public Scout(int ID, BeeColony colony, Dataset dataset) {
        super(ID, colony, dataset);
        this.ID = ID + dataset.getSize();
        this.colony = colony;
        this.favouredPath = super.getPath();
    }

    @Override public void mainProcedure() {
        performWaggleDance(searchNewPath());
        favouredPath = newRandomFavouredPath();
    }

    public Integer[] newRandomFavouredPath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));
        return pathArray;
    }
}

