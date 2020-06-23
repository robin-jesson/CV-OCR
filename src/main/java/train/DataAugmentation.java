package train;

import exception.NotFileException;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import processing.Image;
import processing.extraction.LetterDetection;

import java.io.File;
import java.nio.file.Paths;

class DataAugmentation {

    public static void main(String[] args) throws NotFileException {
        OpenCV.loadLocally();

        /*File num = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\num").toFile();
        File maj = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\maj").toFile();
        File min = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\min").toFile();
        File ponct = Paths.get("C:\\Users\\robin.jesson\\Documents\\letters\\ponct").toFile();*/

        augmentFolders(TrainFiles.num.getFile(),
                TrainFiles.maj.getFile(),
                TrainFiles.min.getFile(),
                TrainFiles.ponct.getFile());

    }

    private static void augmentFolders(File... folders) throws NotFileException {
        for(int i = 0; i<folders.length; i++){
            System.out.println("Augmentation de "+folders[i].getAbsolutePath());
            augmentFolder(folders[i]);
        }


    }

    private static void augmentFolder(File folder) throws NotFileException {
        int i = 0;
        for(File classFolder : folder.listFiles()){
            for(File img : classFolder.listFiles()){
                Mat ch = Image.loadImage(img.getAbsolutePath(),false);
                Image.saveImage(dilate(ch),classFolder.getAbsolutePath()+"\\"+ i++ +"dil.png");
                Image.saveImage(erode(ch),classFolder.getAbsolutePath()+"\\"+ i+"ero.png");
            }
            for(File img : classFolder.listFiles()){
                Mat ch = Image.loadImage(img.getAbsolutePath(),false);
                for(int a = -30; a <= 30; a += 10){
                    // System.out.println(classFolder.getAbsolutePath()+"\\augm"+a+".png");
                    Image.saveImage(rotation(ch,a),classFolder.getAbsolutePath()+"\\"+ i++ +"augm"+a+".png");
                }
            }
        }
    }

    private static Mat rotation(Mat img, int angle){
        Mat rotated = img.clone();
        Mat M = Imgproc.getRotationMatrix2D(new Point(img.width(),img.height()),angle,1);
        RotatedRect rr = new RotatedRect(new Point(),img.size(),angle);
        Imgproc.warpAffine(img,rotated,M,rr.boundingRect().size(),Imgproc.INTER_CUBIC);

        return LetterDetection.cropROI(rotated);
    }

    private static Mat erode(Mat img){
        return LetterDetection.erodeLetters(img);
    }

    private static Mat dilate(Mat img){
        return LetterDetection.dilateLetters(img,1);
    }


}
