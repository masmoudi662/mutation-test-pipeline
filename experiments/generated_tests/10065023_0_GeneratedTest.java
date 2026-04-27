java
package org.neuro4j.workflow.tutorial;

import org.junit.Test;
import org.neuro4j.workflow.FlowContext;
import org.neuro4j.workflow.common.FlowExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.neuro4j.workflow.tutorial.HelloWorld.IN_NAME;
import static org.neuro4j.workflow.tutorial.HelloWorld.OUT_MESSAGE;

public class HelloWorldTest {

    @Test
    public void testExecuteWithName() throws FlowExecutionException {
        HelloWorld helloWorld = new HelloWorld();
        FlowContext ctx = new FlowContext();
        ctx.put(IN_NAME, "TestUser");
        helloWorld.execute(ctx);
        assertEquals("Hello World! TestUser", ctx.get(OUT_MESSAGE));
    }

    @Test
    public void testExecuteWithoutName() throws FlowExecutionException {
        HelloWorld helloWorld = new HelloWorld();
        FlowContext ctx = new FlowContext();
        helloWorld.execute(ctx);
        assertEquals("Hello World! ", ctx.get(OUT_MESSAGE));
    }

    @Test
    public void testExecuteWithEmptyName() throws FlowExecutionException {
        HelloWorld helloWorld = new HelloWorld();
        FlowContext ctx = new FlowContext();
        ctx.put(IN_NAME, "");
        helloWorld.execute(ctx);
        assertEquals("Hello World! ", ctx.get(OUT_MESSAGE));
    }

    @Test
    public void testExecuteWithNullName() throws FlowExecutionException {
        HelloWorld helloWorld = new HelloWorld();
        FlowContext ctx = new FlowContext();
        ctx.put(IN_NAME, null);
        helloWorld.execute(ctx);
        assertEquals("Hello World! ", ctx.get(OUT_MESSAGE));
    }

    @Test
    public void testExecuteContextInitialState() throws FlowExecutionException {
        HelloWorld helloWorld = new HelloWorld();
        FlowContext ctx = new FlowContext();
        assertNull(ctx.get(OUT_MESSAGE));
        helloWorld.execute(ctx);
    }
}