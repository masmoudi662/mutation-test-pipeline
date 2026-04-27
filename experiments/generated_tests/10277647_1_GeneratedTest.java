java
package ca.vanzyl.provisio.perms;

import org.junit.Test;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class ModeParserTest {

    @Test
    public void testParseOneAdd() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("u+r", toAdd, toRemove);
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_READ));
    }

    @Test
    public void testParseOneRemove() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("g-w", toAdd, toRemove);
        assertTrue(toRemove.contains(PosixFilePermission.GROUP_WRITE));
    }

    @Test
    public void testParseOneMultiple() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("ugo+rwx", toAdd, toRemove);
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_READ));
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_EXECUTE));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_READ));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_EXECUTE));
        assertTrue(toAdd.contains(PosixFilePermission.OTHERS_READ));
        assertTrue(toAdd.contains(PosixFilePermission.OTHERS_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.OTHERS_EXECUTE));
    }

    @Test(expected = RuntimeException.class)
    public void testParseOneInvalidExpression() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("u", toAdd, toRemove);
    }

    @Test(expected = RuntimeException.class)
    public void testParseOneInvalidExpressionEmptyWhat() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("u+", toAdd, toRemove);
    }

    @Test
    public void testParseOneAll() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("+rwx", toAdd, toRemove);
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_READ));
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_EXECUTE));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_READ));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_EXECUTE));
        assertTrue(toAdd.contains(PosixFilePermission.OTHERS_READ));
        assertTrue(toAdd.contains(PosixFilePermission.OTHERS_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.OTHERS_EXECUTE));
    }

    @Test
    public void testParseOneUserAndGroup() {
        Set<PosixFilePermission> toAdd = new HashSet<>();
        Set<PosixFilePermission> toRemove = new HashSet<>();
        ModeParser.parseOne("ug+rw", toAdd, toRemove);
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_READ));
        assertTrue(toAdd.contains(PosixFilePermission.OWNER_WRITE));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_READ));
        assertTrue(toAdd.contains(PosixFilePermission.GROUP_WRITE));
    }
}