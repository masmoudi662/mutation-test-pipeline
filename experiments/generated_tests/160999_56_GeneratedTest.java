java
package org.apache.zookeeper.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileKeyStoreLoaderBuilderProviderTest {

    @Test
    public void testGetBuilderForKeyStoreFileType_JKS() {
        FileKeyStoreLoader.Builder<? extends FileKeyStoreLoader> builder =
                FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.JKS);
        assertNotNull(builder);
        assertTrue(builder instanceof JKSFileLoader.Builder);
    }

    @Test
    public void testGetBuilderForKeyStoreFileType_PEM() {
        FileKeyStoreLoader.Builder<? extends FileKeyStoreLoader> builder =
                FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.PEM);
        assertNotNull(builder);
        assertTrue(builder instanceof PEMFileLoader.Builder);
    }

    @Test
    public void testGetBuilderForKeyStoreFileType_PKCS12() {
        FileKeyStoreLoader.Builder<? extends FileKeyStoreLoader> builder =
                FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.PKCS12);
        assertNotNull(builder);
        assertTrue(builder instanceof PKCS12FileLoader.Builder);
    }

    @Test
    public void testGetBuilderForKeyStoreFileType_BCFKS() {
        FileKeyStoreLoader.Builder<? extends FileKeyStoreLoader> builder =
                FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.BCFKS);
        assertNotNull(builder);
        assertTrue(builder instanceof BCFKSFileLoader.Builder);
    }

    @Test
    public void testGetBuilderForKeyStoreFileType_NullType() {
        assertThrows(NullPointerException.class, () ->
                FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(null));
    }
}