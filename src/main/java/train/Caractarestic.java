package train;

import exception.NotDividibleException;
import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.ml.TrainData;
import processing.Image;
import processing.extraction.LetterDetection;

import java.io.File;
import java.nio.file.Paths;

/**
 * Caracteristic vectors and KNN model
 */
public class Caractarestic {
    /**
     * Create the vector from a letter in black and white.
     * @param letter  image
     * @param charNb  character
     * @param step
     * @return vector creatd from the letter
     * @throws NotDividibleException  step must divide 32
     */
    private static int[] getVector(Mat letter, int charNb ,int step) throws NotDividibleException {
        if(32%step!=0) throw new NotDividibleException();
        int[] vec = new int[(32/step) * (32/step)];
        Mat m = new Mat();
        String c = ((char)charNb) + "[";
        /*Mat gray = new Mat();
        Imgproc.cvtColor(letter,gray,Imgproc.COLOR_BGR2GRAY);
        Mat bw = new Mat();
        Imgproc.threshold(gray,bw,0,255,Imgproc.THRESH_OTSU);*/
        Mat resized = new Mat();
        Imgproc.resize(letter, resized, new Size(32,32));
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
        //System.out.println(c);
        return vec;
    }

    /**
     * Train a KNN with the training images located in the folders.
     * @param save  to save the model or not
     * @param folders  list of folders where the trianing image are located
     * @return  OpenCV's KNN model
     * @throws NotDividibleException
     */
    private static KNearest trainKnn(boolean save, File... folders) throws NotDividibleException {
        int nbFile = 0;
        for(int i = 0; i < folders.length; i++)
            for(File f : folders[i].listFiles())
                for(File pic : f.listFiles())
                    nbFile++;

        Mat traindata = new Mat(new Size(16,nbFile),CvType.CV_32F);
        Mat labels = new Mat(new Size(1,nbFile),CvType.CV_32F);
        int line = 0;
        for(int i = 0; i < folders.length; i++){
            File[] subFolders = folders[i].listFiles();
            boolean isPonct = folders[i].getName().equals("ponct");
            for(int j=0;j<subFolders.length;j++){
                int charName = isPonct ?
                        (int)Ponct.valueOf(subFolders[j].getName()).getC() :
                        subFolders[j].getName().charAt(0);
                File[] pics = subFolders[j].listFiles();
                for(int k = 0; k < pics.length; k++){
                    labels.put(line,0,charName);
                    try {
                        Mat pic = Image.loadImage(pics[k].getAbsolutePath(), false);
                        int[] vec = getVector(pic,charName,8);
                        for(int l=0;l<vec.length;l++){
                            traindata.put(line,l,vec[l]);
                        }
                    } catch (NotFileException e) {/* continue if not a file */}
                    line++;
                }
            }
        }

        KNearest knn = KNearest.create();
        knn.train(traindata, Ml.ROW_SAMPLE, labels);
        knn.save("knn.yml");
        return knn;
    }

    public static void main(String[] args) throws Exception {
        OpenCV.loadLocally();
        File num = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\num").toFile();
        File maj = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\maj").toFile();
        File ponct = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\ponct").toFile();

        KNearest knn = trainKnn(true, num,maj,ponct);
        File numtest = Paths.get("C:\\Users\\robin.jesson\\Desktop\\numtest").toFile();
        double total = 0;
        double totalFound = 0;
        for(File fold : numtest.listFiles()){
            System.out.print(fold.getName() + " : ");
            double sum = 0;
            for(File img : fold.listFiles()){
                Mat res = new Mat();
                Mat testdata = new Mat(new Size(16,1),CvType.CV_32F);
                Mat lettToTest = Image.loadImage(img.getAbsolutePath(), false);
                int[] vec = getVector(LetterDetection.cropROI(lettToTest),'0',8);
                for(int i=0;i<vec.length;i++){
                    testdata.put(0,i,vec[i]);
                }
                float p = knn.findNearest(testdata,6,res);
                char c = (char)((int)p);
                System.out.print(c+ " ");
                if(c==fold.getName().charAt(0))
                    sum++;
                total++;
            }
            totalFound += sum;
            System.out.println("-> " + (int)(sum/fold.listFiles().length * 100) + "%");
        }
        System.out.println("TOTAL : "+(int)(totalFound/total*100)+"%");
/*
        Mat res = new Mat();
        Mat testdata = new Mat(new Size(16,1),CvType.CV_32F);

        Mat lettToTest = Image.loadImage("C:\\Users\\robin.jesson\\Desktop\\numtest\\12_1_3_3.png", false);
        int[] vec = getVector(LetterDetection.cropROI(lettToTest),'0',8);
        for(int i=0;i<vec.length;i++){
            testdata.put(0,i,vec[i]);
        }


        float p = knn.findNearest(testdata,5,res);
        System.out.println((char)((int)p));*/
    }
}
