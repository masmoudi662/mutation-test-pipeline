java
package org.apache.wiki.ui;

import org.apache.wiki.TestEngine;
import org.apache.wiki.api.core.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedirectCommandTest {

    private RedirectCommand command;

    @BeforeEach
    public void setUp() {
        command = (RedirectCommand) RedirectCommand.REDIRECT.targetedCommand("testTarget");
    }

    @Test
    public void testTargetedCommandWithValidTarget() {
        Command targetedCommand = RedirectCommand.REDIRECT.targetedCommand( "http://example.com" );
        assertNotNull( targetedCommand );
        assertEquals( "http://example.com", targetedCommand.getName() );
    }

    @Test
    public void testTargetedCommandWithInvalidTarget() {
        assertThrows( IllegalArgumentException.class, () -> RedirectCommand.REDIRECT.targetedCommand( Integer.valueOf( 123 ) ) );
    }

    @Test
    public void testGetName() {
        assertEquals("testTarget", command.getName());
    }

    @Test
    public void testRequiredPermission() {
        assertEquals(null, command.requiredPermission());
    }
}