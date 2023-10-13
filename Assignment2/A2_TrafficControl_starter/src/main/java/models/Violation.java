package models;

import java.util.Comparator;

public class Violation {
    private final Car car;
    private final String city;
    private int offencesCount;

    public Violation(Car car, String city) {
        this.car = car;
        this.city = city;
        this.offencesCount = 1;
    }

    public static int compareByLicensePlateAndCity(Violation v1, Violation v2) {
        if (v1 == null || v2 == null) throw new IllegalArgumentException("Violation cannot be null");

        int result = v1.compareByCar(v2);
        if(result == 0){
            result = v1.compareByCity(v2);
        }

        return result;   // replace by a proper outcome
    }

    public int compareByCity(Violation other) {
        return this.getCity().compareTo(other.getCity());   // replace by a proper outcome
    }

    public int compareByCar(Violation other) {
        return this.car.getLicensePlate().compareTo(other.getCar().getLicensePlate());
    }



    /**
     * Aggregates this violation with the other violation by adding their counts and
     * nullifying identifying attributes car and/or city that do not match
     * identifying attributes that match are retained in the result.
     * This method can be used for aggregating violations applying different grouping criteria
     * @param other
     * @return  a new violation with the accumulated offencesCount and matching identifying attributes.
     */
    public Violation combineOffencesCounts(Violation other) {
        if (other == null) return this;  // nothing to combine with

        Violation combinedViolation = new Violation(
                // nullify the car attribute iff this.car does not match other.car
                this.car != null && this.car.equals(other.car) ? this.car : null,
                // nullify the city attribute iff this.city does not match other.city
                this.city != null && this.city.equals(other.city) ? this.city : null);

        // add the offences counts of both original violations
        combinedViolation.setOffencesCount(this.offencesCount + other.offencesCount);

        return combinedViolation;
    }

    public Car getCar() {
        return car;
    }

    public String getCity() {
        return city;
    }

    public int getOffencesCount() {
        return offencesCount;
    }

    public void setOffencesCount(int offencesCount) {
        this.offencesCount = offencesCount;
    }

    @Override
    public String toString() {
        return String.format("%s/%s/%d", (car == null ? "null" : car.getLicensePlate()), city, offencesCount);   // replace by a proper outcome
    }
}
