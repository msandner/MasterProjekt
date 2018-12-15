import com.hsh.parser.Dataset;

public class Scout extends Bee {

    public int ID;
    //favorisierter Pfad
    public Integer[] favouredPath;

    //Pfad mit bereits besuchten Städten -> am Ende der neue gefundene Pfad
    public Integer[] newPath;

    public Scout(int ID, BeeColony colony, Dataset dataset) {
        super(ID, colony, dataset);
        this.ID = ID + dataset.getSize();
        favouredPath = super.getPath();
        newPath = new Integer[dataset.getSize()];
    }

    @Override public void mainProcedure() {
        performWaggleDance(searchNewPath());
    }
}

