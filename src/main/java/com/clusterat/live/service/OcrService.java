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

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            System.setProperty("jna.library.path", "/opt/homebrew/Cellar/tesseract/5.5.1/lib:/opt/homebrew/Cellar/leptonica/1.86.0/lib");
            log.info("Configured JNA library path for macOS (Homebrew)");
        } else if (os.contains("linux")) {
            String jnaLibPath = "/usr/lib/x86_64-linux-gnu:/usr/lib:/lib/x86_64-linux-gnu:/lib";

            String linuxbrewPath = "/home/linuxbrew/.linuxbrew/Cellar/tesseract/5.5.1_1/lib/:/home/linuxbrew/.linuxbrew/Cellar/leptonica/1.86.0/lib/";
            if (new java.io.File("/home/linuxbrew/.linuxbrew").exists()) {
                jnaLibPath = linuxbrewPath + ":" + jnaLibPath;
                log.info("Configured JNA library path for Linux (Linuxbrew + system)");
            } else {
                log.info("Configured JNA library path for Linux (system apt installation)");
            }

            System.setProperty("jna.library.path", jnaLibPath);
        }

        log.info("JNA library path: {}", System.getProperty("jna.library.path"));
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
