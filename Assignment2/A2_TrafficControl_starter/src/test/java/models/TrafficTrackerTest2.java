package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrafficTrackerTest2 extends TrafficTrackerTest{

    @BeforeEach
    @Override
    public void setup(){
        super.setup();
    }

    @Test public void totalFinesShouldEqual175() {
        Assertions.assertEquals(175.0,trafficTracker.calculateTotalFines());
    }

    @Test
    public void assertOutofBounds() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> trafficTracker.topViolationsByCar(3));
    }

    @Test
    public void citiesShouldEqualRotterdamAndAmsterdam() {
        Assertions.assertTrue(trafficTracker.topViolationsByCity(1).toString().contains("Rotterdam"));
        Assertions.assertTrue(trafficTracker.topViolationsByCity(2).toString().contains("Amsterdam"));
    }

}
