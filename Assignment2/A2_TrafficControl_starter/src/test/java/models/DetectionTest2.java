package models;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;



public class DetectionTest2 extends DetectionTest {

    @BeforeEach
    @Override
    public void setup() {
        super.setup();
    }

    @Test
        void validatePurpleDieselTruckBelow6EmissionLpgShouldReturnNull() {
            Detection detection = new Detection(icova, "Rotterdam", LocalDateTime.now());

            Violation violation = detection.validatePurple();

            assertNull(violation);
        }

        @Test
        void validatePurpleDieselCoachBelow6EmissionShouldReturnViolation() {
            Detection detection = new Detection(daf1, "Amsterdam", LocalDateTime.now());

            Violation violation = detection.validatePurple();

            assertNotNull(violation);
            assertEquals(daf1, violation.getCar());
            assertEquals("Amsterdam", violation.getCity());
        }

        @Test
        void validatePurpleExistingDieselTruckWith6EmissionShouldReturnViolation() {
            Detection detection = new Detection(volvo1, "Aalsmeer", LocalDateTime.now());

            Violation violation = detection.validatePurple();
            System.out.println(violation);
            assertNotNull(violation);
            assertEquals(volvo1, violation.getCar());
            assertEquals("Aalsmeer", violation.getCity());
        }

        @Test
        void validatePurpleExistingPetrolCarShouldReturnNull() {
            Detection detection = new Detection(scoda, "Landshoven", LocalDateTime.now());

            Violation violation = detection.validatePurple();

            assertNull(violation);
        }

        @Test
        void validatePurpleExistingDieselSedanShouldReturnNull() {
            Detection detection = new Detection(audi, "Pannenkoek", LocalDateTime.now());

            Violation violation = detection.validatePurple();

            assertNull(violation);
        }
    }
