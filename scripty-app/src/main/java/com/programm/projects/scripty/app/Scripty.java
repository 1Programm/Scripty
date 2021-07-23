package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.io.IOException;

public class Scripty {

    private final ScriptyOut out = new ScriptyOut(System.out, true);
    private final ScriptyOut log = new ScriptyOut(System.out, false);
    private final ScriptyOut err = new ScriptyOut(System.err, true);

    private final ScriptyWorkspace workspace;
    private final ScriptyModulesManager modulesManager;
    private final ScriptyCoreContext context;

    public Scripty() {
        this.workspace = new ScriptyWorkspace(out, log, err);
        this.modulesManager = new ScriptyModulesManager(log, err);
        this.context = new ScriptyCoreContext(out, log, err, workspace);

        try {
            context.registerCommand("help", new CmdHelp());
            context.registerCommand("modules-add", new CmdModulesAdd());
            context.registerCommand("modules-remove", new CmdModulesRemove());
            context.registerCommand("modules-list", new CmdModulesList());
            context.registerCommand("commands-list", new CmdCommandsList());

        }
        catch (InvalidNameException e){
            throw new IllegalStateException("This should not happen.", e);
        }
    }

    public void init(String workspacePath){
        log.println("Initializing Workspace ...");

        try {
            workspace.setupWorkspace(workspacePath);
        }
        catch (Exception e){
            err.println("Error while initializing workspace: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void run(String command, Args args){
        int index_info = args.indexOf("-i");

        if(index_info == -1){
            index_info = args.indexOf("--info");
        }

        if(index_info != -1){
            log.enable();
            args = args.removed(index_info);
        }

        try {
            //First init Modules and then check custom commands
            workspace.loadAndInitModules(modulesManager, context);

            SyCommand cmd = context.getCommand(command);

            if(cmd == null){
                err.println("No such command: [" + command + "].");
                return;
            }

            try {
                cmd.run(context, command, args);
            }
            catch (CommandExecutionException e){
                err.println("Error executing command [" + command + "]: " + e.getMessage());
            }
        }
        catch (IOException e){
            err.println("Error while running command [" + command + "]: " + e.getMessage());
        }
    }

}
