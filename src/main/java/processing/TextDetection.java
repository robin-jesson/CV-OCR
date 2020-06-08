package processing;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextDetection {

    /**
     * Trouve les zones de texte en dilatant les contours des lettres.
     * @param image image RGB ou NG
     * @return Liste de matrice RGB ou NG selon format d'entrée
     */
    public static LinkedList<Mat> getTextBlock(Mat image){
        final Size kernelSize = new Size(3, 3);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        Mat loadedImage = image.clone();
        Imgproc.cvtColor(loadedImage, loadedImage, Imgproc.COLOR_BGRA2GRAY, 1);
        Imgproc.threshold(loadedImage,loadedImage,125,255,Imgproc.THRESH_OTSU);
        Core.bitwise_not( loadedImage, loadedImage );
        Imgproc.dilate(loadedImage, loadedImage, kernel,new Point(),10);
        //saveImage(loadedImage, "dilate.jpg");


        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        LinkedList<Mat> rois = new LinkedList<>();
        Mat hier = new Mat();
        Imgproc.findContours(loadedImage, contours, hier, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        for(int i=0; i< contours.size();i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (rect.width > 35 && rect.height > 35){
                rois.add(new Mat(image,rect));
                //Imgproc.rectangle(image, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0));
            }
        }
        //Image.saveImage(image, "textzones.jpg");
        return rois;

    }

}
