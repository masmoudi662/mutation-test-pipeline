java
package py4j.reflection;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ReflectionEngineTest {

    @Test
    public void testGetMethod_existingMethod() {
        ReflectionEngine engine = new ReflectionEngine();
        Class<?> clazz = String.class;
        String methodName = "length";
        Method method = engine.getMethod(clazz, methodName);
        assertNotNull(method);
        assertEquals(methodName, method.getName());
    }

    @Test
    public void testGetMethod_nonExistingMethod() {
        ReflectionEngine engine = new ReflectionEngine();
        Class<?> clazz = String.class;
        String methodName = "nonExistingMethod";
        Method method = engine.getMethod(clazz, methodName);
        assertNull(method);
    }

    @Test
    public void testGetMethod_nullClass() {
        ReflectionEngine engine = new ReflectionEngine();
        String methodName = "length";
        Method method = engine.getMethod(null, methodName);
        assertNull(method);
    }

    @Test
    public void testGetMethod_nullMethodName() {
        ReflectionEngine engine = new ReflectionEngine();
        Class<?> clazz = String.class;
        Method method = engine.getMethod(clazz, null);
        assertNull(method);
    }

    @Test
    public void testGetMethod_emptyMethodName() {
        ReflectionEngine engine = new ReflectionEngine();
        Class<?> clazz = String.class;
        Method method = engine.getMethod(clazz, "");
        assertNull(method);
    }

    @Test
    public void testGetMethod_inheritedMethod() throws NoSuchMethodException {
        ReflectionEngine engine = new ReflectionEngine();
        Class<?> clazz = Integer.class;
        String methodName = "toString";
        Method expectedMethod = Object.class.getMethod(methodName);
        Method actualMethod = engine.getMethod(clazz, methodName);
        assertNotNull(actualMethod);
        assertEquals(expectedMethod.getName(), actualMethod.getName());
    }
}