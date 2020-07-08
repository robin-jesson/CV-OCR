package recognition;

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
        for(File block : file.listFiles()){
            System.out.println(block.getName());
            try {
                s += TesseractOCR.tess.doOCR(block);
                s+=" ";
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        }
        System.out.println(s);
        TextProcessing tp = new TextProcessing(s);
        System.out.println(tp.getCvInfo());


    }


}
