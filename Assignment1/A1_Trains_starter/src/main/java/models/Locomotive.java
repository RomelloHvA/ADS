package models;

public class Locomotive {
    private int locNumber;
    private int maxWagons;


    public Locomotive(int locNumber, int maxWagons) {
        this.locNumber = locNumber;
        this.maxWagons = maxWagons;
    }

    public int getMaxWagons() {
        return maxWagons;
    }

    public int getLocNumber() {
        return locNumber;
    }

    public int setLocNumber(int locNumber) {
        this.locNumber = locNumber;
    }
}
