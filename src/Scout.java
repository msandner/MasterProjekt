import com.hsh.parser.Dataset;
import java.util.Arrays;
import java.util.Collections;

public class Scout extends Bee {

    //Zum Testen
    private int ID;

    private BeeColony colony;
    private Integer[] favouredPath;

    //ID so gesetzt, dass sie nach den "normalen" Bienen anfangen
    Scout(int ID, BeeColony colony, Dataset dataset, double[][] distance) {
        super(ID, colony, dataset, distance);
        this.ID = ID + dataset.getSize();
        this.colony = colony;
        this.favouredPath = super.getPath();
    }

    //kein observeDance, dafür wird der favouredPath am Ende immer wieder auf einen neuen Zufallspfad gesetzt
    @Override public void mainProcedure() {
        searchNewPath();
        performWaggleDance();
        favouredPath = newRandomFavouredPath();
    }

    //zufälligen Pfad erstellen
    private Integer[] newRandomFavouredPath() {
        Integer[] pathArray = colony.getDefaultArray().clone();
        Collections.shuffle(Arrays.asList(pathArray));
        return pathArray;
    }
}

