package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.modules.api.Module;
import org.json.simple.parser.ParseException;

import static com.programm.projects.scripty.app.FileUtils.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scripty {

    private static final String BASE_REPO = "https://raw.githubusercontent.com/1Programm/Scripty/master/sy.repo";

    private File workspaceFolder;
    private File repoFile;
    private File modulesFile;
    private final List<String> repos = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();

    public void init(String workspace){
        try {
            this.workspaceFolder = getCreateDir(workspace);
            this.repoFile = new File(workspaceFolder, "sy.repos");

            if(!repoFile.exists()){
                initRepo();
            }

            this.modulesFile = getCreateFile(workspaceFolder, "sy.modules");

            loadModules();
            initModules();
        }
        catch (Exception e){
            System.err.println("Error while initializing workspace: " + e.getMessage());
            System.exit(-1);
        }
    }

    private void initRepo() throws IOException {
        if(!repoFile.createNewFile()){
            throw new IllegalStateException("Could not create file [" + repoFile.getAbsolutePath() + "]!");
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(repoFile))){
            writer.write("[\n   " + BASE_REPO + "\n]");
        }
    }

    private void loadModules(){
        try {
            ModulesLoader.loadRepos(repoFile, repos);
        } catch (IOException | ParseException e) {
            System.err.println("Error loading the repositories file: " + e.getMessage());
            System.exit(-1);
        }

        System.out.println("Finished loading repos file.");

        try {
            ModulesLoader.loadModules(repos, modules);
        } catch (IOException | ParseException e) {
            System.err.println("Error loading a repository: " + e.getMessage());
            System.exit(-1);
        }

        System.out.println("Finished loading modules.");
    }

    private void initModules(){

    }

    public void run(String command, Args args){

    }

}
