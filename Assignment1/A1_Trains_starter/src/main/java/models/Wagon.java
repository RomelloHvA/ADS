package models;

public class Wagon {
    protected int id;               // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
                                    // a.k.a. the successor of this wagon in a sequence
                                    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
                                    // a.k.a. the predecessor of this wagon in a sequence
                                    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon (int wagonId) {
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
        if (nextWagon != null){
            return true;
        }

        return false;
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        if (previousWagon != null){
            return true;
        }
        return false;
    }

    /**
     * Returns the last wagon attached to it,
     * if there are no wagons attached to it then this wagon is the last wagon.
     * @return  the last wagon
     */
    public Wagon getLastWagonAttached() {
        if (nextWagon != null){
            Wagon searchWagon = nextWagon;
            while (searchWagon.hasNextWagon()) {
                searchWagon = searchWagon.getNextWagon();
            }
            return searchWagon;
        }

        return this;
    }

    /**
     * @return  the length of the sequence of wagons towards the end of its tail
     * including this wagon itself.
     */
    public int getSequenceLength() {
        if (nextWagon != null){
            Wagon searchWagon = nextWagon;
            int length = 2;
            while (searchWagon.hasNextWagon()) {
                searchWagon = searchWagon.getNextWagon();
                length++;
            }
            return length;
        }
        return 1;
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
        if (nextWagon != null){
            throw new IllegalStateException(String.format("%s is already pulling %s", this, nextWagon));
        } else {
            if (tail.hasPreviousWagon()){
                throw new IllegalStateException(String.format("%s has already been attached to %s", tail, tail.getPreviousWagon()));
            } else {
                nextWagon = tail;
                tail.previousWagon = this;
            }
        }
    }

    /**
     * Detaches the tail from this wagon and returns the first wagon of this tail.
     * @return the first wagon of the tail that has been detached
     *          or <code>null</code> if it had no wagons attached to its tail.
     */
    public Wagon detachTail() {
        if (nextWagon != null) {
            Wagon tail = nextWagon;
            nextWagon = null;
            tail.previousWagon = null;
            return tail;
        }
        return null;
    }

    /**
     * Detaches this wagon from the wagon in front of it.
     * No action if this wagon has no previous wagon attached.
     * @return  the former previousWagon that has been detached from,
     *          or <code>null</code> if it had no previousWagon.
     */
    public Wagon detachFront() {
        if (previousWagon != null) {
            Wagon front = previousWagon;
            previousWagon = null;
            front.nextWagon = null;
            return front;
        }

        return null;
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
        } else if (front == this) {
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
        if (previousWagon != null){
            previousWagon.attachTail(nextWagon);
            this.detachFront();
            nextWagon = null;
        }
    }


    /**
     * Reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the wagon in front of this Wagon, if any.
     * No action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        if (nextWagon == null){
           return null;
        }
        Wagon newFrontWagon = this.getLastWagonAttached();

        Wagon wagonToAttach = newFrontWagon.detachFront();

        newFrontWagon.attachTail(wagonToAttach);

        wagonToAttach = wagonToAttach.getNextWagon();


        while (wagonToAttach != null){
            Wagon wagonToAttachPrevious = wagonToAttach.getNextWagon();
            Wagon wagonToAttachNext = wagonToAttach.getPreviousWagon();

            wagonToAttach.setNextWagon(wagonToAttachNext);
            wagonToAttach.setPreviousWagon(wagonToAttachPrevious);

            wagonToAttach = wagonToAttachNext;

        }


        return newFrontWagon;
    }

    // TODO string representation of a Wagon
}
