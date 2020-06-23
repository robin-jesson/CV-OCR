package processing.extraction;

import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import processing.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LettersSeparation {

    /*public static void main(String[] args) throws IOException, NotFileException {
        File f = Paths.get("C:\\Users\\robin.jesson\\Documents\\GitHub\\CV-OCR\\roi\\letters\\15_2_2_1.png").toFile(); //pé
        Mat img = Image.loadImage(f.getAbsolutePath(),false);
        Path dir = Paths.get("C:\\Users\\robin.jesson\\Documents\\GitHub\\CV-OCR\\roi\\letters");

        int idx = getIndex(dir,f);
        System.out.println(idx);
        File[] around = getAroundFiles(idx,20, dir.toFile().listFiles());
        System.out.println(List.of(around));
        double m = getMeanWidth(around);
        System.out.println("mean="+m);
        System.out.println("img width="+img.width());
        long nbLtters = Math.round(img.width()/m);
        System.out.println("lettre detectée="+img.width()/m+" -> " +nbLtters);

        long widthCut = img.width()/nbLtters;

        for(int x=0;x<nbLtters*widthCut;x+=widthCut){
            System.out.println(x+" "+(x+widthCut));
            Rect rect = new Rect(
                    new Point(x,0),
                    new Point(x+widthCut,img.height())
            );
            Image.imshow(new Mat(img, rect),100);
        }





    }*/

    public static List<Mat> serparate(Path dir, File f) throws IOException, NotFileException {
        Mat img = Image.loadImage(f.getAbsolutePath(),false);
        int idx = getIndex(dir,f);
        System.out.println(idx);
        File[] around = getAroundFiles(idx,20, dir.toFile().listFiles());
        double m = getMeanWidth(around);
        long nbLtters = Math.round(img.width()/m);
        long widthCut = img.width()/nbLtters;
        List<Mat> letters = new LinkedList<>();
        for(int x=0;x<nbLtters*widthCut;x+=widthCut){
            System.out.println(x+" "+(x+widthCut));
            Rect rect = new Rect(new Point(x,0),new Point(x+widthCut,img.height()));
            letters.add(new Mat(img,rect));
        }
        return letters;

    }

    private static int getIndex(Path dir, File f) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(dir, (entry) -> { return !Files.isDirectory(entry); } );
        int idx = 0;
        for(Path p : stream){
            if(p.toFile().equals(f))
                return idx;
            idx++;
        }
        return -1;
    }

    private static File[] getAroundFiles(int index , int shift, File[] dir){

        int j = 0;
        int shiftLeft = (index<shift)?index:shift;
        int shiftRight = (index+shift>dir.length)?dir.length-index-1 : shift;
        File[] around = new File[shiftLeft+shiftRight];

        for(int i=index-shiftLeft; i<index; i++)
            around[j++] = dir[i];
        for(int i=index+1; i<=index+shiftRight; i++)
            around[j++] = dir[i];

        return around;
    }

    private static double getMeanWidth(File[] around) throws NotFileException {
        double s = 0.0;
        for(int i=0;i<around.length;i++){
            Mat m = Image.loadImage(around[i].getAbsolutePath(),false);
            s += m.width();
        }
        return s/around.length;
    }
}
