package processing;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Deskewing {
    public static void main(String[] args){
        OpenCV.loadLocally();
        String img="C:\\Users\\robin.jesson\\Desktop\\pench3.png";
        Mat loadedImage = loadImage(img);
        Mat gray = new Mat();
        Imgproc.cvtColor(loadedImage, gray, Imgproc.COLOR_BGRA2GRAY, 1);
        Core.bitwise_not(gray,gray);
        Mat thresh = new Mat();
        Imgproc.threshold(gray,thresh,0,255,Imgproc.THRESH_OTSU);

        RotatedRect rect = Imgproc.minAreaRect(getPoints(thresh));
        double angle = rect.angle;
        if(angle<-45)
            angle = -(90+angle);
        else
            angle = -angle;

        Size size=loadedImage.size();
        double h = size.height;
        double w = size.width;
        Mat rotated = new Mat();
        Mat M = Imgproc.getRotationMatrix2D(new Point(h/2,w/2),angle,1.0);
        Imgproc.warpAffine(loadedImage, rotated, M,loadedImage.size(),Imgproc.INTER_CUBIC);
        saveImage(rotated,"rotated.jpg");

    }

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
        //saveImage(rotated,"rotated.jpg");
        return rotated;
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
