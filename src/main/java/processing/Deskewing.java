package processing;

import com.recognition.software.jdeskew.ImageDeskew;
import main.Main;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deskewing {

    public static Mat deskew(Mat im){
        int max_skew = 20;
        int height = im.height();
        int width = im.width();

        Mat im_gs = new Mat();
        Imgproc.cvtColor(im, im_gs, Imgproc.COLOR_BGRA2GRAY, 1);
        Photo.fastNlMeansDenoising(im_gs,im_gs,3);

        Mat im_bw = new Mat();
        Imgproc.threshold(im_gs,im_bw,0,255,Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(im_bw,lines,1,Math.PI/180,200,width/12,width/150);

        ArrayList<Double> angles = new ArrayList<>();
        for(int i=0;i<lines.rows();i++){
            Mat line = lines.row(i);
            double x1 = line.get(0,0)[0];
            double y1 = line.get(0,0)[1];
            double x2 = line.get(0,0)[2];
            double y2 = line.get(0,0)[3];
            angles.add(Math.atan2(y2-y1,x2-x1));
        }
        ArrayList<Double> angles2 = new ArrayList<>();

        for(double angle : angles){
            if(Math.abs(angle)<Math.toRadians(max_skew))
                angles2.add(angle);
        }

        if(angles2.size()<5)
            return im_bw;


        double angle_deg = Math.toDegrees(median(angles2));
        //Main.imshow(im_bw);

        Mat rotated = new Mat();
        Mat M = Imgproc.getRotationMatrix2D(new Point(width/2,height/2),angle_deg,1.0);
        Imgproc.warpAffine(im_bw, rotated, M,im.size(),Imgproc.INTER_LINEAR);
        Imgproc.threshold(rotated,rotated,0,255,Imgproc.THRESH_OTSU);
        return rotated;
    }

    private static double median(ArrayList<Double> values){
        Double[] array = new Double[values.size()];
        array = values.toArray(array);
        Arrays.sort(array);
        double median;
        if(values.size()%2==0)
            return (array[array.length/2] + array[array.length/2 - 1])/2;
        else
            return array[array.length/2];
    }


}
