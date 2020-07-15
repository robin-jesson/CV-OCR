package recognition;

import main.Utils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;


public class TesseractOCR implements OCR{
    private String s = "";

    /**
     * Uses the tesseract engine to recognize text on pictures. It loops through image files.
     * @return Text containend in the images
     */
    @Override
    public String recognize(){
        this.s="";
        Arrays.stream(Paths.get("roi/blocs").toFile().listFiles()).parallel().forEach(f -> {
            try {
                System.out.println("Processing "+f.getName()+"...");
                Tesseract ocr = new Tesseract();
                ocr.setDatapath("src/main/resources");
                ocr.setLanguage("fra");
                ocr.setTessVariable("user_defined_dpi", "200");
                TesseractOCR.this.s += ocr.doOCR(f);
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        });

        return this.s;

    }
}
