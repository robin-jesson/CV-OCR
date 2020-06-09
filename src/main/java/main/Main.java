package main;

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
import java.util.LinkedList;


public class Main {

    static{
        OpenCV.loadLocally();
        initFolder();
    }

    public static void main(String[] args){
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\ipad.jpg";
        Mat img = Image.loadImage(imgSrc);
        Mat warped = PageDetection.detectAndCropPage(img);
        Mat bw = Denoising.removeShadowAndBinarize(warped);
        LinkedList<Mat> rects = TextDetection.getTextBlock(bw);
        int i = 0;
        for(Mat roi : rects) {
            progressBar(i,rects.size()-1);
            Mat deskewed = Deskewing.deskewByHough(roi);
            //deskewed = Deskewing.deskewByRotatedRect(deskewed);
            Image.saveImage(deskewed,"roi/deskew/"+i+".png");
            LinkedList<Mat> lines = LetterDetection.detectLinesOfRoi(deskewed);
            int j = 0;
            for(Mat line : lines){
                if(!line.empty()){
                    Mat line2 = LetterDetection.erodeLetters(line);
                    Image.saveImage(line,"roi/crop/"+i+"_"+ j++ +".png");
                }
            }
            j = 0;
            i++;
        }
    }

    public static void initFolder() {
        try {
            Path cropPath = Paths.get("./roi/crop");
            Path deskewPath = Paths.get("./roi/deskew");
            FileUtils.deleteDirectory(cropPath.toFile());
            FileUtils.deleteDirectory(deskewPath.toFile());
            Files.createDirectories(cropPath).toAbsolutePath();
            Files.createDirectories(deskewPath);

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    private static void progressBar(int curr, int max){
        String pB = "|";
        for(int i = 0; i<curr; i++){
            pB+="#";
        }
        for(;curr<max;curr++){
            pB+=" ";
        }
        pB+="|\r";
        System.out.print(pB);
    }


}
