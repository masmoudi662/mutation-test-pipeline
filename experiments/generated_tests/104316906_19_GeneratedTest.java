java
package com.github.fartherp.framework.poi.excel.write;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

class FileExcelWriteTest {

    @Test
    void build_validInput_returnsExcelWrite() {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        String fileName = "test.xlsx";
        ExcelWrite excelWrite = FileExcelWrite.build(inputStream, fileName);
        assertNotNull(excelWrite);
    }

    @Test
    void build_nullInputStream_throwsException() {
        String fileName = "test.xlsx";
        assertThrows(NullPointerException.class, () -> FileExcelWrite.build(null, fileName));
    }

    @Test
    void build_nullFileName_throwsException() {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        assertThrows(NullPointerException.class, () -> FileExcelWrite.build(inputStream, null));
    }

    @Test
    void build_nullInputStreamAndFileName_throwsException() {
        assertThrows(NullPointerException.class, () -> FileExcelWrite.build(null, null));
    }

    @Test
    void write_validOutputStream_doesNotThrowException() throws IOException {
        OutputStream outputStream = Mockito.mock(OutputStream.class);
		FileExcelWrite fileExcelWrite = new FileExcelWrite();
    }
}