package processing.preprocessing;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import processing.Image;

public class Denoising {
    /**
     * https://stackoverflow.com/questions/44047819/increase-image-brightness-without-overflow/44054699#44054699
     * @param im
     */
    public static Mat removeShadowAndBinarize(Mat im) {
        Mat gray = new Mat();
        Imgproc.cvtColor(im,gray,Imgproc.COLOR_BGR2GRAY);

        //dilate the image to remove the text
        Mat dilated_img  = new Mat();
        final Size kernelSize = new Size(3, 3);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        Imgproc.dilate(gray, dilated_img, kernel,new Point(),10);

        //gaussian blur the dilated image unite the background (shadow still appearing)
        Mat bg_img = new Mat();
        Imgproc.GaussianBlur(dilated_img,bg_img,new Size(5,5),0);

        //do the difference between original and new background previously obtained
        //close pixel value will be white and the rest will be black
        Mat diff_img = new Mat();
        Core.absdiff(gray,bg_img,diff_img);
        Core.absdiff(diff_img,new Scalar(255),diff_img);

        //normalize to have a full dynamic range
        Mat norm_img = new Mat();
        Core.normalize(diff_img,norm_img,0,255, Core.NORM_MINMAX, CvType.CV_8UC1);

        //binarize the picture (black text white background)
        Mat thr_img = new Mat();
        Imgproc.threshold(norm_img,thr_img,0,255,Imgproc.THRESH_OTSU);
        return thr_img;
    }
}
