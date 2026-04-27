java
package com.consoledrawing.command;

import com.consoledrawing.command.exception.InvalidCommandException;
import com.consoledrawing.command.exception.InvalidCommandParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    @Test
    void getCommand_QuitCommand() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("Q");
        assertTrue(command instanceof QuitCommand);
    }

    @Test
    void getCommand_CreateCommand() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("C 20 4");
        assertTrue(command instanceof CreateCommand);
    }

    @Test
    void getCommand_DrawLineCommand() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("L 1 2 6 2");
        assertTrue(command instanceof DrawLineCommand);
    }

    @Test
    void getCommand_DrawRectangleCommand() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("R 14 1 18 3");
        assertTrue(command instanceof DrawRectangleCommand);
    }

    @Test
    void getCommand_BucketFillCommand() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("B 10 3 o");
        assertTrue(command instanceof BucketFillCommand);
    }

    @Test
    void getCommand_InvalidCommandException() {
        CommandFactory commandFactory = new CommandFactory();
        assertThrows(InvalidCommandException.class, () -> commandFactory.getCommand("X"));
    }

    @Test
    void getCommand_multiple_spaces() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("C   20  4");
        assertTrue(command instanceof CreateCommand);
    }

    @Test
    void getCommand_leading_and_trailing_spaces() throws InvalidCommandException, InvalidCommandParams {
        CommandFactory commandFactory = new CommandFactory();
        Command        command        = commandFactory.getCommand("  C 20 4  ");
        assertTrue(command instanceof CreateCommand);
    }
}