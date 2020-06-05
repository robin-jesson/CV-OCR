package main;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.Deskewing;
import processing.LetterDetection;
import processing.PageDetection;
import processing.TextDetection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;


public class Main {

    static{
        OpenCV.loadLocally();
    }

    public static void main(String[] args){

        //OpenCV.loadLocally();
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\iphone.jpg";
        //String imgSrc="C:\\Users\\robin.jesson\\Downloads\\EnglishFnt\\EnglishFnt\\English\\Fnt\\Sample018\\img018-00042.png";
        Mat img = loadImage(imgSrc);
        Mat warped = PageDetection.detectAndCropPage(img);
        LinkedList<Mat> rects = TextDetection.getTextBlock(warped);
        int i = 0;
        for(Mat roi : rects) {
            progressBar(i,rects.size()-1);
            //saveImage(roi,"roi/skew/"+i+".png");
            Mat deskewed = Deskewing.deskew(roi);
            saveImage(deskewed,"roi/deskew/"+i+".png");
            LinkedList<Mat> lines = LetterDetection.detectLinesOfRoi(deskewed);
            int j = 0;
            for(Mat line : lines){
                saveImage(line,"roi/crop/"+i+"_"+ j++ +".png");
            }
            j = 0;
            i++;
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
        if(!imageMatrix.empty()){
            Imgcodecs imgcodecs = new Imgcodecs();
            imgcodecs.imwrite(targetPath, imageMatrix);
        }
    }



    public static void imshow(Mat img) {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
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
