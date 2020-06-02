package processing;

import com.recognition.software.jdeskew.ImageDeskew;
import main.Main;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import javax.crypto.spec.PSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Deskewing {

    /**
     * Modifie l'angle du texte pour que celui-ci soit horizontale.
     * @param roi matrice d'entrée RGB ou NG
     * @return matrice BIN avec fond noir(0.0) et lettre blanche (255.0)
     */
    public static Mat deskew(Mat roi){
        Mat loadedImage = roi.clone();
        Imgproc.cvtColor(loadedImage, loadedImage, Imgproc.COLOR_BGRA2GRAY, 1);
        Core.bitwise_not(loadedImage,loadedImage);
        Imgproc.threshold(loadedImage,loadedImage,0,255,Imgproc.THRESH_OTSU);

        RotatedRect rect = Imgproc.minAreaRect(getPoints(loadedImage));
        double angle = rect.angle;
        if(angle<-45)
            angle = -(90+angle);
        else
            angle = -angle;

        double h = loadedImage.height();
        double w = loadedImage.width();
        Mat rotated = new Mat();
        Mat M = Imgproc.getRotationMatrix2D(new Point(h/2,w/2),angle,1.0);
        Imgproc.warpAffine(loadedImage, rotated, M,loadedImage.size(),Imgproc.INTER_CUBIC);

        Core.bitwise_not(rotated,rotated);



        Imgproc.threshold(rotated,rotated,0,255,Imgproc.THRESH_OTSU);
        //Main.saveImage(rotated,"rotated.jpg");
        return rotated;
    }

    public static Mat deskew2(Mat im, int count){
        int max_skew = 10;
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
            //System.out.println(Math.atan2(y2-y1,x2-x1));
            angles.add(Math.atan2(y2-y1,x2-x1));
        }
        ArrayList<Double> angles2 = new ArrayList<>();

            for(double angle : angles){
                if(Math.abs(angle)<Math.toRadians(max_skew))
                    angles2.add(angle);
            }


        //System.out.println(angles2.size());
        if(angles2.size()<5)
            return im_bw;


        double angle_deg = Math.toDegrees(median(angles2));
        //System.out.println(angle_deg);
        Mat rotated = new Mat();
        Mat M = Imgproc.getRotationMatrix2D(new Point(width/2,height/2),angle_deg,1.0);
        Imgproc.warpAffine(im_bw, rotated, M,im.size(),Imgproc.INTER_LINEAR, Core.BORDER_REPLICATE);
        Imgproc.threshold(rotated,rotated,0,255,Imgproc.THRESH_OTSU);
        return rotated;
    }

    private static double median(ArrayList<Double> values){
        Double[] array = new Double[values.size()];
        array = values.toArray(array);
        Arrays.sort(array);
        double median;
        if(values.size()%2==0)
            median = ((double)array[array.length/2] + (double)array[array.length/2 - 1])/2;
        else
            median = (double) array[array.length/2];
        return median;
    }


    private static MatOfPoint2f getPoints(Mat m){
        List<Point> pts = new ArrayList<>();
        MatOfPoint2f mat = new MatOfPoint2f();
        Size size=m.size();
        //System.out.println(size.width*size.height);
        for(int i=0;i<size.height;i++){
            for(int j=0;j<size.width;j++){
                if(m.get(i,j)[0]>0){
                    pts.add(new Point(i,j));
                }
            }
        }
        mat.fromList(pts);
        //System.out.println(mat.size());
        return mat;
    }

    public static Mat loadImage(String imagePath) {
        Imgcodecs imageCodecs = new Imgcodecs();
        return imageCodecs.imread(imagePath);
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        Imgcodecs imgcodecs = new Imgcodecs();
        imgcodecs.imwrite(targetPath, imageMatrix);
    }

    public static Mat load(String imagePath) {
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


}
