package models;
// TODO
public class FreightWagon extends Wagon {
    private int maxWeight;


    public FreightWagon(int wagonId, int maxWeight) {
        super(wagonId);
        this.maxWeight = maxWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight){
        this.maxWeight = maxWeight;
    }

    @Override
    public String toString() {
        return "[Wagon-" + id + "]";
    }
}
