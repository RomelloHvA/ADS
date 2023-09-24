import models.FreightWagon;
import models.PassengerWagon;
import models.Wagon;
import org.junit.jupiter.api.*;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class WagonTestExtra {

    Wagon passengerWagon1, passengerWagon2, passengerWagon3, passengerWagon4, passengerWagon5;
    Wagon freightWagon1, freightWagon2;

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.ENGLISH);
        passengerWagon1 = (Wagon)(Object)new PassengerWagon(8001, 36);
        passengerWagon2 = (Wagon)(Object)new PassengerWagon(8002, 18);
        passengerWagon3 = (Wagon)(Object)new PassengerWagon(8003, 48);
        passengerWagon4 = (Wagon)(Object)new PassengerWagon(8004, 44);
        passengerWagon5 = (Wagon)(Object)new PassengerWagon(8005, 22);

        freightWagon1 = (Wagon)(Object)new FreightWagon(9001, 50000);
        freightWagon2 = (Wagon)(Object)new FreightWagon(9002, 60000);
    }




    @Test
    /**
     * Reversing the second wagon in a sequence of five should reverse the sequence of the second wagon and the wagons after it.
     * The sequence of the wagons before the second wagon should remain the same.
     * The sequence length should remain the same.
     * The first wagon should still be the first wagon.
     */
    public void T01_PartiallyReverseASequenceOfFive() {
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        passengerWagon3.attachTail(passengerWagon4);
        passengerWagon4.attachTail(passengerWagon5);

        // reverse part of the sequence
        Wagon rev = passengerWagon2.reverseSequence();
        assertEquals(4, rev.getSequenceLength(), "After reversing the middle wagon, the sequence length should remain the same");
        assertEquals(passengerWagon5, rev);

        assertEquals(passengerWagon4, rev.getNextWagon());
        assertEquals(passengerWagon1, rev.getPreviousWagon());

        assertFalse(passengerWagon2.hasNextWagon());
        assertEquals(passengerWagon5, passengerWagon4.getPreviousWagon());

        assertEquals(5, passengerWagon1.getSequenceLength());
        assertFalse(passengerWagon1.hasPreviousWagon());
        assertEquals(passengerWagon5, passengerWagon1.getNextWagon());

        assertEquals(passengerWagon3, passengerWagon2.getPreviousWagon());
        assertEquals(passengerWagon2, passengerWagon3.getNextWagon());

        assertEquals(passengerWagon4, passengerWagon3.getPreviousWagon());
        assertEquals(passengerWagon3, passengerWagon4.getNextWagon());

    }

    @Test
    /**
     * Test if a sequence which references in a loop can be detected.
     */

   public void T02_FindingErrorInCorruptSequenceWhileReversing(){
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        passengerWagon3.attachTail(passengerWagon4);
        passengerWagon4.attachTail(passengerWagon5);

        passengerWagon4.setNextWagonForTest(passengerWagon2);


        assertThrows(IllegalStateException.class, () -> passengerWagon2.reverseSequence());

        passengerWagon4.setNextWagonForTest(passengerWagon5);
        passengerWagon2.setPreviousWagonForTest(passengerWagon4);

        assertThrows(IllegalStateException.class, () -> passengerWagon2.reverseSequence());

        passengerWagon2.setPreviousWagonForTest(passengerWagon1);
        passengerWagon1.setPreviousWagonForTest(passengerWagon5);


        assertThrows(IllegalStateException.class, () -> passengerWagon2.reverseSequence());


    }

    /**
     * Test if a sequence which references in a loop can be detected. While getting the length of the sequence.
     */
    @Test
    public void T03_FindingErrorInCorruptSequenceWhileGettingLength() {
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        passengerWagon3.attachTail(passengerWagon4);

        passengerWagon1.setPreviousWagonForTest(passengerWagon4);
        passengerWagon4.setNextWagonForTest(passengerWagon1);

        assertThrows(IllegalStateException.class, () -> passengerWagon1.getSequenceLength());

        passengerWagon3.setPreviousWagonForTest(passengerWagon4);

        assertThrows(IllegalStateException.class, () -> passengerWagon1.getSequenceLength());

    }

    /**
     * Test if a sequence which references in a loop can be detected. While getting the last wagon attached.
     */
    @Test
    public void T04_FindingErrorInCorruptSequenceWhileGettingLastWagonAttached() {
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        passengerWagon3.attachTail(passengerWagon4);

        passengerWagon1.setPreviousWagonForTest(passengerWagon4);
        passengerWagon4.setNextWagonForTest(passengerWagon1);

        assertThrows(IllegalStateException.class, () -> passengerWagon1.getLastWagonAttached());

        passengerWagon3.setNextWagonForTest(passengerWagon2);

        assertThrows(IllegalStateException.class, () -> passengerWagon3.getLastWagonAttached());

    }

    /**
     * Test if a sequence which references in a loop can be detected. While reversing the sequence.
     */
    @Test
    public void T05_FindingErrorInCorruptSequenceWhileReversing() {
        passengerWagon1.attachTail(passengerWagon2);
        passengerWagon2.attachTail(passengerWagon3);
        passengerWagon3.attachTail(passengerWagon4);

        passengerWagon1.setPreviousWagonForTest(passengerWagon4);
        passengerWagon4.setNextWagonForTest(passengerWagon1);

        assertThrows(IllegalStateException.class, () -> passengerWagon1.reverseSequence());

        passengerWagon3.setPreviousWagonForTest(passengerWagon4);
        passengerWagon4.setPreviousWagonForTest(null);

        assertThrows(IllegalStateException.class, () -> passengerWagon3.reverseSequence());

    }




}
