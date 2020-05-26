package main;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import processing.Deskewing;
import processing.TextDetection;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;


public class Main {
    public static void main(String[] args){

        OpenCV.loadLocally();
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\cvr.png";
        Mat img = loadImage(imgSrc);
        LinkedList<Mat> rects = TextDetection.getTextBlock(img);
        int i=0;
        for(Mat roi : rects) {
            //saveImage(roi, ".\\roi\\nor_" + i++ + ".png");
            Mat rot = Deskewing.deskew(roi);
            //saveImage(rot, ".\\roi\\rot_" + i++ + ".png");

        }
    }

    public static Mat loadImage(String imagePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        try{
            return imageCodecs.imread(imagePath);
        }
        catch(UnsatisfiedLinkError e){
            System.err.println("erreur de lecture de l'image "+imagePath);
            System.exit(0);
        }
        return null;
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs imgcodecs = new Imgcodecs();
        imgcodecs.imwrite(targetPath, imageMatrix);
    }
}
