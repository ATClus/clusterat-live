package com.clusterat.api.config;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TesseractConfiguration {
    @Bean
    public Tesseract tesseract() {
        Tesseract instance = new Tesseract();
        instance.setDatapath("src/main/resources/tessdata");
        instance.setLanguage("por");

        return instance;
    }
}
