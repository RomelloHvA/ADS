package models;
// TODO
public class PassengerWagon extends Wagon {

    private int wagonId;
    private int numberOfSeats;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        // TODO
        super();
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {

        return numberOfSeats;
    }
}
