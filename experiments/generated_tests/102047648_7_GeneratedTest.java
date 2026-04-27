java
package fr.bmartel.passwords;

import fr.bmartel.passwords.utils.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class PasswordEntryTest {

    private PasswordEntry passwordEntry;

    @BeforeEach
    void setUp() {
        passwordEntry = new PasswordEntry((short) 10);
    }

    @Test
    void testSearch_found() {
        byte[] buf = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        short ofs = 0;
        byte len = 5;

        try (MockedStatic<PasswordEntry> utilities = mockStatic(PasswordEntry.class)) {
            utilities.when(() -> PasswordEntry.search(buf, ofs, len)).thenReturn(passwordEntry);
            PasswordEntry result = PasswordEntry.search(buf, ofs, len);
            assertEquals(passwordEntry, result);
        }
    }

    @Test
    void testSearch_not_found() {
        byte[] buf = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        short ofs = 0;
        byte len = 5;

        try (MockedStatic<PasswordEntry> utilities = mockStatic(PasswordEntry.class)) {
            utilities.when(() -> PasswordEntry.search(buf, ofs, len)).thenReturn(null);
            PasswordEntry result = PasswordEntry.search(buf, ofs, len);
            assertNull(result);
        }
    }

    @Test
    void testDelete_password_entry_exists() {
        byte[] buf = new byte[]{1, 2, 3, 4, 5};
        short ofs = 0;
        byte len = 5;

        try (MockedStatic<PasswordEntry> utilities = mockStatic(PasswordEntry.class);
             MockedStatic<JCSystem> jcSystemMockedStatic = mockStatic(JCSystem.class)) {

            utilities.when(() -> PasswordEntry.search(buf, ofs, len)).thenReturn(passwordEntry);
            PasswordEntry.delete(buf, ofs, len);

            jcSystemMockedStatic.verify(() -> JCSystem.beginTransaction());
            jcSystemMockedStatic.verify(() -> JCSystem.commitTransaction());
        }
    }

    @Test
    void testDelete_password_entry_does_not_exist() {
        byte[] buf = new byte[]{1, 2, 3, 4, 5};
        short ofs = 0;
        byte len = 5;

        try (MockedStatic<PasswordEntry> utilities = mockStatic(PasswordEntry.class);
             MockedStatic<JCSystem> jcSystemMockedStatic = mockStatic(JCSystem.class)) {

            utilities.when(() -> PasswordEntry.search(buf, ofs, len)).thenReturn(null);
            PasswordEntry.delete(buf, ofs, len);

            jcSystemMockedStatic.verify(() -> JCSystem.beginTransaction(), Mockito.never());
            jcSystemMockedStatic.verify(() -> JCSystem.commitTransaction(), Mockito.never());
        }
    }
}