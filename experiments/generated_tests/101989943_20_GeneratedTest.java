java
package de.rcblum.stream.deck.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.Color;

class IconHelperTest {

    @Test
    void testRotate180() {
        BufferedImage inputImage = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) inputImage.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = i + 1;
        }
        BufferedImage rotatedImage = IconHelper.rotate180(inputImage);
        assertNotNull(rotatedImage);
    }

    @Test
    void testRotate180_nullImage() {
        BufferedImage rotatedImage = IconHelper.rotate180(null);
        assertNull(rotatedImage);
    }

    @Test
    void testRotate180_singlePixelImage() {
        BufferedImage inputImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        inputImage.setRGB(0, 0, Color.RED.getRGB());
        BufferedImage rotatedImage = IconHelper.rotate180(inputImage);
        assertNotNull(rotatedImage);
    }

    @Test
    void testRotate180_differentImageType() {
        BufferedImage inputImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        inputImage.setRGB(0, 0, Color.RED.getRGB());
        inputImage.setRGB(0, 1, Color.GREEN.getRGB());
        inputImage.setRGB(1, 0, Color.BLUE.getRGB());
        inputImage.setRGB(1, 1, Color.WHITE.getRGB());
        BufferedImage rotatedImage = IconHelper.rotate180(inputImage);
        assertNotNull(rotatedImage);
    }
}