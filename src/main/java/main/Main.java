package main;

import nu.pattern.OpenCV;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;


public class Main {

    static{
        OpenCV.loadLocally();
        initFolder();
    }

    public static void main(String[] args){
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\ipad.jpg";
        Mat img = Image.loadImage(imgSrc);
        Mat warped = PageDetection.detectAndCropPage(img);
        LinkedList<Mat> rects = TextDetection.getTextBlock(warped);
        int i = 0;
        for(Mat roi : rects) {
            progressBar(i,rects.size()-1);
            Mat deskewed = Deskewing.deskew(roi);
            Image.saveImage(deskewed,"roi/deskew/"+i+".png");
            LinkedList<Mat> lines = LetterDetection.detectLinesOfRoi(deskewed);
            int j = 0;
            for(Mat line : lines){
                Image.saveImage(line,"roi/crop/"+i+"_"+ j++ +".png");
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
            pB+="=";
        }
        for(;curr<max;curr++){
            pB+=" ";
        }
        pB+="|\r";
        System.out.print(pB);
    }


}
