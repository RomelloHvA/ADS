package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Indicates whether the train has at least one connected Wagon
     * @return
     */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    /**
     * A train is a passenger train when its first wagon is a PassengerWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isPassengerTrain() {
        return firstWagon instanceof PassengerWagon;
    }
    /**
     * A train is a freight train when its first wagon is a FreightWagon
     * (we do not worry about the posibility of mixed compositions here)
     * @return
     */
    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * @param wagon the first wagon of a sequence of wagons to be attached (can be null)
     */
    public void setFirstWagon(Wagon wagon) {
        firstWagon = wagon;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {

        Wagon searchWagon = firstWagon;
        int counter = 0;
        if (hasWagons()){
             counter = 1;
            while (searchWagon.hasNextWagon()) {
                counter++;
                searchWagon = searchWagon.getNextWagon();
            }
        }



        return counter;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
            if (hasWagons()) {
                return firstWagon.getLastWagonAttached();
            }
            return null;
        }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (firstWagon instanceof PassengerWagon searchWagon) {
            int totalSeats = 0;
            while   (searchWagon.hasNextWagon()) {
                totalSeats += searchWagon.getNumberOfSeats();
                searchWagon = (PassengerWagon) searchWagon.getNextWagon();
            }
            return totalSeats + searchWagon.getNumberOfSeats();
        } else {
            return 0;
        }

    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        if (firstWagon instanceof FreightWagon searchWagon) {
            int totalWeight = 0;
            while (searchWagon.hasNextWagon()) {
                totalWeight += searchWagon.getMaxWeight();
                searchWagon = (FreightWagon) searchWagon.getNextWagon();
            }
            return totalWeight + searchWagon.getMaxWeight();
        }
       return 0;

    }

     /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {


        if (hasWagons() && position >= 0 && firstWagon.getSequenceLength() > position){
            Wagon searchWagon = firstWagon;
            for (int i = 0; i < position; i++) {
                if(!searchWagon.hasNextWagon()){
                    return null;
                }
                searchWagon = searchWagon.getNextWagon();
            }
            return searchWagon;
        }
        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon indexWagon = null;

        if (hasWagons()) {
            indexWagon = firstWagon;

            while (indexWagon.getId() != wagonId){
                indexWagon = indexWagon.getNextWagon();
                if (!indexWagon.hasNextWagon() && indexWagon.getId() != wagonId){
                    return null;
                }

            }
        }
        return indexWagon;

    }


    /**
     * Determines if the given sequence of wagons can be attached to this train
     * Verifies if the type of wagons match the type of train (Passenger or Freight)
     * Verifies that the capacity of the engine is sufficient to also pull the additional wagons
     * Verifies that the wagon is not part of the train already
     * Ignores the predecessors before the head wagon, if any
     * @param wagon the head wagon of a sequence of wagons to consider for attachment
     * @return whether type and capacity of this train can accommodate attachment of the sequence
     */
    public boolean canAttach(Wagon wagon) {

        if (wagon == null){
            return false;
        }

        // Verify if firstwagon is empty, type match and engine capacity
        if (firstWagon != null
                && wagon.getClass() == firstWagon.getClass()
                && engine.getMaxWagons() >= (wagon.getSequenceLength() + firstWagon.getSequenceLength())) {
            //Verify if part of train already
            Wagon indexWagon = firstWagon;
            for (int i = 0; i < firstWagon.getSequenceLength(); i++) {
                // If part of the train cannot attach
                if (indexWagon == wagon || indexWagon == null){
                    return false;
                }
                indexWagon = indexWagon.getNextWagon();
            }

            return true;
            // Also checks if the sequence fits the engine on an empty train.
        } else return firstWagon == null && engine.getMaxWagons() >= wagon.getSequenceLength();

    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * if attachment is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be attached
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {

        boolean succesfullAttach = false;
        // Check if wagon can be attached
        if (canAttach(wagon)){
                wagon.detachFront();

            // If the train is not empty
            if (getLastWagonAttached() != null){
                wagon.reAttachTo(getLastWagonAttached());
            } else {
                setFirstWagon(wagon);
            }
            succesfullAttach = true;
        }

        return succesfullAttach;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * (the front is at position one, before the current first wagon, if any)
     * No change is made if the insertion cannot be made.
     *      * (when the sequence is not compatible or the engine has insufficient capacity)
     * if insertion is possible, the head wagon is first detached from its predecessors, if any
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
            boolean succesfullInsert = false;

        if (canAttach(wagon)){
                wagon.detachFront();


            if (firstWagon == null){
                setFirstWagon(wagon);
            } else {
                Wagon previousFirstWagon = firstWagon;
                setFirstWagon(wagon);
                attachToRear(previousFirstWagon);
            }

            succesfullInsert = true;



        }
        return succesfullInsert;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given position in the train.
     * (The current wagon at given position including all its successors shall then be reattached
     *    after the last wagon of the given sequence.)
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity
     *    or the given position is not valid for insertion into this train)
     * if insertion is possible, the head wagon of the sequence is first detached from its predecessors, if any
     * @param position the position where the head wagon and its successors shall be inserted
     *                 0 <= position <= numWagons
     *                 (i.e. insertion immediately after the last wagon is also possible)
     * @param wagon the head wagon of a sequence of wagons to be inserted
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        boolean isInserted = false;
        Wagon wagonAtposition = findWagonAtPosition(position);

        if (position > getNumberOfWagons()){
            return false;
        }

        //Check wagoncompatibility first and for empty wagons
        if (canAttach(wagon)) {
            wagon.detachFront();

            //Empty train
            if (wagonAtposition == null) {
                insertAtFront(wagon);
                isInserted = true;

//            position is front of the train;
            } else if (!wagonAtposition.hasPreviousWagon() && wagonAtposition == getLastWagonAttached() ) {
                insertAtFront(wagon);
                attachToRear(wagonAtposition);
                isInserted = true;
            }
        }

        System.out.println(this);
        return isInserted;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId   the id of the wagon to be removed
     * @param toTrain   the train to which the wagon shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
     }

    /**
     * Tries to split this train before the wagon at given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position  0 <= position < numWagons
     * @param toTrain   the train to which the split sequence shall be attached
     *                  toTrain shall be different from this train
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // TODO

        return false;   // replace by proper outcome
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        if (hasWagons()){
            setFirstWagon(firstWagon.reverseSequence());
        }


    }

    @Override
    public String toString() {

        StringBuilder trainString = new StringBuilder(this.engine.toString());

        int wagonLength = 0;

        if (hasWagons() && firstWagon != null){
            Wagon currentWagon = firstWagon;
            wagonLength = firstWagon.getSequenceLength();
           while (currentWagon.hasNextWagon()){
               trainString.append(currentWagon);
               currentWagon = currentWagon.getNextWagon();
           }
           trainString.append(currentWagon);
        }

        return String.format("%s with %d wagons from %s to %s", trainString, wagonLength, origin, destination);

    }

    // TODO string representation of a train
}

