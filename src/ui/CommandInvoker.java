package ui;

import java.util.Stack;

public class CommandInvoker {
    private final Stack<Command> history = new Stack<>();

    public void invoke(Command command) {
        command.execute();
        history.push(command);
    }

    public Command undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            return command;
        }
        return null;
    }
}

