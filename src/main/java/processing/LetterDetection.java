package processing;

import main.Main;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;

public class LetterDetection {

    private static Mat createFakeColNB(){
        Mat m = new Mat(15,1,CvType.CV_8U);
        double[] da = new double[]{0,0,0,255,255,255,255,255,0,0,0,255,255,255,255};
        for(int i=0;i<da.length;i++)
            m.put(i,0, new double[]{da[i]});
        return m;
    }

    private static Mat addBegEndRowsToMat(Mat mat){
        Mat begLine = new Mat(1,mat.cols(),CvType.CV_8U, Scalar.all(0));
        Mat endLine = new Mat(1,mat.cols(),CvType.CV_8U, Scalar.all(0));
        Mat matFlat = mat.reshape(1);
        begLine.push_back(matFlat);
        begLine.push_back(endLine);
        begLine.reshape(mat.cols());
        System.out.println(matFlat.size());
        return begLine;
    }

    public static LinkedList<Mat> detectLinesOfRoi(Mat roi){
        LinkedList<Mat> lines = new LinkedList<Mat>();
        Mat colNB = new Mat(roi.rows(),1, CvType.CV_8U);
        for(int i=0;i<roi.rows();i++){
            Mat row = roi.row(i);
            Core.MinMaxLocResult res = Core.minMaxLoc(row);
            colNB.put(i,0, new double[]{res.maxVal});
        }
        //Mat colNB = createFakeColNB();
        colNB = addBegEndRowsToMat(colNB);
        ArrayList<Integer> beginsLines = new ArrayList<>();
        ArrayList<Integer> endsLines = new ArrayList<>();
        boolean isInLine = false;
        for(int i=0;i<colNB.rows();i++){
            double color = colNB.get(i,0)[0];
            if(!isInLine && color==255.0){
                isInLine = true;
                beginsLines.add(i);
                //System.out.println("début "+ (i));
            }
            else if(isInLine && color==0.0){
                isInLine = false;
                endsLines.add(i);
                //System.out.println("fin "+i);
            }
            else if(isInLine && i==colNB.rows()-1){
                endsLines.add(i);
                //System.out.println("fin "+colNB.rows());
            }
        }
        Mat augmentedROI = addBegEndRowsToMat(roi);
        for(int i=0;i<beginsLines.size();i++){
            Rect rect = new Rect(new Point(0,beginsLines.get(i)), new Point(roi.cols(),endsLines.get(i)));
            Mat lineROI = new Mat(augmentedROI,rect);
            lines.add(cropLine(lineROI));
        }
        return lines;
    }

    public static Mat cropLine(Mat line){
        int beginR =0;
        int endR=0;
        int beginC=0;
        int endC  = 0;
        for(int i=0;i<line.rows();i++){
            if(Core.minMaxLoc(line.row(i)).maxVal!=0.0){
                beginR = i;
                break;
            }
        }
        for(int i=line.rows()-1;i>=0;i--){
            if(Core.minMaxLoc(line.row(i)).maxVal!=0.0){
                endR = i;
                break;
            }
        }
        for(int i=0;i<line.cols();i++){
            if(Core.minMaxLoc(line.col(i)).maxVal!=0.0){
                beginC = i;
                break;
            }
        }
        for(int i=line.cols()-1;i>=0;i--){
            if(Core.minMaxLoc(line.col(i)).maxVal!=0.0){
                endC = i;
                break;
            }
        }
        Rect rect = new Rect(new Point(beginC,beginR), new Point(endC,endR));
        //System.out.println(rect);
        return new Mat(line,rect);
    }

    public static LinkedList<Mat> detectWordsOfLine(Mat lineROI){
        LinkedList<Mat> words = new LinkedList<Mat>();
        return words;
    }

    public static LinkedList<Mat> detectLettersOfWord(Mat wordROI){
        LinkedList<Mat> letters = new LinkedList<Mat>();
        //hauteur >= largeur
        return letters;
    }
}
