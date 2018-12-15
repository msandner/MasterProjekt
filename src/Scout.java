import com.hsh.parser.Dataset;

public class Scout extends Bee {

    private int ID;

    public Scout(int ID, BeeColony colony, Dataset dataset) {
        super(ID, colony, dataset);
        this.ID = ID + dataset.getSize();
    }

    @Override public void mainProcedure() {
        performWaggleDance(searchNewPath());
    }
}

