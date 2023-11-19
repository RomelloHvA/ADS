package spotifycharts;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SorterImpl<E> implements Sorter<E> {

    /**
     * Sorts all items by selection or insertion sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> selInsBubSort(List<E> items, Comparator<E> comparator) {
        int n = items.size();
         // Initialize if there has been a swap.
        boolean isSwapped = false;
        for(int i=0; i < n; i++){
            isSwapped = false;
            for(int j=1; j < (n-i); j++){
                if(comparator.compare(items.get(j - 1), items.get(j)) > 0) {
                    isSwapped = true;
                    //swap elements
                    swap(items, j-1, j);
                }

            }
            //If at the end of the loop there have been no swaps, the items are already sorted.
            if (!isSwapped) {
                break;
            }
        }
        return items;
    }

    /**
     * Helper function to swap to items
     * @param items that need to be swapped
     * @param i index of the first item
     * @param j index of the second item
     */

    private void swap(List<E> items, int i, int j) {
        E tempItem = items.get(i);
        items.set(i, items.get(j));
        items.set(j, tempItem);
    }



    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */

    private int partition (List<E> items, int low, int high, Comparator<E> comparator){
        E pivot = items.get(high);

        int i = (low - 1);

        for (int j = low; j <= high ; j++) {

            if (comparator.compare(items.get(j), pivot) < 0) {
                i++;
                swap(items,i,j);
            }
        }
        swap(items, i + 1, high);
        return (i + 1);
    }

    /**
     * Helper function to recursively sort the items
     * @param items that need to be sorted
     * @param low index of the first item
     * @param high index of the second item
     * @param comparator to compare the items
     */

    private void recursiveQuickSort(List<E> items, int low, int high, Comparator<E> comparator) {
        {
            if (low < high) {

                // is now at right place
                int partitionIndex = partition(items, low, high, comparator);
                // Separately sort elements before
                // partition and after partition
                recursiveQuickSort(items, low, partitionIndex - 1, comparator);
                recursiveQuickSort(items, partitionIndex + 1, high, comparator);
            }
        }
    }
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        recursiveQuickSort(items,0, items.size() - 1, comparator);
        return items;
    }

    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     * @param numTops       the size of the lead collection of items to be found and sorted
     * @param items
     * @param comparator
     * @return              the items list with its first numTops items sorted according to comparator
     *                      all other items >= any item in the lead collection
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {
        // the lead collection of numTops items will be organised into a (zero-based) heap structure
        // in the first numTops list positions using the reverseComparator for the heap condition.
        // that way the root of the heap will contain the worst item of the lead collection
        // which can be compared easily against other candidates from the remainder of the list
        Comparator<E> reverseComparator = comparator.reversed();

        // initialise the lead collection with the first numTops items in the list
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // insert remaining items into the lead collection as appropriate
        for (int i = numTops; i < items.size(); i++) {
            // loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure
            //  the root of the heap is the currently trailing item in the lead collection,
            //  which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // item < worstLeadItem, so shall be included in the lead collection
                items.set(0, item);
                // demote worstLeadItem back to the tail collection, at the orginal position of item
                items.set(i, worstLeadItem);
                // repair the heap condition of the lead collection
                heapSink(items, numTops, reverseComparator);
            }
        }
        // the first numTops positions of the list now contain the lead collection
        // the reverseComparator heap condition applies to this lead collection
        // now use heapSort to realise full ordening of this collection
        for (int i = numTops-1; i > 0; i--) {
            // loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection
            // position 0 holds the root item of a heap of size i+1 organised by reverseComparator
            // this root item is the worst item of the remaining front part of the lead collection
            swap(items, 0, i);

            heapSink(items, i, reverseComparator);
        }

        return items;
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        for (int i = heapSize -1; i > 0; i--){
            if (comparator.compare(items.get(i), items.get((i-1)/2)) < 0){
                swap(items, i, (i-1)/2);
            }
        }
    }
    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        int parentIndex = 0;
        int childIndex = 2 * parentIndex + 1;
        E sinker = items.get(parentIndex);

        while(childIndex < heapSize){
            if(childIndex + 1 < heapSize && comparator.compare(items.get(childIndex), items.get(childIndex + 1)) > 0){
                childIndex++;
            }
            if(comparator.compare(sinker, items.get(childIndex)) < 0){
                break;
            }
            items.set(parentIndex, items.get(childIndex));
            items.set(childIndex, sinker);
            parentIndex = childIndex;
            childIndex = 2 * parentIndex + 1;
        }
    }
}
