package train;

import exception.NotDividibleException;
import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
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
     * @param letter  image 1 channel
     * @return vector creatd from the letter
     * @throws NotDividibleException  step must divide 32
     */
    /*public static int[] getVector(Mat letter, int step) throws NotDividibleException {
        if(32%step!=0) throw new NotDividibleException();
        int[] vec = new int[(32/step) * (32/step)];
        Mat m = new Mat();
        //String c = ((char)charNb) + "[";

        //Mat gray = new Mat();
        //Imgproc.cvtColor(letter,gray,Imgproc.COLOR_BGR2GRAY);
        Mat bw = new Mat();
        Imgproc.threshold(letter,bw,0,255,Imgproc.THRESH_OTSU);
        Mat resized = new Mat();
        try {
            Imgproc.resize(bw, resized, new Size(32, 32));

            int idx = 0;
            for(int i=0;i<32;i+=step){
                for(int j=0;j<32;j+=step){
                    Rect r = new Rect(new Point(i,j),new Point(i+step,j+step));
                    Mat temp = new Mat(resized,r);
                    int whitePix = Core.countNonZero(temp);
                    vec[idx++]=whitePix;
                    //if(i!=0 || j!=0) c+=",";
                    //c+=whitePix;
                }
            }
            //c+="]";
            //System.out.println(c);
            return vec;
        }
        catch (CvException cve){
            System.err.println("GetVector could not resize");
        }
        return new int[0];
    }*/

    public static int[] getVector(Mat letter){
        int[] vec = new int[49];
        Mat bw = new Mat();
        Imgproc.threshold(letter,bw,0,255,Imgproc.THRESH_OTSU);
        Mat resized = new Mat();
        try {
            Imgproc.resize(bw, resized, new Size(32, 32));
            int idx = 0;
            for(int i=0; i<=24; i+=4){
                for(int j=0; j<=24; j+=4){
                    Rect r = new Rect(new Point(i,j), new Point(i+8,j+8));
                    Mat temp = new Mat(resized,r);
                    vec[idx++]=Core.countNonZero(temp);
                }
            }
            return vec;
        }
        catch (CvException cve){
            //System.err.println("GetVector could not resize");
            return new int[0];
        }
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

        Mat traindata = new Mat(new Size(49,nbFile),CvType.CV_32F);
        Mat labels = new Mat(new Size(1,nbFile),CvType.CV_32F);
        int line = 0;
        for(int i = 0; i < folders.length; i++){
            File[] subFolders = folders[i].listFiles();
            boolean isPonct = folders[i].getName().equals("ponct") || folders[i].getName().equals("min_acc");
            for(int j=0;j<subFolders.length;j++){
                int charName = isPonct ?
                        (int)Special.valueOf(subFolders[j].getName()).getC() :
                        subFolders[j].getName().charAt(0);
                System.out.println("Training of "+(char)charName);
                File[] pics = subFolders[j].listFiles();
                for(int k = 0; k < pics.length; k++){
                    labels.put(line,0,charName);
                    try {
                        Mat pic = Image.loadImage(pics[k].getAbsolutePath(), false);
                        if(pic.empty()) System.out.println("empty");
                        int[] vec = getVector(LetterDetection.cropROI(pic));
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
        if(save) knn.save("knn.yml");
        return knn;
    }

    public static void main(String[] args) throws Exception {
        OpenCV.loadLocally();
        boolean train = false;
        KNearest knn = null;
        if(train){
            File num = Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\num").toFile();
            File maj = Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\maj").toFile();
            File ponct = Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\ponct").toFile();
            File min = Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\min").toFile();
            File min_acc = Paths.get("C:\\Users\\robin.jesson\\Desktop\\train\\min_acc").toFile();

           knn = trainKnn(true, num,maj,ponct, min, min_acc);
        }
        else{
            knn = KNearest.load("knn.yml");
        }




        File numtest = Paths.get("C:\\Users\\robin.jesson\\Desktop\\test4").toFile();
        double total = 0;
        double totalFound = 0;
        for(File fold : numtest.listFiles()){
            try {
                System.out.print(Special.valueOf(fold.getName()).getC() + " : ");
            }
            catch (IllegalArgumentException e){
                System.out.print(fold.getName().charAt(0) + " : ");
            }
            double sum = 0;
            for(File img : fold.listFiles()){
                Mat res = new Mat();
                Mat testdata = new Mat(new Size(49,1),CvType.CV_32F);
                Mat lettToTest = Image.loadImage(img.getAbsolutePath(), false);
                int[] vec = getVector(LetterDetection.cropROI(lettToTest));
                for(int i=0;i<vec.length;i++){
                    testdata.put(0,i,vec[i]);
                }
                float p = knn.findNearest(testdata,5,res);
                char c = (char)((int)p);
                System.out.print(c+ " ");
                String s ="zzz";
                try {
                    if (Character.toLowerCase(c) == Special.valueOf(fold.getName()).getC())
                        sum++;
                }
                catch (IllegalArgumentException e){
                    if (Character.toLowerCase(c) == Character.toLowerCase(fold.getName().charAt(0)))
                        sum++;
                }
                total++;
            }
            totalFound += sum;
            System.out.println("-> " + (int)(sum/fold.listFiles().length * 100) + "%");
        }
        System.out.println("TOTAL : "+(int)(totalFound/total*100)+"%");
    }
}
