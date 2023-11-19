package util;

import java.io.File;
import java.io.PrintWriter;

/**
 * This class is used to write the data to a CSV file.
 * The CSV file will be saved in the efficiencydata folder.
 * The file will be named Test + current time in seconds + _ + filename + .csv
 * The file will contain the following columns:
 * RunNumber; RuninRunNumber; N (Amount); Time to Sort (ms)
 * The file will be closed when the closeWriter() method is called.
 * @author Marco de Boer
 */
public class DataToCSV {

    PrintWriter writer;
    private File file;

    public DataToCSV(String filename){
        String filenameWithDate = "Test" + (int)System.currentTimeMillis()/1000 + "_" + filename + ".csv";
        String directory = "efficiencydata"; // One level up and then into efficiencydata
        String fullPath = directory + File.separator + filenameWithDate;

        try {
          file = new File(fullPath);
          file.getParentFile().mkdirs();
          writer = new PrintWriter(file);
          writer.println("RunNumber; RuninRunNumber; N (Amount); Time to Sort (ms)");

        } catch (Exception e) {
            System.err.println("Something went wrong while creating the file!");
        }
    }

    /**
     * This method writes the data to the file.
     * @param runNumber number of the run
     * @param runInRunNumber number of the run in the run
     * @param amount amount of songs
     * @param time time it took to sort the songs in milliseconds
     * @author Marco de Boer
     */
    public void writeToFile(int runNumber, int runInRunNumber, int amount, double time){
        final String COMMASEPERATOR = ";";
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(runNumber).append(COMMASEPERATOR);
            stringBuilder.append(runInRunNumber).append(COMMASEPERATOR);
            stringBuilder.append(amount).append(COMMASEPERATOR);
            String timeString = Double.toString(time);
            timeString = timeString.replace(".",",");
            stringBuilder.append(timeString);
            writer.println(stringBuilder);
            writer.flush();
        } catch (Exception e) {
            System.err.println("Something went wrong while writing to the file!");
        }

    }

    /**
     * This method closes the writer.
     */
    public void closeWriter() {
        if (writer != null) {
            writer.close();
        }
    }
}
