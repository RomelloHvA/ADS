package spotifycharts;

import org.junit.jupiter.api.Test;
import util.DataToCSV;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used to test the efficiency of the sorting algorithms.
 *
 * The test will run the sorting algorithms on different amounts of songs and
 * will stop when one of the algorithms takes longer than 20 seconds to sort
 * the songs.
 *
 * @author Marco de Boer
 */
public class EfficiencyTest {

    private static DataToCSV bubbleSortDataToCSV = new DataToCSV("BubbleSort");
    private static DataToCSV quickSortDataToCSV = new DataToCSV("QuickSort");
    private static DataToCSV heapSortDataToCSV = new DataToCSV("HeapSort");



    @Test
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Efficiency test\n");
        SorterImpl<Song> sorter = new SorterImpl<Song>();
        Comparator<Song> comparator = Song::compareByHighestStreamsCountTotal;
        ChartsCalculator chartsCalculator;
        chartsCalculator = new ChartsCalculator(20060423L);
        int amountOfSongs = 100;
        final int NUMBER_OF_RUNS = 10;
        int runNumber = 0;
        long durationBubble = 0;  //in milliseconds
        long durationQuick = 0;  //in milliseconds
        long durationHeap = 0;  //in milliseconds

        while(amountOfSongs < 5000000 && durationBubble < 20000 && durationQuick < 20000 && durationHeap < 20000) {
            System.out.println("Sorting run number: " + runNumber);

            for(int i = 0; i < NUMBER_OF_RUNS; i++) {
                chartsCalculator = new ChartsCalculator(20060423L + i);
                chartsCalculator.registerStreamedSongs(amountOfSongs);
                System.gc();
                durationBubble = bubbleSort(amountOfSongs, chartsCalculator.getSongs(), comparator, sorter, runNumber, i);
                durationQuick = quickSort(amountOfSongs, chartsCalculator.getSongs(), comparator, sorter, runNumber, i);
                durationHeap = heapSot(amountOfSongs, chartsCalculator.getSongs(), comparator, sorter, runNumber, i);
            }

            System.out.println();
            amountOfSongs *= 2;
            runNumber++;

        }

        bubbleSortDataToCSV.closeWriter();
        quickSortDataToCSV.closeWriter();
        heapSortDataToCSV.closeWriter();

    }

    /**
     * This method will sort the songs by highest streams count total using the bubble sort algorithm.
     * @param amountOfSongs you want to sort
     * @param songs you want to sort
     * @param comparator to compare the songs
     * @param sorter to sort the songs
     * @param runNumber the number of the run
     * @param runInRunNumber the number of the run in the run
     *@author Marco de Boer IS102 500902539
     * @return the duration of the sorting in milliseconds
     */
    private static long bubbleSort (int amountOfSongs, List<Song> songs, Comparator<Song> comparator, SorterImpl<Song> sorter, int runNumber, int runInRunNumber){
        List<Song> unsortedList = deepCopySongsList(songs);
        System.gc();
        long startTime = System.nanoTime();
        sorter.selInsBubSort(unsortedList, comparator);
        long finishTime = System.nanoTime();
        System.out.println("BubbleSorting " + amountOfSongs + " songs by highest streams count total took " + (finishTime - startTime)/1E6 + " miliseconds");
        bubbleSortDataToCSV.writeToFile(runNumber, runInRunNumber, amountOfSongs, ((finishTime - startTime)/1E6));
        return (long) ((finishTime - startTime)/1E6);
    }

    /**
     * This method will sort the songs by highest streams count total using the quick sort algorithm.
     * @param amountOfSongs you want to sort
     * @param songs you want to sort
     * @param comparator to compare the songs
     * @param sorter to sort the songs
     * @param runNumber the number of the run
     * @param runInRunNumber the number of the run in the run
     * @author Marco de Boer IS102 500902539
     * @return the duration of the sorting in milliseconds
     */
    private static long quickSort (int amountOfSongs, List<Song> songs, Comparator<Song> comparator, SorterImpl<Song> sorter, int runNumber, int runInRunNumber){
        List<Song> unsortedList = deepCopySongsList(songs);
        System.gc();
        long startTime = System.nanoTime();
        sorter.quickSort(unsortedList, comparator);
        long finishTime = System.nanoTime();
        System.gc();
        System.out.println("QuickSorting " + amountOfSongs + " songs by highest streams count total took " + (finishTime - startTime)/1E6 + " miliseconds");
        quickSortDataToCSV.writeToFile(runNumber, runInRunNumber, amountOfSongs, ((finishTime - startTime)/1E6));
        return (long) ((finishTime - startTime)/1E6);
    }

    /**
     * This method will sort the songs by highest streams count total using the heap sort algorithm.
     * @param amountOfSongs you want to sort
     * @param songs you want to sort
     * @param comparator to compare the songs
     * @param sorter to sort the songs
     * @param runNumber the number of the run
     * @param runInRunNumber the number of the run in the run
     * @author Marco de Boer
     * @return the duration of the sorting in milliseconds
     */
    private static long heapSot (int amountOfSongs, List<Song> songs, Comparator<Song> comparator, SorterImpl<Song> sorter, int runNumber, int runInRunNumber){
        List<Song> unsortedList = deepCopySongsList(songs);
        System.gc();
        long startTime = System.nanoTime();
        sorter.topsHeapSort(unsortedList.size(),unsortedList, comparator);
        long finishTime = System.nanoTime();
        System.out.println("HeapSorting " + amountOfSongs + " songs by highest streams count total took " + (finishTime - startTime)/1E6 + " miliseconds");
        heapSortDataToCSV.writeToFile(runNumber, runInRunNumber, amountOfSongs, ((finishTime - startTime)/1E6));
        return (long) ((finishTime - startTime)/1E6);
    }

    /**
     * This method will make a deep copy of the songs list.
     * @param originalList the list you want to copy
     * @author Marco de Boer
     * @return the copied list
     */
    private static List<Song> deepCopySongsList(List<Song> originalList) {
        List<Song> copiedList = new ArrayList<>();
        for (Song song : originalList) {
            copiedList.add(song.clone());
        }
        return copiedList;
    }
}
