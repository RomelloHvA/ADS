package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> sortOrder;   // the comparator that has been used with the latest sort
    protected int nSorted;                       // the number of sorted items in the first section of the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given sortOrder comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> sortOrder) {
        super();
        this.sortOrder = sortOrder;
        this.nSorted = 0;
    }

    public Comparator<? super E> getSortOrder() {
        return this.sortOrder;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.sortOrder = c;
        this.nSorted = this.size();
    }

    /**
     * Method for adding items to an arraylist. This checks if the index in inside the number of sorted items.
     * If it is nSorted gets updated. This means it is added to the sorted items. If the index lies outside of nSorted
     * the item will be added to the unsorted section.
     *
     * @param index index at which the specified element is to be inserted
     * @param item  element to be inserted
     * @author Romello ten Broeke
     */
    @Override
    public void add(int index, E item) {
        if(index > size()){
            index = size();
        }

        if (index <= nSorted) {
            nSorted = index;
        }
        super.add(index, item);
    }

    /**
     * Method for removing and item at the specified index. If the item is inside the nSorted. nSorted will become smaller.
     * If it is not it simply removes it.
     * @param index the index of the element to be removed
     * @return the object that was removed.
     * @author Romello ten Broeke
     */
    @Override
    public E remove(int index) {
        if (index <= nSorted) {
            nSorted--;
        }
        return super.remove(index);
    }

    /**
     * Method for removing a given object. If the index of said object is within the range of nSorted
     * it will decrease nSorted by one. If it is not it will try to remove it from the unsorted side.
     * @param object element to be removed from this list, if present
     * @return true if and object was removed and false if it was not removed.
     */
    @Override
    public boolean remove(Object object) {
        int objectIndex = indexOf(object);

        if (objectIndex <= nSorted && objectIndex >= 0) {
            nSorted--;
        }
        return super.remove(object);
    }


    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.sortOrder);
        }
    }

    @Override
    public int indexOf(Object item) {
        // efficient search can be done only if you have provided an sortOrder for the list
        if (this.getSortOrder() != null) {
            return indexOfByIterativeBinarySearch((E) item);
        } else {
            return super.indexOf(item);
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.sortOrder
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {

        if (searchItem == null) {
            return -1;
        }

        int from = 0;
        int to = nSorted - 1;

        while (from <= to) {

            int middle = (int) Math.ceil(((from + to) / 2.0));
            int compareResult = sortOrder.compare(searchItem, get(middle));

            if (compareResult == 0) {
                return middle;
            } else if (compareResult < 0) {
                to = middle - 1;
            } else {
                from = middle + 1;
            }
        }

        for (int i = nSorted; i < size(); i++) {
            if (sortOrder.compare(searchItem, get(i)) == 0) {
                return i;
            }
        }

        return -1; // nothing was found
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.sortOrder
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        int result = indexOfByRecursiveBinarySearchHelper(searchItem, 0, nSorted - 1);

        if (result != -1) {
            return result;
        }

        for (int i = nSorted; i < size(); i++) {
            if (sortOrder.compare(searchItem, get(i)) == 0) {
                return i;
            }
        }

        return -1;  // nothing was found ???
    }

    private int indexOfByRecursiveBinarySearchHelper(E searchItem, int from, int to){
        if(from > to) {
            return -1;
        }

        int midIndex = (int) Math.ceil((from + to) / 2.0);
        int compareResult = sortOrder.compare(searchItem, get(midIndex));

        if(compareResult == 0) {
            return midIndex;
        }else if (compareResult < 0) {
            return indexOfByRecursiveBinarySearchHelper(searchItem, from, midIndex - 1);
        } else {
            return indexOfByRecursiveBinarySearchHelper(searchItem, midIndex + 1, to);
        }
    }




    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     *
     * @param newItem
     * @param merger  a function that takes two items and returns an item that contains the merged content of
     *                the two items according to some merging rule.
     *                e.g. a merger could add the value of attribute X of the second item
     *                to attribute X of the first item and then return the first item
     * @return whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            E item = get(matchedItemIndex);
            E mergedItem = merger.apply(item, newItem);
            set(matchedItemIndex , mergedItem);

            return false;
        }
    }

    /**
     * calculates the total sum of contributions of all items in the list
     *
     * @param mapper a function that calculates the contribution of a single item
     * @return the total sum of all contributions
     */
    @Override
    public double aggregate(Function<E, Double> mapper) {
        double sum = 0.0;

        for (E item : this) {
            sum += mapper.apply(item);
        }

        return sum;
    }
}
