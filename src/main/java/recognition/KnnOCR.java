package recognition;

import exception.NotDividibleException;
import exception.NotFileException;
import main.Utils;
import nu.pattern.OpenCV;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.ml.KNearest;
import org.w3c.dom.Text;
import processing.Image;
import processing.extraction.LetterDetection;
import train.Caractarestic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class KnnOCR implements IRecognition{



    static{
        OpenCV.loadLocally();
    }

    @Override
    public String recognize() throws IOException {
        System.out.println("Recognition");
        String res = "";
        var knn = KNearest.load("knn.yml");
        var dir = Paths.get("roi/letters");
        var paths = Files.newDirectoryStream(dir);
        var dirSize = dir.toFile().listFiles().length;
        String currentWordNum = null;
        int curr = 0;
        for(Path path: paths){
            var imgFile = path.toFile();
            var wordNum = FilenameUtils.removeExtension(imgFile.getName()).split("_")[0];
            if(currentWordNum==null || !currentWordNum.equals(wordNum)) {
                currentWordNum = wordNum;
                //System.out.println(currentWordNum);
                res += "\n";
            }

            //System.out.print(imgFile.getName()+" -> ");
            try {
                Mat pic = Image.loadImage(imgFile.getAbsolutePath(), false);
                Mat resKnn = new Mat();
                Mat testdata = new Mat(new Size(49,1), CvType.CV_32F);
                int[] vec = Caractarestic.getVector(LetterDetection.cropROI(pic));
                for(int i=0;i<vec.length;i++){
                    testdata.put(0,i,vec[i]);
                }
                float p = knn.findNearest(testdata,9,resKnn);
                //System.out.println((char)p);
                res += (char)((int)p);
            } catch (NotFileException e) {
            }
            Utils.progressBar(++curr,dirSize);
        }

        return res;
    }
}
