package processing.extraction;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        return begLine;
    }

    private static Mat addBegEndColsToMat(Mat mat){
        Mat begLine = new Mat(mat.cols(),1,CvType.CV_8U, Scalar.all(0));
        Mat endLine = new Mat(mat.cols(),1,CvType.CV_8U, Scalar.all(0));
        Mat matFlat = mat.reshape(1);
        begLine.push_back(matFlat);
        begLine.push_back(endLine);
        begLine.reshape(mat.cols()+2);
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
            lines.add(cropROI(lineROI));
        }
        return lines;
    }

    public static Mat cropROI(Mat roi){
        int beginR =0;
        int endR=0;
        int beginC=0;
        int endC  = 0;
        for(int i=0;i<roi.rows();i++){
            if(Core.minMaxLoc(roi.row(i)).maxVal!=0.0){
                beginR = i;
                break;
            }
        }
        for(int i=roi.rows()-1;i>=0;i--){
            if(Core.minMaxLoc(roi.row(i)).maxVal!=0.0){
                endR = i;
                break;
            }
        }
        for(int i=0;i<roi.cols();i++){
            if(Core.minMaxLoc(roi.col(i)).maxVal!=0.0){
                beginC = i;
                break;
            }
        }
        for(int i=roi.cols()-1;i>=0;i--){
            if(Core.minMaxLoc(roi.col(i)).maxVal!=0.0){
                endC = i;
                break;
            }
        }
        Rect rect = new Rect(new Point(beginC,beginR), new Point(endC,endR));
        //System.out.println(rect);
        return new Mat(roi,rect);
    }

    public static LinkedList<Mat> detectWordsOfLine(Mat lineROI){
        LinkedList<Mat> words = new LinkedList<Mat>();
        Mat lineDil = LetterDetection.dilateLetters(lineROI, 3);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();
        Imgproc.findContours(lineDil, contours, hier, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        for(int k=0; k< contours.size();k++) {
            Rect rect = Imgproc.boundingRect(contours.get(k));
            if(rect.height>lineROI.height()/2){
                Rect wordRect = new Rect(new Point(rect.x,0),new Point(rect.x+rect.width,lineROI.height()));
                //Imgproc.rectangle(lineROI, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),new Scalar(255,0,0));
                words.add(new Mat(lineROI,wordRect));
            }

        }
        return words;
    }

    public static Mat erodeLetters(Mat roi){
        Mat erod = new Mat();
        final Size kernelSize = new Size(3, 3);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        Imgproc.erode(roi, erod, kernel,new Point(),1);
        return erod;
    }

    public static Mat dilateLetters(Mat roi, int inter){
        Mat dil = new Mat();
        final Size kernelSize = new Size(3, 3);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, kernelSize);
        Imgproc.dilate(roi, dil, kernel,new Point(),inter);
        return dil;
    }

    public static LinkedList<Mat> detectLettersOfWord(Mat wordROI){
        LinkedList<Mat> letters = new LinkedList<Mat>();
        //hauteur >= largeur
        Mat rowNB = new Mat(1,wordROI.cols(), CvType.CV_8U);
        for(int i=0;i<wordROI.cols();i++){
            Mat col = wordROI.col(i);
            Core.MinMaxLocResult res = Core.minMaxLoc(col);
            rowNB.put(0,i, new double[]{res.maxVal});
        }
        rowNB = addBegEndColsToMat(rowNB);
        ArrayList<Integer> beginsLetters = new ArrayList<>();
        ArrayList<Integer> endsLetters = new ArrayList<>();
        boolean isInLetter = false;
        for(int i=0;i<rowNB.cols();i++){
            double color = rowNB.get(0,i)[0];
            if(!isInLetter && color==255.0){
                isInLetter = true;
                beginsLetters.add(i);
                //System.out.println("début "+ (i));
            }
            else if(isInLetter && color==0.0){
                isInLetter = false;
                endsLetters.add(i);
                //System.out.println("fin "+i);
            }
            else if(isInLetter && i==rowNB.rows()-1){
                endsLetters.add(i);
                //System.out.println("fin "+colNB.rows());
            }
        }
        Mat augmentedROI = addBegEndColsToMat(wordROI);
        for(int i=0;i<beginsLetters.size();i++){
            Rect rect = new Rect(new Point(beginsLetters.get(i),0), new Point(endsLetters.get(i),wordROI.rows()));
            Mat lineROI = new Mat(augmentedROI,rect);
            letters.add(cropROI(lineROI));
        }
        return letters;
    }
}
