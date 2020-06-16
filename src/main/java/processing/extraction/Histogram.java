package processing.extraction;

import com.lowagie.text.html.simpleparser.Img;
import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import processing.Image;

import java.util.ArrayList;

public class Histogram {
    public static int[] horizontalHist(Mat m){
        int[] h = new int[m.width()];
        for(int i=0;i<m.width();i++){
            Mat col = m.col(i);
            h[i] = Core.countNonZero(col);
        }
        return h;
    }

    public static void main(String[] args) throws NotFileException {
        OpenCV.loadLocally();
        Mat img = Image.loadImage("C:\\Users\\robin.jesson\\Desktop\\domaine.png");
        int maxHeight = img.height()/4;
        Mat gray = new Mat();
        Imgproc.cvtColor(img,gray,Imgproc.COLOR_BGR2GRAY);
        int[] hist = horizontalHist(gray);
        int minIdx = -1;
        int minVal = Integer.MAX_VALUE;
        ArrayList<Integer> minIdxs = new ArrayList<>();
        for(int i=0;i<hist.length;i++) {
            System.out.print(hist[i] + " ");
            /*if(hist[i]< maxHeight && hist[i]<minVal){
                minIdx = i;
                minVal = hist[i];
            }*/
            if(hist[i]< maxHeight){
                minIdxs.add(i);
            }
        }
        //System.out.println("idx="+minIdx+" val="+minVal);
        for(int i=0;i<minIdxs.size();i++) {
            Imgproc.line(img, new Point(minIdxs.get(i), 0), new Point(minIdxs.get(i), img.height()), new Scalar(255, 0, 0));
        }
        Image.imshow(img,200);
    }
}
