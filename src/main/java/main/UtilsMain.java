package main;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UtilsMain {
    /**
     * initialize folder to store temporary image
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
     * @param curr  current state
     * @param max  maximum state
     */
    public static void progressBar(int curr, int max){
        int percent = (int)((double)curr/max*100);
        String pB = "|";
        for(int i = 0; i<percent; i++){
            pB+="#";
        }
        for(int i = percent;i<100;i++){
            pB+=" ";
        }
        pB+="|"+percent+"\r";
        System.out.print(pB);
    }

    protected static String createFilename(int k, int l){
        return createNumberString(k)+"_"+createNumberString(l);
    }

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
}
