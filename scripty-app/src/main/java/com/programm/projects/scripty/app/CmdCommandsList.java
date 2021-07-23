package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.*;

import java.util.ArrayList;
import java.util.List;

class CmdCommandsList implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        List<String> commands = new ArrayList<>(context.commandManager.commandMap.keySet());
        commands.sort(String::compareToIgnoreCase);

        io.out().println("# Commands:");
        for(String command : commands){
            SyCommand cmd = context.commandManager.commandMap.get(command);
            SyCommandInfo info = cmd.info();

            if(info == null){
                io.out().println("| " + command);
            }
            else {
                String type = info.type();

                if(type.equals("build-in")){
                    if(!(cmd instanceof SySysCommand)){
                        type = "\"build-in\"";
                    }
                }

                io.out().println("| %20<(" + command + ") [" + type + "]");
            }
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }
}
