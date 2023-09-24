package models;

import java.util.HashSet;
import java.util.Set;

public abstract class Wagon {
    private final int id;           // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
                                    // a.k.a. the successor of this wagon in a sequence
                                    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
                                    // a.k.a. the predecessor of this wagon in a sequence
                                    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    protected Wagon (int wagonId) {
        this.id = wagonId;
    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    public void setNextWagon(Wagon next){nextWagon = next;}
    public void setPreviousWagon(Wagon previous){previousWagon = previous;}


    /**
     * @return  whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon != null;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     * @return  the last wagon
     */
    public Wagon getLastWagonAttached() {
        if (!hasNextWagon()){
            return this;
        }

        checkSequenceForInvariants();

        Wagon searchWagon = nextWagon;

        while (searchWagon.hasNextWagon()) {
            searchWagon = searchWagon.getNextWagon();
        }

        return searchWagon;


    }

    /**
     * @return  the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        if (!hasNextWagon()){
            return 1;
        }

        checkSequenceForInvariants();

        Wagon searchWagon = nextWagon;
        int length = 2;


        while (searchWagon.hasNextWagon()) {
            searchWagon = searchWagon.getNextWagon();
            length++;
        }

        return length;


    }

    /**
     * Attaches the tail wagon and its connected successors behind this wagon,
     * if and only if this wagon has no wagon attached at its tail
     * and if the tail wagon has no wagon attached in front of it.
     * @param tail the wagon to attach behind this wagon.
     * @throws IllegalStateException if this wagon already has a wagon appended to it.
     * @throws IllegalStateException if tail is already attached to a wagon in front of it.
     *          The exception should include a message that reports the conflicting connection,
     *          e.g.: "%s is already pulling %s"
     *          or:   "%s has already been attached to %s"
     */
    public void attachTail(Wagon tail) {
        if (hasNextWagon()){
            throw new IllegalStateException(String.format("%s is already pulling %s", this, nextWagon));
        }

        if (tail.hasPreviousWagon()){
            throw new IllegalStateException(String.format("%s has already been attached to %s", tail, tail.getPreviousWagon()));
        }

        nextWagon = tail;
        tail.previousWagon = this;

    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     * @return the first wagon of the tail that has been detached
     *          or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if (!hasNextWagon()) {
            return null;
        }

        // Check if the next wagon is actually pulling this wagon
        // This is to prevent a wagon from detaching a wagon that is not pulling
        if (nextWagon.getPreviousWagon() != this ){
            throw new IllegalStateException(String.format("%s is attached to %s but %s is not pulling %s", this, nextWagon, nextWagon, this));
        }

        Wagon thisTail = nextWagon;
        nextWagon = null;
        thisTail.previousWagon = null;

        return thisTail;

    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     * @return  the former previousWagon that has been detached from,
     *          or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        if (!hasPreviousWagon()) {
            return null;
        }

        if (previousWagon.nextWagon != this){
            throw new IllegalStateException(String.format("%s is pulling to %s but %s is not attached to %s", this, previousWagon, previousWagon, this));
        }

        Wagon inFront = previousWagon;
        previousWagon = null;
        inFront.nextWagon = null;

        return inFront;

    }

    /**
     * Replaces the tail of the <code>front</code> wagon by this wagon and its connected successors
     * Before such reconfiguration can be made,
     * the method first disconnects this wagon form its predecessor,
     * and the <code>front</code> wagon from its current tail.
     * @param front the wagon to which this wagon must be attached to.
     */
    public void reAttachTo(Wagon front) {
        if (front == null){
            throw new IllegalArgumentException("front wagon cannot be null");
        }

        if (front == this) {
            throw new IllegalArgumentException("front wagon cannot be this wagon");
        }

        front.detachTail();
        this.detachFront();
        front.attachTail(this);
    }

    /**
     * Removes this wagon from the sequence that it is part of,
     * and reconnects its tail to the wagon in front of it, if any.
     */
    public void removeFromSequence() {

        //First wagon actions
        if (!hasPreviousWagon()) {
            detachTail();

            // Middle wagon actions
        } else if (hasNextWagon()) {
            nextWagon.reAttachTo(previousWagon);

            // Last wagon actions
        } else {
            detachFront();
        }

    }

    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        // No action
        if (!hasNextWagon()){
            return null;

            // Middle of sequence reverse
        } else if (hasPreviousWagon()) {

            checkSequenceForInvariants();

            Wagon lastWagon = getLastWagonAttached();
            Wagon thisNextWagon = nextWagon;
            Wagon thisPreviousWagon = previousWagon;

            detachFront();
            detachTail();

            reverseSequenceFromLast(lastWagon);

            // Last to rearange complete
            // Now attach the lastWagon to the previous wagon so this becomes the next wagon in the sequence
            // Attach this wagon to the last wagon of the new sequence so this becomes the last wagon
            thisNextWagon.attachTail(this);
            thisPreviousWagon.attachTail(lastWagon);

            return lastWagon;

        // First in sequence reverse.
        } else {

            checkSequenceForInvariants();

            return reverseSequenceFromLast(getLastWagonAttached());
        }
    }

    /**
     * Reverses the order in the sequence of wagons from the given Wagon until its final successor.
     * @param lastWagon the last wagon of the sequence to reverse
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */

    private Wagon reverseSequenceFromLast(Wagon lastWagon){
        Wagon editWagon = lastWagon;
        Wagon editNext;
        Wagon editPrevious;

        checkSequenceForInvariants();

        while (editWagon != null) {

            editNext = editWagon.getNextWagon();
            editPrevious = editWagon.getPreviousWagon();

            editWagon.setNextWagon(editPrevious);
            editWagon.setPreviousWagon(editNext);

            editWagon = editWagon.getNextWagon();

        }

        return lastWagon;

    }

    /**
     * Checks if the sequence of wagons contains a loop that is caused by invariant violations.
     */

    private void checkSequenceForInvariants(){
        Wagon checkWagon = this;
        Set<Wagon> visited = new HashSet<>();

        // Check the sequence forwards
        while (checkWagon.hasNextWagon()){
            if (!visited.add(checkWagon) ){
                throw new IllegalStateException(String.format("Wagon sequence contains a loop, caused near %s", checkWagon));
            }

            checkWagon = checkWagon.getNextWagon();
        }

        visited.clear();

        // Check the sequence backwards
        while (checkWagon.hasPreviousWagon()){
            if (!visited.add(checkWagon) ){
                throw new IllegalStateException(String.format("Wagon sequence contains a loop, caused near %s", checkWagon));
            }

            checkWagon = checkWagon.getPreviousWagon();
        }

    }



    @Override
    public String toString() {
        return String.format("[Wagon-%d]", id);
    }
}
