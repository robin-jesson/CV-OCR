package processing;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextDetection {
    public static void main(String[] args){
        OpenCV.loadLocally();

        final Size kernelSize = new Size(3, 3);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        String img="C:\\Users\\robin.jesson\\Desktop\\cvr.png";
        Mat loadedImage = loadImage(img);
        Mat other = loadImage(img);
        Imgproc.cvtColor(loadedImage, loadedImage, Imgproc.COLOR_BGRA2GRAY, 1);
        Imgproc.threshold(loadedImage,loadedImage,125,255,Imgproc.THRESH_OTSU);
        Core.bitwise_not( loadedImage, loadedImage );
        Imgproc.dilate(loadedImage, loadedImage, kernel,new Point(),10);
        saveImage(loadedImage, "dilate.jpg");


        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hier = new Mat();
        Imgproc.findContours(loadedImage, contours, hier, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        for(int i=0; i< contours.size();i++)
        {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            //System.out.println(rect);
            if (rect.width > 35 && rect.height > 35)
            {

                Imgproc.rectangle(other, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0));
            }
        }
        //Imgproc.rectangle(loadedImage, new Point(50,50), new Point(100,100),new Scalar(255,255,252));

        saveImage(other, "img.jpg");
    }

    public static Mat loadImage(String imagePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        return imageCodecs.imread(imagePath);
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs imgcodecs = new Imgcodecs();
        imgcodecs.imwrite(targetPath, imageMatrix);
    }

    public LinkedList<Rect> getTextBlock(Mat image){
        final Size kernelSize = new Size(3, 3);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        Mat loadedImage = image.clone();
        Imgproc.cvtColor(loadedImage, loadedImage, Imgproc.COLOR_BGRA2GRAY, 1);
        Imgproc.threshold(loadedImage,loadedImage,125,255,Imgproc.THRESH_OTSU);
        Core.bitwise_not( loadedImage, loadedImage );
        Imgproc.dilate(loadedImage, loadedImage, kernel,new Point(),10);
        saveImage(loadedImage, "dilate.jpg");


        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        LinkedList<Rect> rects = new LinkedList<>();
        Mat hier = new Mat();
        Imgproc.findContours(loadedImage, contours, hier, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        for(int i=0; i< contours.size();i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width > 35 && rect.height > 35){
                rects.add(rect);
               // Imgproc.rectangle(other, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0));
            }
        }
        return rects;
        //saveImage(other, "img.jpg");
    }

}
