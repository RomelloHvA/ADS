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
        if(!hasWagons()){
            return 0;
        }

        Wagon searchWagon = firstWagon;
        int counter = 1;

        while (searchWagon.hasNextWagon()) {
            counter++;
            searchWagon = searchWagon.getNextWagon();
        }

        return counter;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        if (!hasWagons()){
            return null;
        }

        return firstWagon.getLastWagonAttached();
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (!(firstWagon instanceof PassengerWagon searchWagon)){
            return 0;
        }

        int totalSeats = 0;

        while (searchWagon.hasNextWagon()) {
            totalSeats += searchWagon.getNumberOfSeats();
            searchWagon = (PassengerWagon) searchWagon.getNextWagon();
        }

        return totalSeats + searchWagon.getNumberOfSeats();

    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        if(!(firstWagon instanceof FreightWagon searchWagon)){
            return 0;
        }

        int totalWeight = 0;

        while (searchWagon.hasNextWagon()) {
            totalWeight += searchWagon.getMaxWeight();
            searchWagon = (FreightWagon) searchWagon.getNextWagon();
        }

        return totalWeight + searchWagon.getMaxWeight();
    }

     /**
     * Finds the wagon at the given position (starting at 0 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        //if position is not valid for this train, so it is smaller than 0 or bigger than amount of wagons the trains has
        if(position < 0 || position > getNumberOfWagons()){
            return null;
        }

        Wagon searchWagon = firstWagon;

        for (int i = 0; i < position ; i++) {
            searchWagon = searchWagon.getNextWagon();
        }

        return searchWagon;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        if (!hasWagons()){
            return null;
        }

        Wagon indexWagon = firstWagon;

        while (indexWagon.getId() != wagonId){

            if (!indexWagon.hasNextWagon()){
                return null;
            }

            indexWagon = indexWagon.getNextWagon();

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

        if (wagon == null || wagon == firstWagon){
            return false;
        }

        if(firstWagon != null){
            if (wagon instanceof PassengerWagon && !isPassengerTrain() || wagon instanceof FreightWagon && !isFreightTrain()){
                return false;
            }

            if ((firstWagon.getSequenceLength() + wagon.getSequenceLength()) > engine.getMaxWagons()){
                return false;
            }

            return !isWagonPartOfTrain(wagon);
        }

        return wagon.getSequenceLength() <= engine.getMaxWagons();

    }


    /**
     * Determines if the given wagon is part of the train already
     * @param wagon the wagon to check can have a tail sequence of wagons attached to it
     * @return true or false whether the wagon is part of the train already
     */
    private boolean isWagonPartOfTrain(Wagon wagon) {
        Wagon indexWagon = firstWagon;
        Wagon indexWagon2 = wagon;

        while (indexWagon.hasNextWagon()) {
            while (indexWagon2.hasNextWagon()) {

                if (indexWagon == indexWagon2) {
                    return true;
                }

                indexWagon2 = indexWagon2.getNextWagon();
            }

            indexWagon = indexWagon.getNextWagon();
            indexWagon2 = wagon;
        }

        return false;
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

        if(!canAttach(wagon)){
            return false;
        }

        wagon.detachFront();

        //if the train has no wagons yet, it should set it as the first wagon
        if (firstWagon == null){
            setFirstWagon(wagon);
        } else {
            getLastWagonAttached().attachTail(wagon);
        }

        return true;
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
        if(!canAttach(wagon)){
            return false;
        }

        wagon.detachFront();
        Wagon previousFirstWagon = firstWagon;

        setFirstWagon(wagon);

        //if the train has wagons already, it should attach the previous first wagon to the rear of the new first wagon
        if (previousFirstWagon != null){
            attachToRear(previousFirstWagon);
        }

        return true;
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

        if(!canAttach(wagon)){
            return false;
        }

        Wagon indexWagon = findWagonAtPosition(position);

        if (indexWagon == null && position < getNumberOfWagons() - 1 || position < 0){
            return false;
        }

        //if the position is 0, it should insert it at the front
        if (position == 0){
            return insertAtFront(wagon);
        }

        //if the position is bigger or equal to the amount of wagons the train has, it should insert it at the rear
        if (position >= getNumberOfWagons() ){
            return attachToRear(wagon);
        }

        //if the position is valid, it should insert it at the given position
        if (indexWagon != null){
            Wagon previousWagon = indexWagon.getPreviousWagon();
            wagon.reAttachTo(previousWagon);
            wagon.getLastWagonAttached().attachTail(indexWagon);

            return true;
        }

        return false;

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
        Wagon wagonToMove = findWagonById(wagonId);

        if (wagonToMove == null || !toTrain.canAttach(wagonToMove)){
            return false;
        }


        //if the wagonToMove is the firstwagon we need to change firstwagon to the next wagon
        if (wagonToMove == firstWagon){
            firstWagon = wagonToMove.getNextWagon();
        }

        wagonToMove.removeFromSequence();

        return toTrain.attachToRear(wagonToMove);
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
        Wagon wagonsToMove = findWagonAtPosition(position);

        if (wagonsToMove == null || !toTrain.canAttach(wagonsToMove)){
            return false;
        }

        //if the wagonsToMove is the firstwagon fromt this train we need to change firstwagon to null
        if(wagonsToMove.detachFront() == null){
            firstWagon = null;
        }

        return toTrain.attachToRear(wagonsToMove);

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

}

