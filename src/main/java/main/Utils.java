package main;

import org.apache.commons.io.FileUtils;

import javax.annotation.processing.SupportedSourceVersion;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tools class.
 */
public class Utils {
    /**
     * Initialize the folders that will contain the letters.
     * Also create a blocs folder to contain RGB text blocks, badletters containing connected letters.
     */
    protected static void initFolder() {
        try {
            Path blocsPath = Paths.get("./roi/blocs");
            Path lettersPath = Paths.get("./roi/letters");
            Path badlettersPath = Paths.get("./roi/badletters");
            Path correctedlettersPath = Paths.get("./roi/correctedletters");
            FileUtils.deleteDirectory(blocsPath.toFile());
            FileUtils.deleteDirectory(lettersPath.toFile());
            FileUtils.deleteDirectory(badlettersPath.toFile());
            FileUtils.deleteDirectory(correctedlettersPath.toFile());
            Files.createDirectories(blocsPath);
            Files.createDirectories(lettersPath);
            Files.createDirectories(badlettersPath);
            Files.createDirectories(correctedlettersPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    /**
     * Write a pourcentage progressing bar.
     * @param curr  Current state
     * @param max  Maximum state
     * @param title Title of progress bar
     */
    public static void progressBar(int curr, int max, String title){
        int percent = (int)((double)curr/max*100);
        String pB = title + "|";
        for(int i = 0; i<percent; i++){
            pB+="#";
        }
        for(int i = percent;i<100;i++){
            pB+=" ";
        }
        pB+="|"+percent+"\r";
        System.out.print(pB);
        if(curr==max) System.out.println();
    }

    /**
     * Create a file name with two numbers converted to string with four digits.
     * @param k  First number
     * @param l  Second number
     * @return File name 0000_0000
     */
    protected static String createFilename(int k, int l){
        return createNumberString(k)+"_"+createNumberString(l);
    }

    /**
     * Create a string of four digits.
     * @param k  Number to convert to string
     * @return  k with four digits
     */
    protected static String createNumberString(int k){
        String s = "";
        if(k<10){
            s += "000";
        }
        else if(k<100){
            s += "00";
        }
        else if(k<1000){
            s += "0";
        }
        else {
            s += "";
        }
        return s + k;
    }

    protected static void write(String str){
        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            myWriter.write(str);
            myWriter.close();
        } catch (IOException e) {
            System.err.println("COuldn't write to file.");
        }
    }
}
