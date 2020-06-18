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

    public static void main(String[] args){
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\20200618_155642000_iOS.jpg";
        Mat img = null;
        try {
            img = Image.loadImage(imgSrc);
        } catch (NotFileException e) {
            e.printStackTrace();
            System.exit(0);
        }
        Mat warped = PageDetection.detectAndCropPage(img);
        Mat bw = Denoising.removeShadowAndBinarize(warped);
        LinkedList<Mat> textzones = TextDetection.getTextBlock(bw);
        int i = 0;
        for(Mat roi : textzones) {
            progressBar(i,textzones.size()-1);
            Mat deskewed = Deskewing.deskewByHough(roi);
            Image.saveImage(deskewed,"roi/deskew/"+i+".png");
            LinkedList<Mat> lines = LetterDetection.detectLinesOfRoi(deskewed);
            int j = 0;
            for(Mat line : lines){
                int k=0;
                if(!line.empty()){
                    Image.saveImage(line,"roi/crop/"+i+"_"+ j++ +".png");
                    List<Mat> words = LetterDetection.detectWordsOfLine(line);
                    int l = 0;
                    for(Mat word : words){
                        Image.saveImage(LetterDetection.cropROI(word),"roi/words/"+i+"_"+ j +"_"+ k++ +".png");
                        try {
                            List<Mat> letters = LetterDetection.detectLettersOfWord(word);
                            for(Mat letter : letters){
                                Image.saveImage(letter,"roi/letters/"+i+"_"+ j +"_"+ k + "_"+ l++ +".png");
                            }
                        } catch (TooSmallWidthOrHeightException | DifferentSizeException e) {/* do nothing */}
                        l=0;
                    }
                }
                k=0;
            }
            j = 0;
            i++;
        }
    }

    public static void initFolder() {
        try {
            Path cropPath = Paths.get("./roi/crop");
            Path deskewPath = Paths.get("./roi/deskew");
            Path wordsPath = Paths.get("./roi/words");
            Path lettersPath = Paths.get("./roi/letters");
            FileUtils.deleteDirectory(cropPath.toFile());
            FileUtils.deleteDirectory(deskewPath.toFile());
            FileUtils.deleteDirectory(wordsPath.toFile());
            FileUtils.deleteDirectory(lettersPath.toFile());
            Files.createDirectories(cropPath).toAbsolutePath();
            Files.createDirectories(deskewPath);
            Files.createDirectories(wordsPath);
            Files.createDirectories(lettersPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

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


}
