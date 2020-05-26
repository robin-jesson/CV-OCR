package main;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.Deskewing;
import processing.LetterDetection;
import processing.TextDetection;
import java.util.LinkedList;


public class Main {
    public static void main(String[] args){

        OpenCV.loadLocally();
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\cvr.png";
        Mat img = loadImage(imgSrc);
        LinkedList<Mat> rects = TextDetection.getTextBlock(img);
        for(Mat roi : rects) {
            Mat deskewed = Deskewing.deskew(roi);
            LetterDetection.detectLinesOfRoi(deskewed);

            break;
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
