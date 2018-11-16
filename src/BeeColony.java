import java.util.ArrayList;

public class BeeColony {

    private int beeCount;
    private ArrayList<Bee> colony = new ArrayList<>();

    public BeeColony(int beeCount) {
        this.beeCount = beeCount;
        for (int i = 0; i < beeCount; i++) {
            colony.add(new Bee(i));
        }
            }
}
