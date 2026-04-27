java
package org.javalite.activejdbc;

import org.javalite.activejdbc.test.ActiveJDBCTest;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class BaseTest {

    private DB db;

    @Before
    public void setUp() {
        db = mock(DB.class);
        ActiveJDBCTest.mockDB = db;
    }

    @Test
    public void testFind() {
        String query = "SELECT * FROM users WHERE id = ?";
        Object[] params = {123};
        RowProcessor processor = new RowProcessor();
        when(db.find(query, params)).thenReturn(processor);
        RowProcessor result = Base.find(query, params);
        assertEquals(processor, result);
        verify(db).find(query, params);
    }

    @Test
    public void testFirst() {
        String query = "SELECT * FROM users WHERE id = ?";
        Object[] params = {123};
        Map<String, Object> expected = new HashMap<>();
        when(db.first(query, params)).thenReturn(expected);
        Map<String, Object> actual = Base.first(query, params);
        assertEquals(expected, actual);
        verify(db).first(query, params);
    }

    @Test
    public void testFindAll() {
        String query = "SELECT * FROM users";
        RowProcessor processor = new RowProcessor();
        when(db.findAll(query)).thenReturn(processor);
        RowProcessor result = Base.findAll(query);
        assertEquals(processor, result);
        verify(db).findAll(query);
    }

    @Test
    public void testFindBySQL() {
        String sql = "SELECT * FROM users WHERE last_name = 'Simpson'";
        RowProcessor processor = new RowProcessor();
        when(db.findBySQL(sql)).thenReturn(processor);
        RowProcessor result = Base.findBySQL(sql);
        assertEquals(processor, result);
        verify(db).findBySQL(sql);
    }

    @Test
    public void testFindBySQLWithParams() {
        String sql = "SELECT * FROM users WHERE last_name = ?";
        Object[] params = {"Simpson"};
        RowProcessor processor = new RowProcessor();
        when(db.findBySQL(sql, params)).thenReturn(processor);
        RowProcessor result = Base.findBySQL(sql, params);
        assertEquals(processor, result);
        verify(db).findBySQL(sql, params);
    }
}