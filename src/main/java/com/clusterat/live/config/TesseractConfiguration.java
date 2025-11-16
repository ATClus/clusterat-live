package com.clusterat.live.config;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Slf4j
@Configuration
class TesseractConfiguration {

    @Value("${tesseract.datapath:}")
    private String tessdataPath;

    @Value("${tesseract.language:por+eng}")
    private String language;

    @Bean
    public Tesseract tesseract() {
        Tesseract instance = new Tesseract();

        String datapath = determineTessdataPath();

        if (datapath != null && !datapath.isEmpty()) {
            log.info("Setting Tesseract datapath to: {}", datapath);
            instance.setDatapath(datapath);
        } else {
            log.info("Using system default Tesseract datapath (TESSDATA_PREFIX environment variable or /usr/share/tesseract-ocr/*/tessdata)");
        }

        log.info("Setting Tesseract language to: {}", language);
        instance.setLanguage(language);

        return instance;
    }

    private String determineTessdataPath() {
        if (tessdataPath != null && !tessdataPath.isEmpty()) {
            File tessdataDir = new File(tessdataPath);
            if (tessdataDir.exists() && tessdataDir.isDirectory()) {
                log.info("Using configured tessdata path: {}", tessdataPath);
                return tessdataPath;
            } else {
                log.warn("Configured tessdata path does not exist: {}", tessdataPath);
            }
        }

        String envTessdataPrefix = System.getenv("TESSDATA_PREFIX");
        if (envTessdataPrefix != null && !envTessdataPrefix.isEmpty()) {
            File tessdataDir = new File(envTessdataPrefix);
            if (tessdataDir.exists() && tessdataDir.isDirectory()) {
                log.info("Using TESSDATA_PREFIX from environment: {}", envTessdataPrefix);
                return envTessdataPrefix;
            }
        }

        String[] commonPaths = {
            "/usr/share/tesseract-ocr/4.00/tessdata",
            "/usr/share/tesseract-ocr/5/tessdata",
            "/usr/share/tessdata",
            "/usr/local/share/tessdata",
            "/opt/homebrew/share/tessdata"
        };

        for (String path : commonPaths) {
            File tessdataDir = new File(path);
            if (tessdataDir.exists() && tessdataDir.isDirectory()) {
                log.info("Found tessdata at common path: {}", path);
                return path;
            }
        }

        String localPath = "src/main/resources/tessdata";
        File localTessdata = new File(localPath);
        if (localTessdata.exists() && localTessdata.isDirectory()) {
            log.info("Using local development tessdata path: {}", localPath);
            return localPath;
        }

        log.info("No explicit tessdata path found, will use system defaults");
        return null;
    }
}

