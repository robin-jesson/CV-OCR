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

/**
 * Image tools.
 */
public class Image {
    /**
     * Loads an image into an OpenCV matrix.
     * @param imagePath  path
     * @param color  colour or black/white
     * @return matrix of the image
     * @throws NotFileException
     */
    public static Mat loadImage(String imagePath, boolean color) throws NotFileException {
        if(!Paths.get(imagePath).toFile().exists())
            throw new NotFileException();

        Imgcodecs imageCodecs = new Imgcodecs();
        if(color)
            return imageCodecs.imread(imagePath);
        else
            return imageCodecs.imread(imagePath, Imgcodecs.IMREAD_GRAYSCALE);
    }

    /**
     * Loads a coloured image
     * @param imagePath path
     * @return  matrix of the image
     * @throws NotFileException
     */
    public static Mat loadImage(String imagePath) throws NotFileException {
        return loadImage(imagePath,true);
    }

    /**
     * Saves an image into a file.
     * @param imageMatrix  matrix of the image
     * @param targetPath  path where to save
     */
    public static void saveImage(Mat imageMatrix, String targetPath) {
        if(!imageMatrix.empty()){
            Imgcodecs imgcodecs = new Imgcodecs();
            imgcodecs.imwrite(targetPath, imageMatrix);
        }
    }

    /**
     * Shows an image in a window.
     * @param src  mage
     * @param height  height of the window
     */
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

    /**
     * Shows an image into a window of 500 px.
     * @param src  image
     */
    public static void imshow(Mat src) {
        imshow(src,500);
    }

    /**
     * Resizes an image given the height.
     * @param src  image
     * @param height  new height
     * @return resized image
     */
    public static Mat resizeH(Mat src, int height){
        int w = src.width();
        int h = src.height();
        Size s = new Size((double)w*height/h,height);
        Mat dst = new Mat();
        Imgproc.resize(src,dst,s);
        return dst;
    }
}
