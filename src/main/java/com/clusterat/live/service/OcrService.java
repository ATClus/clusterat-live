package com.clusterat.live.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {
    private final Tesseract tesseract;

    public OcrService(Tesseract tesseract) {
        this.tesseract = tesseract;
        System.setProperty("jna.library.path", "/opt/homebrew/Cellar/tesseract/5.5.1/lib:/opt/homebrew/Cellar/leptonica/1.86.0/lib");
    }

    public String extractText(String imagePath) throws TesseractException {
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            throw new IllegalArgumentException("Invalid file path: " + imagePath);
        }

        return tesseract.doOCR(imageFile);
    }
}
