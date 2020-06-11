package main;

import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.features2d.MSER;
import org.opencv.imgproc.Imgproc;
import processing.Image;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

public class SmallPgrLetterExtr {
    public static void main(String[] args) throws NotFileException {
        OpenCV.loadLocally();
        Mat img = Image.loadImage("C:\\Users\\robin.jesson\\Desktop\\cvr.png");
        double h = img.height();
        double w = img.width();
        double image_size = h*w;
        MSER mser = MSER.create();
        mser.setMaxArea((int)image_size/2);
        mser.setMinArea(10);

        Mat gray = new Mat();
        Imgproc.cvtColor(img,gray,Imgproc.COLOR_BGR2GRAY);
        Mat bw = new Mat();
        Imgproc.threshold(gray,bw,0,255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        List<MatOfPoint> regions = new LinkedList<>();
        MatOfRect rects = new MatOfRect();
        mser.detectRegions(bw,regions,rects);

        for(Rect rect : rects.toList()){
            int x = rect.x;
            int y = rect.y;
            w = rect.width;
            h = rect.height;

            Imgproc.rectangle(img, new Point(x,y), new Point(x+w,y+h),new Scalar(255,0,255),1);
        }

        Image.saveImage(img,"C:\\Users\\robin.jesson\\Desktop\\11jiun.png");
    }
}
