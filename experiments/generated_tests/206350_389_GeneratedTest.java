java
package org.apache.cayenne.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void testStripFileExtension_Normal() {
        assertEquals("test", Util.stripFileExtension("test.txt"));
    }

    @Test
    public void testStripFileExtension_NoExtension() {
        assertEquals("test", Util.stripFileExtension("test"));
    }

    @Test
    public void testStripFileExtension_HiddenFile() {
        assertEquals(".test", Util.stripFileExtension(".test"));
    }

    @Test
    public void testStripFileExtension_MultipleDots() {
        assertEquals("test.name", Util.stripFileExtension("test.name.txt"));
    }

    @Test
    public void testStripFileExtension_EmptyFileName() {
        assertEquals("", Util.stripFileExtension(""));
    }

    @Test
    public void testStripFileExtension_DotOnly() {
        assertEquals("", Util.stripFileExtension("."));
    }

    @Test
    public void testStripFileExtension_PathWithFileName() {
        assertEquals("path/to/test", Util.stripFileExtension("path/to/test.txt"));
    }

    @Test
    public void testStripFileExtension_PathWithDotFile() {
        assertEquals("path/to/.test", Util.stripFileExtension("path/to/.test"));
    }

    @Test
    public void testStripFileExtension_FileNameWithHiddenPart() {
        assertEquals(".hidden.file", Util.stripFileExtension(".hidden.file.txt"));
    }

    @Test
    public void testStripFileExtension_ChineseChars() {
        assertEquals("测试文件", Util.stripFileExtension("测试文件.txt"));
    }
}