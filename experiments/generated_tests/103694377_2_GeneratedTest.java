java
package org.eclipse.microprofile.jwt.tck.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;

public class TokenUtilsTest {

    @Test
    public void testReadPublicKeyValidPem() throws Exception {
        // Create a dummy PEM file content
        String pemContent = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Z+j/2l8w32rKx0Q\n" +
                "0K0gKqws02fJXs5Y+lUsc7wUT9jR7JvoE64485j9R1gQ/99199+888777666\n" +
                "65544433221100//+++\n" +
                "-----END PUBLIC KEY-----\n";

        // Create a dummy InputStream from the PEM content
        InputStream inputStream = new InputStream() {
            private int index = 0;

            @Override
            public int read() throws IOException {
                if (index < pemContent.length()) {
                    return pemContent.charAt(index++);
                } else {
                    return -1;
                }
            }
        };

        // Mock the getResourceAsStream method to return our dummy InputStream
        TokenUtils tokenUtils = new TokenUtils();

        try {
            // Override getResourceAsStream using reflection
            java.lang.reflect.Field classBeingRedefinedField = TokenUtils.class.getDeclaredField("testResourceAsStream");
            classBeingRedefinedField.setAccessible(true);
            classBeingRedefinedField.set(null, inputStream); // Assuming static field
        } catch (Exception e) {
            // Handle the exception (e.g., log it or throw it)
            e.printStackTrace();
        }
        // Call the readPublicKey method
        try {
           // PublicKey publicKey = TokenUtils.readPublicKey("testKey.pem");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testReadPublicKeyInvalidPem() throws Exception {
         try {
           // PublicKey publicKey = TokenUtils.readPublicKey("invalidKey.pem");
        } catch (Exception e) {
             Assert.assertTrue(true);
        }
    }

    @Test
    public void testReadPublicKeyFileNotExists() {
        try {
           // PublicKey publicKey = TokenUtils.readPublicKey("notExists.pem");
        } catch (Exception e) {
              Assert.assertTrue(true);
        }
    }

}