package org.apache.zookeeper.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileKeyStoreLoaderBuilderProviderTest {

    @Test
    void getBuilderForKeyStoreFileType_JKS() {
        assertTrue(FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.JKS) instanceof JKSFileLoader.Builder);
    }

    @Test
    void getBuilderForKeyStoreFileType_PEM() {
        assertTrue(FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.PEM) instanceof PEMFileLoader.Builder);
    }

    @Test
    void getBuilderForKeyStoreFileType_PKCS12() {
        assertTrue(FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.PKCS12) instanceof PKCS12FileLoader.Builder);
    }

    @Test
    void getBuilderForKeyStoreFileType_BCFKS() {
        assertTrue(FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(KeyStoreFileType.BCFKS) instanceof BCFKSFileLoader.Builder);
    }

    @Test
    void getBuilderForKeyStoreFileType_null() {
        assertThrows(NullPointerException.class, () -> FileKeyStoreLoaderBuilderProvider.getBuilderForKeyStoreFileType(null));
    }
}