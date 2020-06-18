package processing.preprocessing;

import exception.AeraException;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import processing.Image;

import java.util.ArrayList;
import java.util.List;

public class PageDetection {

    /**
     * Detect the rectagle paper in an image.
     * If none is found, then the original image is returned.
     * @param image  original picture of a paper page.
     * @return  either the page warped or the original page.
     */
    public static Mat detectAndCropPage(Mat image) {

        double ratio = image.rows()/500;
        Mat orig = image.clone();
        image = Image.resizeH(image,500);

        Mat gray = new Mat();
        Imgproc.cvtColor(image,gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray,gray,new Size(5,5),0);
        Mat edged = new Mat();
        Imgproc.Canny(gray,edged,75,200);


        List<MatOfPoint> cnts = new ArrayList<>();
        Mat hier = new Mat();
        Imgproc.findContours(edged.clone(),cnts,hier, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        MatOfPoint2f screenCnt2f = new MatOfPoint2f();
        MatOfPoint screenCnt = new MatOfPoint();
        double biggestPeri = -1.0;
        for(MatOfPoint mP : cnts){
            MatOfPoint2f c = new MatOfPoint2f( mP.toArray() );
            double peri = Imgproc.arcLength(c,true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(c, approx,0.02*peri,true);
            if(approx.rows()==4){
                if(peri>biggestPeri){
                    biggestPeri = peri;
                    screenCnt = mP;
                    screenCnt2f = approx;
                }
            }
        }
        List<MatOfPoint> l = new ArrayList<>();
        l.add(screenCnt);
        MatOfPoint2f ptsScaled = new MatOfPoint2f();
        Core.multiply(screenCnt2f,new Scalar(ratio,ratio),ptsScaled);
        Mat warped = new Mat();
        try {
            warped = fourPointTransform(orig,ptsScaled.toList());
        } catch (AeraException e) {
            warped = orig;
        }
        Image.saveImage(warped,"warped.png");

        return warped;

    }

    /**
     * Return the top left point
     * @param pts  four-point array
     * @return  top left point
     */
    private static Point getTopLeft(List<Point> pts){
        double min= Double.MAX_VALUE;
        Point tl = pts.get(0);
        for(Point p : pts){
            if(p.x+p.y<min){
                min = p.x-p.y;
                tl = p;
            }
        }
        return tl;
    }

    /**
     * Return the bottom right point
     * @param pts  four-point array
     * @return  bottom right point
     */
    private static Point getBottomRight(List<Point> pts){
        double max= Double.MIN_VALUE;
        Point br = pts.get(0);
        for(Point p : pts) {
            if (p.x + p.y > max) {
                max = p.x + p.y;
                br = p;
            }
        }
        return br;
    }

    /**
     * Return the top right point
     * @param pts  four-point array
     * @return  top right point
     */
    private static Point getTopRight(List<Point> pts){
        double min= Double.MAX_VALUE;
        Point tr = pts.get(0);
        for(Point p : pts){
            if(p.x-p.y<min){
                min = p.x-p.y;
                tr = p;
            }
        }
        return tr;
    }

    /**
     * Return the bottom left point
     * @param pts  four-point array
     * @return  bottom left point
     */
    private static Point getBottomLeft(List<Point> pts){
        double max= Double.MIN_VALUE;
        Point bl = pts.get(0);
        for(Point p : pts){
            if(p.x-p.y>max){
                max = p.x-p.y;
                bl = p;
            }
        }
        return bl;
    }

    /**
     * Order the four-point array as so :
     * <ol>
     *     <li>top left</li>
     *     <li>bottom left</li>
     *     <li>bottom right</li>
     *     <li>top right</li>
     * </ol>
     * @param pts  four-point array
     * @return four-point array ordered
     */
    private static List<Point> orderPoints(List<Point> pts){
        List<Point> rect = new ArrayList<>();
        rect.add(getTopLeft(pts));
        rect.add(getBottomLeft(pts));
        rect.add(getBottomRight(pts));
        rect.add(getTopRight(pts));
        return rect;
    }

    /**
     * Warp the image according to the four points found.
     * @param image  image to be transformed
     * @param pts  four-point array
     * @return  image transformed according to the four points
     * @throws AeraException
     */
    private static Mat fourPointTransform(Mat image, List<Point> pts) throws AeraException {
        List<Point> rect = orderPoints(pts);

        Point tl = rect.get(0);
        Point tr = rect.get(1);
        Point br = rect.get(2);
        Point bl = rect.get(3);

        double widthA = Math.sqrt((br.x-bl.x)*(br.x-bl.x)+(br.y-bl.y)*(br.y-bl.y));
        double widthB = Math.sqrt((tr.x-tl.x)*(tr.x-tl.x)+(tr.y-tl.y)*(tr.y-tl.y));
        double maxWidth = Math.max(widthA,widthB);

        double heightA = Math.sqrt((tr.x-br.x)*(tr.x-br.x)+(tr.y-br.y)*(tr.y-br.y));
        double heightB = Math.sqrt((tl.x-bl.x)*(tl.x-bl.x)+(tl.y-bl.y)*(tl.y-bl.y));
        double maxHeight = Math.max(heightA,heightB);

        if(maxHeight * maxWidth < (image.width()*image.height()*0.70))
            throw new AeraException();

        MatOfPoint2f src = new MatOfPoint2f(rect.get(0), rect.get(1), rect.get(2), rect.get(3));
        MatOfPoint2f dst = new MatOfPoint2f(new Point(0,0), new Point(maxWidth-1,0),
                new Point(maxWidth-1,maxHeight-1), new Point(0,maxHeight-1));
        Mat M = Imgproc.getPerspectiveTransform(src,dst);
        Mat warped = new Mat();
        Imgproc.warpPerspective(image,warped,M,new Size(maxWidth,maxHeight));
        return warped;
    }


}
