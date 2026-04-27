java
package tools.descartes.teastore.image;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.entities.ImageSize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.Assert.*;

public class StoreImageTest {

    private static final Logger log = LoggerFactory.getLogger(StoreImageTest.class);

    @Test
    public void testGetImage() throws IOException {
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", byteArrayOutputStream);
        String imageData = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

        StoreImage storeImage = new StoreImage(imageData, ImageSize.LARGE);
        BufferedImage retrievedImage = storeImage.getImage();

        assertNotNull(retrievedImage);
        assertEquals(100, retrievedImage.getWidth());
        assertEquals(100, retrievedImage.getHeight());
    }

    @Test
    public void testGetImageInvalidData() {
        StoreImage storeImage = new StoreImage("invalidImageData", ImageSize.LARGE);
        BufferedImage retrievedImage = storeImage.getImage();
        assertNull(retrievedImage);
    }

    @Test
    public void testConstructorAndGetter() {
        String testData = "testData";
        ImageSize testSize = ImageSize.LARGE;
        StoreImage storeImage = new StoreImage(testData, testSize);
        assertEquals(testData, storeImage.getData());
        assertEquals(testSize, storeImage.getSize());
    }

    @Test
    public void testIsCached() {
        StoreImage storeImage = new StoreImage("testData", ImageSize.LARGE);
        assertFalse(storeImage.isCached());
        storeImage.setCached(true);
        assertTrue(storeImage.isCached());
    }

    @Test
    public void testSetCached() {
        StoreImage storeImage = new StoreImage("testData", ImageSize.LARGE);
        storeImage.setCached(true);
        assertTrue(storeImage.isCached());
        storeImage.setCached(false);
        assertFalse(storeImage.isCached());
    }

    @Test
    public void testToString() {
        StoreImage storeImage = new StoreImage("testData", ImageSize.LARGE);
        assertNotNull(storeImage.toString());
    }

    @Test
    public void testHashCode() {
        StoreImage storeImage1 = new StoreImage("testData", ImageSize.LARGE);
        StoreImage storeImage2 = new StoreImage("testData", ImageSize.LARGE);
        assertEquals(storeImage1.hashCode(), storeImage2.hashCode());
    }

    @Test
    public void testEquals() {
        StoreImage storeImage1 = new StoreImage("testData", ImageSize.LARGE);
        StoreImage storeImage2 = new StoreImage("testData", ImageSize.LARGE);
        StoreImage storeImage3 = new StoreImage("differentData", ImageSize.LARGE);
        assertEquals(storeImage1, storeImage2);
        assertNotEquals(storeImage1, storeImage3);
        assertNotEquals(storeImage1, null);
        assertNotEquals(storeImage1, new Object());
    }

    @Test
    public void testEqualsSameObject() {
        StoreImage storeImage = new StoreImage("testData", ImageSize.LARGE);
        assertEquals(storeImage, storeImage);
    }
}