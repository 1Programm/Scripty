package com.programm.projects.scripty.app.commands;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.scripty.app.files.SyWorkspace;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;

import java.util.List;

@Command("module-list")
public class CmdModuleList {

    @Get
    private IContext ctx;

    @Get
    private SyWorkspace workspace;

    @Command
    public void run(String name, String input){
        List<String> moduleNames = workspace.listModules();

        if(moduleNames.isEmpty()){
            ctx.out().println("No modules installed yet!");
            return;
        }

        Counter maxLength = new Counter();
        maxLength.i = moduleNames.get(0).length(); //If array has only 1 element it will not go into sort

        moduleNames.sort((n1, n2) -> {
            maxLength.i = Math.max(maxLength.i, n1.length());
            return n1.compareTo(n2);
        });

        ctx.out().println("| %{}<(${yellow}(Modules):)   |", maxLength.i);
        ctx.out().println("----%{}<[-]()--", maxLength.i);

        for(String moduleName : moduleNames){
            ctx.out().println("| > %{}<({}) |", maxLength.i, moduleName);
        }
    }

}
