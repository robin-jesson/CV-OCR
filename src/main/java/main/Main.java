package main;

import exception.DifferentSizeException;
import exception.NotFileException;
import exception.TooSmallWidthOrHeightException;
import org.opencv.imgproc.Imgproc;
import processing.extraction.LetterDetection;
import processing.extraction.TextDetection;
import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import processing.preprocessing.Denoising;
import processing.preprocessing.Deskewing;
import processing.preprocessing.PageDetection;
import processing.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Main {

    static{
        OpenCV.loadLocally();
        initFolder();
    }

    public static void main(String[] args) throws NotFileException {
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\img\\iphone.jpg";
        Mat img = Image.loadImage(imgSrc);
        Mat warped = PageDetection.detectAndCropPage(img);
        Mat bw = Denoising.removeShadowAndBinarize(warped);
        LinkedList<Mat> textzones = TextDetection.getTextBlock(bw);
        int progress = 0;
        int wordCount=0;
        int letterCount = 0;
        for(Mat roi : textzones) {
            progressBar(progress,textzones.size()-1);
            Mat deskewed = Deskewing.deskewByHough(roi);
            //Image.saveImage(deskewed,"roi/deskew/"+i+".png");
            LinkedList<Mat> lines = LetterDetection.detectLinesOfRoi(deskewed);

            for(Mat line : lines){
                if(!line.empty()){
                    List<Mat> words = LetterDetection.detectWordsOfLine(line);
                    for(Mat word : words){
                        //Image.saveImage(word,"roi/words/"+createNumberString(wordCount)+".png");
                        try {
                            List<Mat> letters = LetterDetection.detectLettersOfWord(word);

                            for(Mat letter : letters){
                                Image.saveImage(letter,"roi/letters/"+createFilename(wordCount,letterCount++)+".png");
                            }

                        }
                        catch (TooSmallWidthOrHeightException e) {}
                        catch (DifferentSizeException e) {
                            //System.err.println(e);
                            //Image.imshow(word,500);
                        }
                    }
                    wordCount++;
                    letterCount = 0;
                }
            }
            progress++;
        }
    }

    /**
     * initialize folder to store temporary image
     */
    public static void initFolder() {
        try {
            //Path cropPath = Paths.get("./roi/crop");
            //Path deskewPath = Paths.get("./roi/deskew");
            //Path wordsPath = Paths.get("./roi/words");
            Path lettersPath = Paths.get("./roi/letters");
            //FileUtils.deleteDirectory(cropPath.toFile());
            //FileUtils.deleteDirectory(deskewPath.toFile());
            //FileUtils.deleteDirectory(wordsPath.toFile());
            FileUtils.deleteDirectory(lettersPath.toFile());
            //Files.createDirectories(cropPath).toAbsolutePath();
            //Files.createDirectories(deskewPath);
            //Files.createDirectories(wordsPath);
            Files.createDirectories(lettersPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    /**
     * Write a pourcentage progressing bar.
     * @param curr  current state
     * @param max  maximum state
     */
    private static void progressBar(int curr, int max){
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

    private static String createFilename(int k, int l){
        return createNumberString(k)+"_"+createNumberString(l);
    }

    private static String createNumberString(int k){
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
