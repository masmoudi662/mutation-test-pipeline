java
package com.deepoove.swagger.dubbo.http;

import com.deepoove.swagger.dubbo.reader.NameDiscover;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.HttpMethod;
import io.swagger.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpMatch {

    private static final Logger logger = LoggerFactory.getLogger(HttpMatch.class);
    private Class<?> refClass;
    private NameDiscover nameDiscover;

    public HttpMatch() {
    }

    public HttpMatch(Class<?> refClass, NameDiscover nameDiscover) {
        this.refClass = refClass;
        this.nameDiscover = nameDiscover;
    }

    public Method[] findRefMethods(Method[] interfaceMethods, String operationId,
                                   String requestMethod) {
        List<Method> ret = new ArrayList<Method>();
        for (Method method : interfaceMethods) {
            Method m;
            try {
                m = refClass.getMethod(method.getName(), method.getParameterTypes());
                final ApiOperation apiOperation = ReflectionUtils.getAnnotation(m,
                        ApiOperation.class);
                String nickname = null == apiOperation ? null : apiOperation.nickname();
                if (operationId != null) {
                    if (!operationId.equals(nickname)) continue;
                } else {
                    if (StringUtils.isNotBlank(nickname)) continue;
                }
                if (requestMethod != null) {
                    String httpMethod = null == apiOperation ? null : apiOperation.httpMethod();
                    if (StringUtils.isNotBlank(httpMethod) && !requestMethod.equals(httpMethod))
                        continue;
                    if (StringUtils.isBlank(httpMethod)
                            && !requestMethod.equalsIgnoreCase(HttpMethod.POST.name()))
                        continue;
                }
                ret.add(m);
            } catch (NoSuchMethodException e) {
                logger.error("NoSuchMethodException", e);
            } catch (SecurityException e) {
                logger.error("SecurityException", e);
            }
        }
        return ret.toArray(new Method[] {});
    }


    public void setRefClass(Class<?> refClass) {
        this.refClass = refClass;
    }

    static class TestClass {
        @ApiOperation(nickname = "testOperation", httpMethod = "GET")
        public void testMethod() {}

        @ApiOperation(httpMethod = "POST")
        public void testMethodPost() {}

        public void testMethodNoAnnotation() {}

        @ApiOperation(nickname = "anotherOperation")
        public void anotherMethod() {}
    }


    public static class HttpMatchTest {

        @Test
        public void testFindRefMethods_matchingOperationIdAndHttpMethod() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethod");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, "testOperation", "GET");
            assertEquals(1, refMethods.length);
            assertEquals("testMethod", refMethods[0].getName());
        }

        @Test
        public void testFindRefMethods_matchingHttpMethodOnly() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethodPost");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, null, "POST");
            assertEquals(1, refMethods.length);
            assertEquals("testMethodPost", refMethods[0].getName());
        }

        @Test
        public void testFindRefMethods_noMatchingOperationId() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethod");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, "wrongOperation", "GET");
            assertEquals(0, refMethods.length);
        }

        @Test
        public void testFindRefMethods_noMatchingHttpMethod() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethod");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, "testOperation", "POST");
            assertEquals(0, refMethods.length);
        }

        @Test
        public void testFindRefMethods_noAnnotationAndPost() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethodNoAnnotation");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, null, "POST");
            assertEquals(1, refMethods.length);
            assertEquals("testMethodNoAnnotation", refMethods[0].getName());
        }

        @Test
        public void testFindRefMethods_noAnnotationAndGet() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethodNoAnnotation");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, null, "GET");
            assertEquals(0, refMethods.length);
        }

        @Test
        public void testFindRefMethods_emptyOperationIdAndNicknameExists() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("anotherMethod");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, null, "POST");
            assertEquals(0, refMethods.length);
        }

        @Test
        public void testFindRefMethods_emptyOperationIdAndHttpMethodNull() throws NoSuchMethodException {
            HttpMatch httpMatch = new HttpMatch();
            httpMatch.setRefClass(TestClass.class);
            Method interfaceMethod = TestClass.class.getMethod("testMethodNoAnnotation");
            Method[] interfaceMethods = new Method[]{interfaceMethod};
            Method[] refMethods = httpMatch.findRefMethods(interfaceMethods, null, null);
            assertEquals(1, refMethods.length);
        }
    }
}