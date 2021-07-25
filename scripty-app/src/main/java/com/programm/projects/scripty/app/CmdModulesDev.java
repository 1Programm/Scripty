package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;
import com.programm.projects.scripty.modules.api.SyModuleConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdModulesDev implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        File userPathFolder = new File(ctx.workspace().userPath());
        File devModuleFile = new File(userPathFolder, "sy.module");

        //Create new - no 'sy.module' file is at userPath
        if(!devModuleFile.exists()){
            io.out().print("Do you want to create a new module? [Y/n]: ");
            boolean answer = IOUtils.getYNAnswer(io.in(), true);

            if(!answer){
                io.out().println("Cancelled module creation.");
                return;
            }

            String name;
            String version;
            List<String> authors;
            String moduleFolder = "";
            String modulePackage = "";
            List<String> moduleFiles = null;
            String moduleEntry = "";

            io.out().newLine();
            io.out().println("--- Creating new Module (interactive) ---");
            io.out().println("(Hitting only 'Enter' at questions will use [defaults]");
            io.out().newLine();

            io.out().print("What name should your module have? [current folder name]: ");
            String a_name = io.in().next();

            if(a_name.isBlank()){
                name = userPathFolder.getName();
                io.out().println("> " + name);
            }
            else {
                name = a_name;
            }


            io.out().newLine();
            io.out().print("What should the starting version be? [1.0-SNAPSHOT]: ");
            String a_version = io.in().next();

            if(a_version.isBlank()){
                version = "1.0-SNAPSHOT";
                io.out().println("> " + version);
            }
            else {
                version = a_version;
            }


            io.out().newLine();
            io.out().print("Enter authors for this module separated by a comma. []: ");
            String a_authors = io.in().next();

            authors = new ArrayList<>();

            if(!a_authors.isBlank()){
                String[] _authors = a_authors.split(",");
                for (String author : _authors) {
                    authors.add(author.trim());
                }
            }
            else {
                io.out().println("[]");
            }

            io.out().newLine();
            io.out().println("What is the starting point - package for your module (Root package and every class is inside that).");

            List<String> packageProposals = getModulePackageProposals(userPathFolder);






            SyModuleConfig config = new ScriptyModuleConfig(name, version, authors, moduleFolder, modulePackage, moduleFiles, moduleEntry);

            ScriptyCoreContext context = (ScriptyCoreContext) ctx;
            try {
                context.workspace.writeModuleConfigFile(config, devModuleFile);
            }
            catch (IOException e){
                throw new CommandExecutionException("Exception writing module file: " + e.getMessage());
            }
            return;
        }
    }

    private List<String> getModulePackageProposals(File userFolder){
        List<String> proposals = new ArrayList<>();
        File[] children = userFolder.listFiles();

        //Check java - maven - project structure
        if(children != null){

        }


        return proposals;
    }

    @Override
    public void printHelp(IOutput out) {

    }
}
