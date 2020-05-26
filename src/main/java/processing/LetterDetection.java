package processing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.LinkedList;

public class LetterDetection {
    public static LinkedList<Mat> detectLinesOfRoi(Mat roi){
        LinkedList<Mat> lines = new LinkedList<Mat>();
        Mat m = new Mat();
        for(int i=0;i<roi.rows();i++){
            Mat row = roi.row(i);
            Core.MinMaxLocResult res = Core.minMaxLoc(row);
            System.out.println(res.maxVal);

        }
        return lines;
    }

    public static LinkedList<Mat> detectWordsOfLine(Mat line){
        LinkedList<Mat> words = new LinkedList<Mat>();
        return words;
    }

    public static LinkedList<Mat> detectLettersOfWord(Mat word){
        LinkedList<Mat> letters = new LinkedList<Mat>();
        //hauteur >= largeur
        return letters;
    }
}
