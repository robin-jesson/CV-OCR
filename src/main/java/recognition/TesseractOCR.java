package recognition;

import main.Utils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.nio.file.Paths;



public class TesseractOCR implements IRecognition{
    private static Tesseract tess;

    static{
        TesseractOCR.tess = new Tesseract();
        TesseractOCR.tess.setDatapath("src/main/resources");
        TesseractOCR.tess.setLanguage("fra");
        TesseractOCR.tess.setTessVariable("user_defined_dpi", "70");
    }

    @Override
    public String recognize(){
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
        return s;
    }


}
