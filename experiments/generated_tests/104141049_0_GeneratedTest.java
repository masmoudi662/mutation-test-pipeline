java
package com.networknt.session.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class JdbcSessionRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(JdbcSessionRepositoryTest.class);

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private JdbcSessionRepository jdbcSessionRepository;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        jdbcSessionRepository = new JdbcSessionRepository(dataSource, 3600, "test_sessions", "test_session_attributes");
    }

    @Test
    public void testSaveNewSession() throws SQLException {
        JdbcSession session = new JdbcSession();
        session.setId("123");
        session.setCreationTime(System.currentTimeMillis());
        session.setLastAccessedTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(1800);
        session.setExpiryTime(System.currentTimeMillis() + 1800000);
        session.setPrincipalName("testUser");
        session.setAttribute("testAttribute", "testValue");

        when(preparedStatement.executeUpdate()).thenReturn(1);

        jdbcSessionRepository.save(session);

        verify(connection).setAutoCommit(false);
        verify(preparedStatement).setString(1, "123");
        verify(preparedStatement).setLong(2, session.getCreationTime());
        verify(preparedStatement).setLong(3, session.getLastAccessedTime());
        verify(preparedStatement).setInt(4, 1800);
        verify(preparedStatement).setLong(5, session.getExpiryTime());
        verify(preparedStatement).setString(6, "testUser");
        verify(preparedStatement).executeUpdate();
        verify(connection).commit();
    }

    @Test
    public void testSaveExistingSession() throws SQLException {
        JdbcSession session = new JdbcSession();
        session.setId("123");
        session.setNew(false);
        session.setLastAccessedTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(3600);
        session.setExpiryTime(System.currentTimeMillis() + 3600000);
        session.setPrincipalName("updatedUser");
        session.setAttribute("testAttribute", "updatedValue");
        session.clearChangeFlags();
        session.setAttribute("testAttribute", "newValue");

        Map<String, Object> delta = new HashMap<>();
        delta.put("testAttribute", "newValue");
        session.setDelta(delta);

        when(preparedStatement.executeUpdate()).thenReturn(1);

        jdbcSessionRepository.save(session);

        verify(connection).setAutoCommit(false);
        verify(preparedStatement).setString(1, "123");
        verify(preparedStatement).setLong(2, session.getLastAccessedTime());
        verify(preparedStatement).setInt(3, 3600);
        verify(preparedStatement).setLong(4, session.getExpiryTime());
        verify(preparedStatement).setString(5, "updatedUser");
        verify(preparedStatement).setString(6, "123");
        verify(preparedStatement, times(2)).executeUpdate();
        verify(connection).commit();
    }

    @Test
    public void testDeleteSessionAttribute() throws SQLException {
        JdbcSession session = new JdbcSession();
        session.setId("123");
        session.setNew(false);
        session.setLastAccessedTime(System.currentTimeMillis());
        session.setMaxInactiveInterval(3600);
        session.setExpiryTime(System.currentTimeMillis() + 3600000);
        session.setPrincipalName("updatedUser");
        session.setAttribute("testAttribute", "updatedValue");
        session.clearChangeFlags();
        session.removeAttribute("testAttribute");

        Map<String, Object> delta = new HashMap<>();
        delta.put("testAttribute", null);
        session.setDelta(delta);

        when(preparedStatement.executeUpdate()).thenReturn(1);

        jdbcSessionRepository.save(session);

        verify(connection).setAutoCommit(false);
        verify(preparedStatement).setString(1, "123");
        verify(preparedStatement).setLong(2, session.getLastAccessedTime());
        verify(preparedStatement).setInt(3, 3600);
        verify(preparedStatement).setLong(4, session.getExpiryTime());
        verify(preparedStatement).setString(5, "updatedUser");
        verify(preparedStatement).setString(6, "123");
        verify(preparedStatement, times(2)).executeUpdate();
        verify(connection).commit();
    }

}