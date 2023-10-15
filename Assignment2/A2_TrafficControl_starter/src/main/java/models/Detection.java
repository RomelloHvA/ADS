package models;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static models.Car.CarType;
import static models.Car.FuelType;

public class Detection {
    private final Car car;                  // the car that was detected
    private final String city;              // the name of the city where the detector was located
    private final LocalDateTime dateTime;   // date and time of the detection event

    /* Representation Invariant:
     *      every Detection shall be associated with a valid Car
     */

    public Detection(Car car, String city, LocalDateTime dateTime) {
        this.car = car;
        this.city = city;
        this.dateTime = dateTime;
    }

    /**
     * Parses detection information from a line of text about a car that has entered an environmentally controlled zone
     * of a specified city.
     * the format of the text line is: lisensePlate, city, dateTime
     * The licensePlate shall be matched with a car from the provided list.
     * If no matching car can be found, a new Car shall be instantiated with the given lisensePlate and added to the list
     * (besides the license plate number there will be no other information available about this car)
     * @param textLine
     * @param cars     a list of known cars, ordered and searchable by licensePlate
     *                 (i.e. the indexOf method of the list shall only consider the lisensePlate when comparing cars)
     * @return a new Detection instance with the provided information
     * or null if the textLine is corrupt or incomplete
     */
    public static Detection fromLine(String textLine, List<Car> cars) {

        Detection newDetection;
        try {
            String newCity = (textLine.split(",")[1].trim());

            LocalDateTime newDate = (LocalDateTime.parse(textLine.split(",")[2].trim()));

            //  use the cars.indexOf to find the car that is associated with the licensePlate of the detection
            Car newCar = cars.stream().filter(car -> car.getLicensePlate().equals(textLine.split(",")[0].trim())).findFirst().orElse(null);
            //  if no car can be found a new Car shall be instantiated and added to the list and associated with the detection
            if (newCar == null) {
                newCar = new Car(textLine.split(",")[0].trim());
                cars.add(newCar);
            }

            newDetection = new Detection(newCar, newCity, newDate);
            return newDetection;

        } catch (Exception e) {
            System.out.printf("Could not parse Detection specification in text line '%s'\n", textLine);
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Validates a detection against the purple conditions for entering an environmentally restricted zone
     * I.e.:
     * Diesel trucks and diesel coaches with an emission category of below 6 may not enter a purple zone
     * @return a Violation instance if the detection saw an offence against the purple zone rule/
     *          null if no offence was found.
     */
    public Violation validatePurple() {
        if (car.getFuelType() == FuelType.Diesel && (car.getCarType() == CarType.Coach || car.getCarType() == CarType.Truck)
                && car.getEmissionCategory() < 6) {
            return new Violation(car, city);
        }

        return null;
    }


    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }


    @Override
    public String toString() {
        // TODO represent the detection in the format: licensePlate/city/dateTime

        return String.format("%s/%s/%s", car.getLicensePlate(), city, dateTime.toString());       // replace by a proper outcome
    }

}
