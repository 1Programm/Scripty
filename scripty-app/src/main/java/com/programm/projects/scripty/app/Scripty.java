package com.programm.projects.scripty.app;

import com.programm.projects.ioutils.log.api.in.IInput;
import com.programm.projects.ioutils.log.api.out.IFormattedOutput;
import com.programm.projects.ioutils.log.api.out.IOutput;
import com.programm.projects.ioutils.log.console.in.ScannerIn;
import com.programm.projects.ioutils.log.console.out.*;
import com.programm.projects.scripty.app.files.SyWorkspace;
import com.programm.projects.scripty.app.modules.ModulesHandler;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.IWorkspace;

public class Scripty implements IContext {

    public static final String FILE_REPOS = "sy.repos";
    public static final String FILE_REPO = "sy.repo";
    public static final String FILE_MODULES = "sy.modules";
    public static final String FILE_MODULE = "sy.module";

    private static IFormattedOutput configuredOut(){
        return new ConsoleOut()
                .addFormatter(new ReplaceArgsFormatter("{}"))
                .addFormatter(new RichFormatter("$"))
                .addFormatter(new AlignmentFormatter("%"));
    }

    private final EnableOut log = new EnableOut(configuredOut(), true);
    private final IOutput out = configuredOut();
    private final IOutput err = configuredOut().addFormatter(new PrependFormatter("[ERROR]: "));
    private final IInput in = new ScannerIn(System.in);

    private final SyWorkspace workspace;
    private final ModulesHandler handler = new ModulesHandler();

    public Scripty(String workspacePath){
        this.workspace = new SyWorkspace(workspacePath);
    }

    public void init(){
        if(!workspace.loadWorkspace(this)){
            err.println("Failed to set up workspace!");
            workspace.deleteWorkspace(this);

            err.println("Exiting Scripty ...");
            System.exit(1);
        }
    }

    public void run(String input){

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
