package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyCommand;

import java.io.IOException;
import java.util.Map;

public class Scripty {

    private final ScriptyOut out = new ScriptyOut(System.out, true);
    private final ScriptyOut log = new ScriptyOut(System.out, false);
    private final ScriptyOut err = new ScriptyOut(System.err, true);

    private final ScriptyWorkspace workspace;
    private final ScriptyModulesManager modulesManager;
    private final CoreScriptyContext context;

    public Scripty() {
        this.workspace = new ScriptyWorkspace(out, log, err);
        this.modulesManager = new ScriptyModulesManager(log, err);
        this.context = new CoreScriptyContext(out, log, err);
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
            if(checkStandardCommands(command, args)){
                return;
            }

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

    private boolean checkStandardCommands(String command, Args args) throws IOException {
        if(command.equals("modules-add")){
            String moduleName = args.size() == 0 ? null : args.get(0);

            if(moduleName == null){
                err.println("[modules-add] needs a name to look for a module it should add!");
                return true;
            }

            String moduleDest = args.size() == 1 ? null : args.get(1);

            if(moduleDest == null){
                moduleDest = workspace.getWorkspacePath() + "/modules/" + moduleName;
            }

            workspace.addModule(moduleName, moduleDest);
            return true;
        }
        else if(command.equals("modules-remove")){
            String moduleName = args.size() == 0 ? null : args.get(0);

            if(moduleName == null){
                err.println("[modules-remove] needs a name to remove a module!");
                return true;
            }

            workspace.removeModule(moduleName);
            return true;
        }
        else if (command.equals("modules-list")) {
            Map<String, String> modules = workspace.listModules();

            if(modules.size() == 0){
                out.println("No modules installed. Try 'sy modules-add [name]' to add a module.");
                return true;
            }

            out.println("# Modules:");
            for(String name : modules.keySet()){
                out.println("| " + name + " - " + modules.get(name));
            }
            return true;
        }

        return false;
    }

}
