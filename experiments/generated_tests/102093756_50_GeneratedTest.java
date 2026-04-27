java
package nl.tudelft.serg.evosql.fixture.type;

import nl.tudelft.serg.evosql.EvoSQLConfiguration;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

class DBTypeSelectorTest {

    @Test
    void createDouble() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.DOUBLE, 10);
        assertTrue(type instanceof DBDouble);
    }

    @Test
    void createReal() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.REAL, 10);
        assertTrue(type instanceof DBDouble);
        assertEquals("REAL", type.getSqlType());
    }

    @Test
    void createInteger() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.INTEGER, 10);
        assertTrue(type instanceof DBInteger);
    }

    @Test
    void createVarchar() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.VARCHAR, 50);
        assertTrue(type instanceof DBString);
        assertEquals(50, ((DBString) type).getLength());
    }

    @Test
    void createBoolean() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.BOOLEAN, 10);
        assertTrue(type instanceof DBBoolean);
    }

    @Test
    void createDate() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.DATE, 10);
        assertTrue(type instanceof DBDate);
    }

    @Test
    void createTime() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.TIME, 10);
        assertTrue(type instanceof DBTime);
    }

    @Test
    void createTimestamp() {
        DBTypeSelector selector = new DBTypeSelector();
        DBType type = selector.create(Types.TIMESTAMP, 10);
        assertTrue(type instanceof DBDateTime);
        assertEquals("TIMESTAMP", type.getSqlType());
    }

    @Test
    void createArrayThrowsException() {
        DBTypeSelector selector = new DBTypeSelector();
        assertThrows(UnsupportedOperationException.class, () -> selector.create(Types.ARRAY, 10));
    }

    @Test
    void createUnknownTypeThrowsException() {
        DBTypeSelector selector = new DBTypeSelector();
        assertThrows(UnsupportedOperationException.class, () -> selector.create(-999, 10));
    }
}