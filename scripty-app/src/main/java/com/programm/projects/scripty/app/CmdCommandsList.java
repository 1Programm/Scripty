package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyCommandInfo;
import com.programm.projects.scripty.modules.api.SyContext;

import java.util.ArrayList;
import java.util.List;

class CmdCommandsList implements SySysCommand {

    @Override
    public void run(SyContext ctx, String name, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

        List<String> commands = new ArrayList<>(context.commandMap.keySet());
        commands.sort(String::compareToIgnoreCase);

        ctx.out().println("# Commands:");
        for(String command : commands){
            SyCommand cmd = context.getCommand(command);
            SyCommandInfo info = cmd.info();

            if(info == null){
                ctx.out().println("| " + command);
            }
            else {
                String type = info.type();

                if(type.equals("build-in")){
                    if(!(cmd instanceof SySysCommand)){
                        type = "\"build-in\"";
                    }
                }

                ctx.out().println("| %20<(" + command + ") [" + type + "]");
            }
        }
    }

    @Override
    public void printHelp(IOutput out) {
        //TODO
        out.println("TODO");
    }
}
