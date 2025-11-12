package com.clusterat.live.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
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
            throw new IllegalArgumentException("File not found: " + imagePath);
        }

        BufferedImage image = null;
        try {
            image = ImageIO.read(imageFile);

            if (image == null) {
                throw new IllegalArgumentException("Failed to read image: " + imagePath);
            }

            String text = tesseract.doOCR(image);

            image.flush();

            return text;

        } catch (IOException e) {
            log.error("Error reading image {}: {}", imagePath, e.getMessage());
            throw new TesseractException("Failed to read image", e);
        } finally {
            if (image != null) {
                image.flush();
                image = null;
            }

            System.gc();
        }
    }
}
