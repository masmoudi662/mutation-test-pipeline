java
package org.cloudml.ui.shell;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShellModeTest {

    @Test
    void from_noArguments_returnsInteractiveMode() {
        ShellMode mode = ShellMode.from();
        assertTrue(mode instanceof InteractiveMode);
    }

    @Test
    void from_batchArgument_returnsBatchMode() {
        ShellMode mode = ShellMode.from("--batch", "command");
        assertTrue(mode instanceof BatchMode);
    }

    @Test
    void from_batchArgumentNoCommand_returnsBatchModeWithEmptyCommand() {
        ShellMode mode = ShellMode.from("--batch");
        assertTrue(mode instanceof BatchMode);
    }

    @Test
    void from_batchArgumentMultipleArguments_returnsBatchModeWithCorrectCommand() {
        ShellMode mode = ShellMode.from("--batch", "command", "arg1", "arg2");
        assertTrue(mode instanceof BatchMode);
    }

    @Test
    void from_interactiveArgument_returnsInteractiveMode() {
        ShellMode mode = ShellMode.from("--interactive");
        assertTrue(mode instanceof InteractiveMode);
    }

    @Test
    void from_mixedArguments_batchWins() {
       ShellMode mode = ShellMode.from("--batch", "command", "--interactive");
        assertTrue(mode instanceof BatchMode);
    }
    
    @Test
    void from_emptyArgument_interactive() {
        ShellMode mode = ShellMode.from("");
        assertTrue(mode instanceof InteractiveMode);
    }

    @Test
    void from_nullArgument_interactive() {
        ShellMode mode = ShellMode.from((String) null);
        assertTrue(mode instanceof InteractiveMode);
    }
}