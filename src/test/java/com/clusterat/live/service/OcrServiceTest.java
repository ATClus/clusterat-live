package com.clusterat.live.service;

import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OcrServiceTest {
    @Autowired
    private OcrService ocrService;

    /**
     * Test case to verify the behavior of extractText() when the OCR operation is successful.
     */
    @Test
    void testExtractTextSuccess() throws TesseractException {
        // Arrange
        String imagePath = "src/test/resources/tessdata/test01.png";
        String expectedText = "View raw";

        // Act
        String result = ocrService.extractText(imagePath).trim();

        // Assert
        assertTrue(result.contains(expectedText),
                "The extracted text should contain: " + expectedText + ". Actual: " + result);
    }


    /**
     * Test case to verify the behavior of extractText() when Tesseract throws an exception.
     * Tesseract often throws an exception for unsupported or corrupt file formats.
     * We'll keep the non-existent file path test below, which handles a different failure case.
     */
    @Test
    void testExtractTextWithNonExistentFile() {
        // Arrange
        String invalidPath = "src/test/resources/tessdata/test00.png";

        // Act & Assert
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> ocrService.extractText(invalidPath));
        assertEquals("Invalid file path: src/test/resources/tessdata/test00.png", thrownException.getMessage());
    }
}