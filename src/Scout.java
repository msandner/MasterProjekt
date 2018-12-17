import com.hsh.parser.Dataset;

import java.util.Arrays;
import java.util.Collections;

public class Scout extends Bee {

    private int ID;
    private BeeColony colony;
    private Integer[] favouredPath;

    //ID so gesetzt, dass sie nach den "normalen" Bienen anfangen
    public Scout(int ID, BeeColony colony, Dataset dataset, double[][] distance) {
        super(ID, colony, dataset, distance);
        this.ID = ID + dataset.getSize();
        this.colony = colony;
        this.favouredPath = super.getPath();
    }

    //kein observeDance, dafür wird der favouredPath immer wieder auf einen neuen Zufallspfad gesetzt
    @Override public void mainProcedure() {
        performWaggleDance(searchNewPath());
        favouredPath = newRandomFavouredPath();
    }

    //zufälligen Pfad erstellen
    public Integer[] newRandomFavouredPath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));
        return pathArray;
    }
}

