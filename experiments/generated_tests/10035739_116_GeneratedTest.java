java
package liquibase.serializer.core.xml;

import liquibase.change.ChangeProperty;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.parser.core.xml.XMLChangeLogSAXParser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XMLChangeLogSerializerTest {

    private XMLChangeLogSerializer serializer;
    private Document currentChangeLogFileDOM;

    @Before
    public void setUp() throws Exception {
        serializer = new XMLChangeLogSerializer();
        currentChangeLogFileDOM = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        serializer.currentChangeLogFileDOM = currentChangeLogFileDOM;
    }

    @Test
    public void createNode_basic() {
        TestSqlVisitor visitor = new TestSqlVisitor();
        visitor.setName("testName");
        visitor.setValue("testValue");
        Element node = serializer.createNode(visitor);

        assertNotNull(node);
        assertEquals("testValue", node.getAttribute("value"));
    }

    @Test(expected = UnexpectedLiquibaseException.class)
    public void createNode_exception() {
        TestSqlVisitor visitor = new TestSqlVisitor();
        visitor.setThrowException(true);

        serializer.createNode(visitor);
    }

    private static class TestSqlVisitor {
        private String name;
        private String value;
        private boolean throwException = false;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ChangeProperty(includeInSerialization = true)
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isThrowException() {
            return throwException;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        public Class<?> getSuperclass() {
            return Object.class;
        }

        public Object get(TestSqlVisitor testSqlVisitor) throws IllegalAccessException {
            if (throwException) {
                throw new IllegalAccessException("Test Exception");
            }
            return "testValue";
        }

        public Field[] getDeclaredFields() {
            try {
                return new Field[]{TestSqlVisitor.class.getDeclaredField("value")};
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }
}