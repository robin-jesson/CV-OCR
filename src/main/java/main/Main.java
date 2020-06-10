package main;

import exception.NotFileException;
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
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\cvr.png";
        Mat img = null;
        try {
            img = Image.loadImage(imgSrc);
        } catch (NotFileException e) {
            e.printStackTrace();
            System.exit(0);
        }
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
                    Mat lineDil = LetterDetection.dilateLetters(line, 3);
                    List<MatOfPoint> contours = new ArrayList<>();
                    Mat hier = new Mat();
                    Imgproc.findContours(lineDil, contours, hier, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
                    for(int k=0; k< contours.size();k++) {
                        Rect rect = Imgproc.boundingRect(contours.get(k));
                        if(rect.height>line.height()/2){
                            rect = new Rect(new Point(rect.x,0),new Point(rect.x+rect.width,line.height()));
                            Imgproc.rectangle(line, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0));

                        }

                    }
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
