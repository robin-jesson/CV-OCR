package processing;

import main.Main;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class PageDetection {
    public static void main(String[] args) {
        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\iphone.jpg";

        Mat image = Main.loadImage(imgSrc);
        double ratio = image.rows()/500;
        Mat orig = image.clone();
        image = PageDetection.resizeH(image,500);

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
                //System.out.println(biggestPeri);
            }

        }
        List<MatOfPoint> l = new ArrayList<>();
        l.add(screenCnt);
        //System.out.println("4 points non triés");
        /*for(Point p : screenCnt2f.toList()){
            //System.out.println("("+p.x+", "+p.y+") +:"+(p.x+p.y)+" -:"+(p.x-p.y));
            //Imgproc.drawMarker(image,p,new Scalar(255,0,0),Imgproc.MARKER_CROSS,10,3);
        }*/

        //System.out.println("4 points triés");
        /*for(Point p : orderPoints(screenCnt2f.toList())){
            //System.out.println("("+p.x+", "+p.y+")");
            //Imgproc.drawMarker(image,p,new Scalar(255,0,0),Imgproc.MARKER_CROSS,10,3);
            //Main.imshow(image);
        }*/
        Mat warped = fourPointTransform(image,screenCnt2f.toList(),ratio);
        Main.saveImage(warped,"warped.png");



    }

    public static Mat resizeH(Mat src, int height){
        int w = src.width();
        int h = src.height();
        Size s = new Size((double)w*height/h,height);
        Mat dst = new Mat();
        Imgproc.resize(src,dst,s);
        return dst;
    }

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

    private static List<Point> orderPoints(List<Point> pts){
        List<Point> rect = new ArrayList<>();
        rect.add(getTopLeft(pts));
        rect.add(getBottomLeft(pts));
        rect.add(getBottomRight(pts));
        rect.add(getTopRight(pts));
        return rect;
    }

    private static Mat fourPointTransform(Mat image, List<Point> pts, double ratio){
        List<Point> rect = orderPoints(pts);
        //System.out.println(rect);

        Point tl = rect.get(0);
        Point tr = rect.get(1);
        Point br = rect.get(2);
        Point bl = rect.get(3);

        double widthA = Math.sqrt(
                (br.x-bl.x)*(br.x-bl.x)+(br.y-bl.y)*(br.y-bl.y)
        );
        double widthB = Math.sqrt(
                (tr.x-tl.x)*(tr.x-tl.x)+(tr.y-tl.y)*(tr.y-tl.y)
        );
        double maxWidth = Math.max(widthA,widthB);
        double heightA = Math.sqrt(
                (tr.x-br.x)*(tr.x-br.x)+(tr.y-br.y)*(tr.y-br.y)
        );
        double heightB = Math.sqrt(
                (tl.x-bl.x)*(tl.x-bl.x)+(tl.y-bl.y)*(tl.y-bl.y)
        );
        double maxHeight = Math.max(heightA,heightB);

        /*List<Point> dst = new ArrayList<>();
        dst.add(new Point(0,0));
        dst.add(new Point(maxWidth-1,0));
        dst.add(new Point(maxWidth-1,maxHeight-1));
        dst.add(new Point(0,maxHeight-1));*/
        //printMat(listPointToMat(rect));
        MatOfPoint2f src = new MatOfPoint2f(
                rect.get(0),
                rect.get(1),
                rect.get(2),
                rect.get(3)
        );

        MatOfPoint2f dst = new MatOfPoint2f(
                new Point(0,0),
                new Point(maxWidth-1,0),
                new Point(maxWidth-1,maxHeight-1),
                new Point(0,maxHeight-1)
        );

        Mat M = Imgproc.getPerspectiveTransform(src,dst);
        Mat warped = new Mat();
        Imgproc.warpPerspective(image,warped,M,new Size(maxWidth,maxHeight));
        return warped;
    }

    private static void printMat(Mat m){
        for(int i=0;i<m.rows();i++){
            for(int j=0;j<m.cols();j++){
                System.out.print(m.get(i,j)[0]+" ");
            }
            System.out.println();
        }
    }

    private static Mat listPointToMat(List<Point> pts){
        Mat m = new Mat(new Size(2,4),CvType.CV_16U);
        for(int i=0;i<pts.size();i++){
            //System.out.println(pts.get(i));
            m.put(i,0,pts.get(i).x);
            m.put(i,1,pts.get(i).y);
        }
        System.out.println(m.size());
        return m;
    }
}
