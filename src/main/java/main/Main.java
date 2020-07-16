package main;

import exception.DifferentSizeException;
import exception.NotFileException;
import exception.TooSmallWidthOrHeightException;
import processing.extraction.LetterDetection;
import processing.extraction.LettersSeparation;
import processing.extraction.TextDetection;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import processing.preprocessing.Denoising;
import processing.preprocessing.Deskewing;
import processing.preprocessing.PageDetection;
import processing.Image;
import recognition.OCR;
import recognition.KnnOCR;
import recognition.TesseractOCR;
import recognition.TextProcessing;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static main.Utils.*;


public class Main {

    static{
        OpenCV.loadLocally();
        initFolder();
    }

    public static void main(String[] args) throws NotFileException, IOException {
        String cv1="C:\\Users\\robin.jesson\\Desktop\\photoandroid\\cv1.jpg";
        //String cv2="C:\\Users\\robin.jesson\\Desktop\\photoandroid\\cv2.jpg";
        System.out.println(ocerize(cv1));
    }

    public static HashMap<String,String> ocerize(String... srcs) throws NotFileException, IOException {

        extraction(srcs);
        String txt = recognition(new TesseractOCR());
        Utils.write(txt);
        TextProcessing tp = new TextProcessing(txt);
        return tp.getCvInfo();

    }

    /**
     * Extracts the letters from a picture and put them in a folder roi/letters.<br/>
     * <ol>
     *     <li>It first takes the 4 corners of a detected page and remove the background.</li>
     *     <li>Then it detects the text dilating the text. Some zones text appears and opencv detects them.</li>
     *     <li>On each text zone, the pixels are projected on the height to detect lines.</li>
     *     <li>On each line, the words are detected also by dilating the letters.</li>
     *     <li>Then on each words the letters ROI appear by projecting the pixel on the horizontal line.</li>
     * </ol>
     * @param imgSrc  A picture file path.
     * @throws NotFileException The given path is not valid.
     * @throws IOException
     */
    private static void extraction(String... imgSrc) throws NotFileException, IOException {
        Mat[] bws = new Mat[imgSrc.length];
        Mat[] warpeds = new Mat[imgSrc.length];

        for(int s =0; s<imgSrc.length; s++){
            Mat img = Image.loadImage(imgSrc[s]);
            warpeds[s] = PageDetection.detectAndCropPage(img);
            bws[s] = Denoising.removeShadowAndBinarize(warpeds[s]);
        }

        LinkedList<Triplet<Mat,Rect,Mat>> textzones = TextDetection.getTextBlock(bws, warpeds);
        int progress = 0;
        int wordCount=0;
        int letterCount = 0;
        for(Triplet<Mat,Rect,Mat> triplet : textzones) {
            /*
            triplet.a = ROI (text zone)
            triplet.b = ROI position (opnecv rect object) in the global image (after transforms)
            triplet.c = global image (after transforms)
             */
            progressBar(progress,textzones.size()-1,"Letter extraction");
            Mat deskewed = Deskewing.deskewByHough(triplet.a);
            Mat whiteBackground = new Mat();
            Core.bitwise_not(deskewed,whiteBackground);
            Image.saveImage(new Mat(triplet.c, triplet.b),"roi/blocs/"+createNumberString(wordCount)+".png");
            LinkedList<Mat> lines = LetterDetection.detectLinesOfRoi(deskewed);

            for(Mat line : lines){
                if(!line.empty()){
                    List<Mat> words = LetterDetection.detectWordsOfLine(line);
                    for(Mat word : words){
                        //Image.saveImage(word,"roi/words/"+createNumberString(wordCount)+".png");
                        try {
                            List<Mat> letters = LetterDetection.detectLettersOfWord(word);

                            for(Mat letter : letters){

                                if(letter.height()<letter.width())
                                    Image.saveImage(letter,"roi/badletters/"+createFilename(wordCount,letterCount++)+"_00.png");
                                else
                                    Image.saveImage(letter,"roi/letters/"+createFilename(wordCount,letterCount++)+"_00.png");
                            }

                        }
                        catch (TooSmallWidthOrHeightException e) {}
                        catch (DifferentSizeException e) {
                            //System.err.println(e);
                            //Image.imshow(word,500);
                        }
                        wordCount++;
                        letterCount = 0;
                    }

                }
            }
            progress++;
        }

        letterSeparation();
    }

    /**
     * Sometimes some letters are connected (due the quality of the photo).
     * To fix it, the mean width og the "good" letters is calculated,
     * then the connected letters are separated using the mean width by cuttiong the connected letters.
     * @throws IOException
     * @throws NotFileException
     */
    private static void letterSeparation() throws IOException, NotFileException {
        LettersSeparation.separateFolders();
    }

    /**
     * Recognize the words present in the roi/letters folder.
     * @param recognizer KNN or Tesseract
     * @return
     * @throws IOException
     */
    private static String recognition(OCR recognizer) throws IOException {
        return recognizer.recognize();
    }




}
