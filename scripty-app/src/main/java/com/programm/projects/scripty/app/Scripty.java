package com.programm.projects.scripty.app;

import com.programm.projects.ioutils.log.api.in.IInput;
import com.programm.projects.ioutils.log.api.out.IFormattedOutput;
import com.programm.projects.ioutils.log.api.out.IOutput;
import com.programm.projects.ioutils.log.console.in.ScannerIn;
import com.programm.projects.ioutils.log.console.out.*;
import com.programm.projects.plugz.Plugz;
import com.programm.projects.plugz.magic.MagicEnvironment;
import com.programm.projects.scripty.app.commands.CommandExecutionException;
import com.programm.projects.scripty.app.commands.CommandManager;
import com.programm.projects.scripty.app.commands.CommandNamingException;
import com.programm.projects.scripty.app.commands.CommandSetupException;
import com.programm.projects.scripty.app.files.SyWorkspace;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.IWorkspace;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class Scripty implements IContext {

    public static final String FILE_REPOS = "sy.repos";
    public static final String FILE_REPO = "sy.repo";
    public static final String FILE_MODULES = "sy.modules";
    public static final String FILE_MODULE = "sy.module";

    private static final boolean DEBUG = false;

    private static EnableOut createLog(){
        return new EnableOut(
                new ConsoleOut()
                .addFormatter(new PrependFormatter("[DEBUG]: "))
                .addFormatter(new ReplaceArgsFormatter("{}"))
                .addFormatter(new RichFormatter("$"))
                .addFormatter(new AlignmentFormatter("%")), DEBUG);
    }

    private static IOutput createOut(){
        return new ConsoleOut()
                .addFormatter(new ReplaceArgsFormatter("{}"))
                .addFormatter(new RichFormatter("$"))
                .addFormatter(new AlignmentFormatter("%"));
    }

    private static IOutput createErr(){
        return new ConsoleOut()
                .addFormatter(new PrependFormatter("${red}([ERROR]): "))
                .addFormatter(new ReplaceArgsFormatter("{}"))
                .addFormatter(new RichFormatter("$"))
                .addFormatter(new AlignmentFormatter("%"));
    }

    private final EnableOut log = createLog();
    private final IOutput out = createOut();
    private final IOutput err = createErr();
    private final IInput in = new ScannerIn(System.in);

    private final SyWorkspace workspace;
    private final MagicEnvironment env;
    private final CommandManager commandManager;

    public Scripty(String workspacePath){
        this.workspace = new SyWorkspace(workspacePath);
        this.env = new MagicEnvironment("com.programm.projects.scripty");
        this.commandManager = new CommandManager();

        this.env.addSearchAnnotation(Command.class);
        this.env.registerInstance(IContext.class, this);
        this.env.registerInstance(SyWorkspace.class, workspace);
        this.env.registerInstance(CommandManager.class, commandManager);

        if(DEBUG) Plugz.setLogger(new ConsoleOut());
    }

    public void init(){
        if(!workspace.loadWorkspace(this)){
            err.println("Failed to set up workspace!");
            workspace.deleteWorkspace(this);

            err.println("Exiting Scripty ...");
            System.exit(1);
        }


        Map<URL, String> moduleUrlWithBasePackagesMap = workspace.collectModuleUrls(this);
        for(URL url : moduleUrlWithBasePackagesMap.keySet()){
            String basePackage = moduleUrlWithBasePackagesMap.get(url);
            env.addUrl(url, basePackage);
        }

        env.startup();

        List<Class<?>> commandClasses = env.getAnnotatedWith(Command.class);

        for(Class<?> cls : commandClasses){
            Object cmd = env.instantiateClass(cls);
            try {
                commandManager.registerCommand(cmd);
            }
            catch (CommandNamingException e){
                err.println(e.getMessage());
                err.println("Command from class [" + cls.getName() + "] will not be registered!");
            }
            catch (CommandSetupException e){
                err.println("Setup exception while registering command from class [" + cls.getName() + "]!");
                err.println(e.getMessage());
            }
        }

        env.postSetup();
    }

    @Override
    public void run(String input){
        if(input.equals("")){
            return;
        }

        int nextSpace = input.indexOf(' ');
        String name, rest;

        if(nextSpace == -1) {
            name = input;
            rest = "";
        }
        else {
            name = input.substring(0, nextSpace);
            rest = input.substring(nextSpace + 1);
        }

        try {
            commandManager.run(name, rest);
        } catch (CommandExecutionException e) {
            err.println(e.getMessage());
        }
    }

    @Override
    public IOutput log() {
        return log;
    }

    @Override
    public IOutput out() {
        return out;
    }

    @Override
    public IOutput err() {
        return err;
    }

    @Override
    public IInput in() {
        return in;
    }

    @Override
    public IWorkspace workspace() {
        return workspace;
    }
}
