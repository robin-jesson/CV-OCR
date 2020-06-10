package processing;

import exception.NotFileException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class Image {
    public static Mat loadImage(String imagePath) throws NotFileException {
        if(!Paths.get(imagePath).toFile().exists())
            throw new NotFileException();

        Imgcodecs imageCodecs = new Imgcodecs();
        return imageCodecs.imread(imagePath);
        /*try{
            return imageCodecs.imread(imagePath);
        }
        catch(UnsatisfiedLinkError e){
            throw e;
        }*/
    }

    public static void saveImage(Mat imageMatrix, String targetPath) {
        if(!imageMatrix.empty()){
            Imgcodecs imgcodecs = new Imgcodecs();
            imgcodecs.imwrite(targetPath, imageMatrix);
        }
    }

    public static void imshow(Mat src, int height) {
        Mat img = Image.resizeH(src,height);
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

    public static void imshow(Mat src) {
        imshow(src,500);
    }

    public static Mat resizeH(Mat src, int height){
        int w = src.width();
        int h = src.height();
        Size s = new Size((double)w*height/h,height);
        Mat dst = new Mat();
        Imgproc.resize(src,dst,s);
        return dst;
    }
}
