package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;

public class Scripty {

    private static final String BASE_REPO = "https://raw.githubusercontent.com/1Programm/Scripty/master/sy.repo";

    private final IOutput log = new ScriptyOut(System.out, true);
    private final IOutput err = new ScriptyOut(System.err, true);

    private final ScriptyWorkspace workspace;

    public Scripty() {
        this.workspace = new ScriptyWorkspace(log, err);
    }

    public void init(String workspacePath){
        log.println("Initializing Workspace ...");

        try {
            workspace.setupWorkspace(workspacePath);
        }
        catch (Exception e){
            err.println("Error while initializing workspace: " + e);
            System.exit(-1);
        }
    }

    public void run(String command, Args args){

    }

}
