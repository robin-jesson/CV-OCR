package train;

import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.ml.TrainData;
import processing.Image;

import java.io.File;
import java.nio.file.Paths;

public class Caractarestic {
    private static int[] getVector(Mat letter, int charNb ,int step) throws Exception {
        if(32%step!=0) throw new Exception("Step must divide 32");
        int[] vec = new int[(32/step) * (32/step)];
        Mat m = new Mat();
        String c = ((char)charNb) + "[";
        Mat gray = new Mat();
        Imgproc.cvtColor(letter,gray,Imgproc.COLOR_BGR2GRAY);
        Mat bw = new Mat();
        Imgproc.threshold(gray,bw,0,255,Imgproc.THRESH_OTSU);
        Mat resized = new Mat();
        Imgproc.resize(bw, resized, new Size(32,32));
        int idx = 0;
        for(int i=0;i<32;i+=step){
            for(int j=0;j<32;j+=step){
                Rect r = new Rect(new Point(i,j),new Point(i+step,j+step));
                Mat temp = new Mat(resized,r);
                int whitePix = Core.countNonZero(temp);
                vec[idx++]=whitePix;
                if(i!=0 || j!=0) c+=",";
                c+=whitePix;
            }
        }
        c+="]";
        System.out.println(c);
        return vec;
    }

    public static void main(String[] args) throws Exception {
        OpenCV.loadLocally();
        File folder = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\ponct\\").toFile();
        Mat traindata = new Mat(new Size(16,folder.listFiles().length),CvType.CV_32F);
        Mat labels = new Mat(new Size(1,folder.listFiles().length),CvType.CV_32F);
        for(File f : folder.listFiles()){
            int charName = (int)Ponct.valueOf(f.getName()).getC();
            int line = 0;
            labels.put(line,0,charName);
            for(File pic : f.listFiles()){
                try {
                    Mat im = Image.loadImage(pic.getAbsolutePath());
                    int[] vec = getVector(im,charName,8);
                    for(int i=0;i<vec.length;i++){
                        traindata.put(line,i,vec[i]);
                    }
                }
                catch (NotFileException ex){/* do nothing */}
            }
            line++;
        }
        KNearest knn = KNearest.create();
        knn.train(traindata, Ml.ROW_SAMPLE, labels);
        knn.save("ocr.txt");
        Mat res = new Mat();
        Mat testdata = new Mat(new Size(16,folder.listFiles().length),CvType.CV_32F);
        int[] vec = new int[]{0,20,35,25,47,56,52,24,8,30,44,35,40,50,40,9};
        for(int i=0;i<vec.length;i++){
            testdata.put(0,i,vec[i]);
        }

        float p = knn.findNearest(testdata,3,res);
        System.out.println(res.dump());
    }
}
