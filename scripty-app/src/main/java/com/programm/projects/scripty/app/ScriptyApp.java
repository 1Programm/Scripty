package com.programm.projects.scripty.app;

import com.programm.projects.ioutils.console.api.Console;
import com.programm.projects.ioutils.console.simple.SimpleConsole;
import com.programm.projects.scripty.app.cmds.StatusCmd;

public class ScriptyApp {

    public static void main(String[] args) {
        SimpleConsole console = new SimpleConsole();
        console.clearCommands();
        console.onCommandNotFound(new InvalidInput());
        console.registerCommand(new StatusCmd(), "status");

        console.run(args);
    }

}
