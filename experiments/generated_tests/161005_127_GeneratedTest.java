java
package org.apache.wiki.ui;

import org.apache.wiki.TestEngine;
import org.apache.wiki.api.core.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedirectCommandTest {

    private RedirectCommand command;

    @BeforeEach
    public void setUp() throws Exception {
        command = new RedirectCommand( "view", "HomePage", null, "HomePage" );
    }

    @Test
    public void testTargetedCommandWithStringTarget() {
        Command targeted = command.targetedCommand( "AnotherPage" );
        assertNotNull( targeted );
        assertEquals( "AnotherPage", ((RedirectCommand) targeted).getTarget());
    }

    @Test
    public void testTargetedCommandWithNullTarget() {
        assertThrows(IllegalArgumentException.class, () -> command.targetedCommand(null));
    }

    @Test
    public void testTargetedCommandWithWrongTypeTarget() {
        assertThrows(IllegalArgumentException.class, () -> command.targetedCommand(123));
    }

    @Test
    public void testGetTarget() {
        assertEquals("HomePage", command.getTarget());
    }

    @Test
    public void testGetContentTemplate() {
        assertNull(command.getContentTemplate());
    }

    @Test
    public void testGetRequestcontext() {
        assertEquals("view", command.getRequestContext());
    }
}