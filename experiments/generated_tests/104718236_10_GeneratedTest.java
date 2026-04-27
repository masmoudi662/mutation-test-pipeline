java
package io.moquette.spi.impl.security;

import io.moquette.spi.security.IAuthorizator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationsCollectorTest {

    private AuthorizationsCollector authorizationsCollector;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authorizationsCollector = new AuthorizationsCollector();
    }

    @Test
    void parseAuthLine_topic() throws ParseException {
        String line = "topic read test/topic user1";
        Authorization authorization = authorizationsCollector.parseAuthLine(line);
        assertNotNull(authorization);
    }

    @Test
    void parseAuthLine_user() throws ParseException {
        String line = "user user1";
        Authorization authorization = authorizationsCollector.parseAuthLine(line);
        assertNull(authorization);
        assertEquals("user1", authorizationsCollector.m_currentUser);
        assertTrue(authorizationsCollector.m_parsingUsersSpecificSection);
        assertFalse(authorizationsCollector.m_parsingPatternSpecificSection);
    }

    @Test
    void parseAuthLine_pattern() throws ParseException {
        String line = "pattern read test/#";
        Authorization authorization = authorizationsCollector.parseAuthLine(line);
        assertNotNull(authorization);
        assertEquals("", authorizationsCollector.m_currentUser);
        assertFalse(authorizationsCollector.m_parsingUsersSpecificSection);
        assertTrue(authorizationsCollector.m_parsingPatternSpecificSection);
    }

    @Test
    void parseAuthLine_invalid() {
        String line = "invalid line";
        assertThrows(ParseException.class, () -> authorizationsCollector.parseAuthLine(line));
    }

    @Test
    void parseAuthLine_topic_empty_line() {
        String line = "topic";
        assertThrows(ParseException.class, () -> authorizationsCollector.parseAuthLine(line));
    }

    @Test
    void parseAuthLine_user_empty_line() throws ParseException {
        String line = "user testUser";
        authorizationsCollector.parseAuthLine(line);
        assertEquals("testUser", authorizationsCollector.m_currentUser);
    }

    @Test
    void parseAuthLine_pattern_empty_line() {
        String line = "pattern";
        assertThrows(ParseException.class, () -> authorizationsCollector.parseAuthLine(line));
    }

}