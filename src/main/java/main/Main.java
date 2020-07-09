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
import recognition.IRecognition;
import recognition.KnnOCR;
import recognition.TesseractOCR;
import recognition.TextProcessing;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static main.Utils.*;


public class Main {

    static{
        OpenCV.loadLocally();
        initFolder();
    }

    public static void main(String[] args) throws NotFileException, IOException {
        long startTime = System.currentTimeMillis();

        String imgSrc="C:\\Users\\robin.jesson\\Desktop\\img\\ipad.jpg";
        extraction(imgSrc);
        String txt = recognition(new TesseractOCR());
        Utils.write(txt);
        TextProcessing tp = new TextProcessing(txt);
        System.out.println(tp.getCvInfo());

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Temps exec = "+elapsedTime/1000);

    }

    private static void extraction(String imgSrc) throws NotFileException, IOException {
        System.out.println("Letter extraction");
        Mat img = Image.loadImage(imgSrc);
        Mat warped = PageDetection.detectAndCropPage(img);
        Mat bw = Denoising.removeShadowAndBinarize(warped);
        LinkedList<Mat> textzones = TextDetection.getTextBlock(bw);
        int progress = 0;
        int wordCount=0;
        int letterCount = 0;
        for(Mat roi : textzones) {
            progressBar(progress,textzones.size()-1);
            Mat deskewed = Deskewing.deskewByHough(roi);
            Mat whiteBackground = new Mat();
            Core.bitwise_not(deskewed,whiteBackground);
            Image.saveImage(whiteBackground,"roi/blocs/"+createNumberString(wordCount)+".png");
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

        System.out.println("Letters separation when needed");
        letterSeparation();
    }

    private static void letterSeparation() throws IOException, NotFileException {
        LettersSeparation.separateFolders();
    }

    private static String recognition(IRecognition recognizer) throws IOException {
        return recognizer.recognize();
    }




}
