java
package org.brixcms;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    @Test
    void subpath_validIndex_returnsSubpath() {
        Path path = new Path("/foo/bar/baz");
        Path subpath = path.subpath(2);
        assertEquals("/foo", subpath.toString());
    }

    @Test
    void subpath_validIndex_returnsSubpath2() {
        Path path = new Path("/foo/bar/baz");
        Path subpath = path.subpath(3);
        assertEquals("/foo/bar", subpath.toString());
    }

    @Test
    void subpath_validIndex_returnsSubpath3() {
        Path path = new Path("/foo/bar/baz");
        Path subpath = path.subpath(4);
        assertEquals("/foo/bar/baz", subpath.toString());
    }

    @Test
    void subpath_zeroIndex_throwsException() {
        Path path = new Path("/foo/bar/baz");
        assertThrows(IndexOutOfBoundsException.class, () -> path.subpath(0));
    }

    @Test
    void subpath_negativeIndex_throwsException() {
        Path path = new Path("/foo/bar/baz");
        assertThrows(IndexOutOfBoundsException.class, () -> path.subpath(-1));
    }

    @Test
    void subpath_indexGreaterThanSize_throwsException() {
        Path path = new Path("/foo/bar/baz");
        assertThrows(IndexOutOfBoundsException.class, () -> path.subpath(5));
    }

    @Test
    void subpath_rootPath() {
        Path path = new Path("/");
        Path subpath = path.subpath(1);
        assertEquals("/", subpath.toString());
    }

    @Test
    void subpath_nonAbsolutePath(){
        Path path = new Path("foo/bar/baz");
        Path subpath = path.subpath(1);
        assertEquals("foo", subpath.toString());
    }

    @Test
    void subpath_nonAbsolutePath2(){
        Path path = new Path("foo/bar/baz");
        Path subpath = path.subpath(2);
        assertEquals("foo/bar", subpath.toString());
    }
}