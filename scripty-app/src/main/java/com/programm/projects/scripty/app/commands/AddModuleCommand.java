package com.programm.projects.scripty.app.commands;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.scripty.app.files.SyWorkspace;
import com.programm.projects.scripty.app.files.WorkspaceException;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;

@Command("module-add")
public class AddModuleCommand {

    @Get
    private IContext ctx;

    @Get
    private SyWorkspace workspace;

    @Command
    public void run(String name, String input){
        String url = workspace.findModuleUrl(ctx, input);

        try {
            workspace.downloadAndAddModule(ctx, url);
        }
        catch (WorkspaceException e){
            ctx.err().println(e.getMessage());
        }
    }

}
