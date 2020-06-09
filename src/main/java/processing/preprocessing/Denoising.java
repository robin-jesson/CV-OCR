package processing.preprocessing;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import processing.Image;

public class Denoising {
    /**
     * https://stackoverflow.com/questions/44047819/increase-image-brightness-without-overflow/44054699#44054699
     * @param args
     */
    public static Mat removeShadowAndBinarize(Mat im) {

        //Mat im = Image.loadImage("C:\\Users\\robin.jesson\\Desktop\\ipad.jpg");
        //Image.imshow(im);


        Mat gray = new Mat();
        Imgproc.cvtColor(im,gray,Imgproc.COLOR_BGR2GRAY);
        //Image.imshow(gray);

        Mat dilated_img  = new Mat();
        final Size kernelSize = new Size(3, 3);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        Imgproc.dilate(gray, dilated_img, kernel,new Point(),10);
        //Image.imshow(dilated_img);

        Mat bg_img = new Mat();
        Imgproc.GaussianBlur(dilated_img,bg_img,new Size(5,5),0);
        //Image.imshow(bg_img);

        Mat diff_img = new Mat();
        Core.absdiff(gray,bg_img,diff_img);
        Core.absdiff(diff_img,new Scalar(255),diff_img);
        //Image.imshow(diff_img);

        Mat norm_img = new Mat();
        Core.normalize(diff_img,norm_img,0,255, Core.NORM_MINMAX, CvType.CV_8UC1);
        //Image.imshow(norm_img);

        Mat thr_img = new Mat();
        Imgproc.threshold(norm_img,thr_img,0,255,Imgproc.THRESH_OTSU);
        return thr_img;
        //Core.normalize(thr_img,thr_img,0,255,Core.NORM_MINMAX, CvType.CV_8UC1);
        //Image.imshow(thr_img);
        //Image.saveImage(thr_img,"bruit.png");
    }
}
