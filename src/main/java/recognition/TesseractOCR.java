package recognition;

import main.Utils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.nio.file.Paths;



public class TesseractOCR {
    private static Tesseract tess;

    static{
        TesseractOCR.tess = new Tesseract();
        TesseractOCR.tess.setDatapath("C:\\Users\\robin.jesson\\Downloads\\tesseract-2.00.fra.tar\\tesseract-2.00.fra\\tessdata");
        TesseractOCR.tess.setLanguage("fra");
        TesseractOCR.tess.setTessVariable("user_defined_dpi", "70");
    }


    public static void main(String[] args) {
        File file = Paths.get("roi/blocs").toFile();
        String s = "";
        File[] blocks = file.listFiles();
        for(int b = 0; b < blocks.length; b++){
            try {
                s += TesseractOCR.tess.doOCR(blocks[b]) + " ";
            } catch (TesseractException e) {
                e.printStackTrace();
            }
            Utils.progressBar(b,blocks.length);
        }
        TextProcessing tp = new TextProcessing(s);
        System.out.println(tp.getCvInfo());


    }


}
