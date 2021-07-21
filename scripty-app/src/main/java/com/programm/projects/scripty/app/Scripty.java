package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.ScriptyContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import static com.programm.projects.scripty.app.FileUtils.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class Scripty {

    private static final String BASE_REPO = "https://raw.githubusercontent.com/1Programm/Scripty/master/sy.repo";

    private File workspaceFolder;
    private File repoFile;
    private File modulesFile;
    private final List<String> repos = new ArrayList<>();
    private final Map<String, String> moduleMap = new HashMap<>();
    private final List<Module> modules = new ArrayList<>();

    public void init(String workspace){
        try {
            this.workspaceFolder = getCreateDir(workspace);
            this.repoFile = new File(workspaceFolder, "sy.repos");

            if(!repoFile.exists()){
                initReposFile();
            }

            this.modulesFile = new File(workspaceFolder, "sy.modules");

            if(!modulesFile.exists()){
                initModulesFile();
            }

            initRepos();
            loadModules();
            initModules();
        }
        catch (Exception e){
            System.err.println("Error while initializing workspace: " + e);
            System.exit(-1);
        }
    }

    private void initReposFile() throws IOException {
        if(!repoFile.createNewFile()){
            throw new IllegalStateException("Could not create file [" + repoFile.getAbsolutePath() + "]!");
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(repoFile))){
            writer.write("[\n   \"" + BASE_REPO + "\"\n]");
        }
    }

    private void initModulesFile() throws IOException {
        if(!modulesFile.createNewFile()){
            throw new IllegalStateException("Could not create file [" + modulesFile.getAbsolutePath() + "]!");
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(modulesFile))){
            writer.write("{\n\n}");
        }
    }

    private void initRepos() throws IOException, ParseException {
        ModulesLoader.loadRepos(repoFile, repos);

        System.out.println("Finished loading repos: " + Arrays.toString(repos.toArray()));
    }

    private String lookupModule(String moduleName) throws IOException, ParseException {
        System.out.println("Lookup: " + moduleName);
        for(String repo : repos){
            JSONObject oRepo = (JSONObject) ModulesLoader.readJsonFromUrl(repo);

            String repoName = oRepo.get("name").toString();
            JSONArray oModules = (JSONArray) oRepo.get("modules");

            for(Object oModule : oModules){
                JSONObject module = (JSONObject) oModule;

                String name = module.get("name").toString();
                if(moduleName.equals(name)){
                    System.out.println("Found module [" + moduleName + "] in repo: [" + repoName + "].");

                    return module.get("url").toString();
                }
            }
        }

        throw new IOException("Could not find module: [" + moduleName + "] in the repositories!");
    }

    private void loadModules(){
        try {
            System.out.println("Loading Modules...");
            JSONObject root = (JSONObject) ModulesLoader.readJsonFromFile(modulesFile);
            System.out.println("Loaded Modules: " + root);
            for(Object oName : root.keySet()){
                String name = oName.toString();
                String value = root.get(oName).toString();

                File installedModule = new File(value);

                if(!installedModule.exists()){
                    String moduleUrl = lookupModule(name);
                    ModulesLoader.createModule(moduleUrl, installedModule);
                }
            }
        }
        catch (IOException | ParseException e){
            System.err.println("Error reading sy.modules file: " + e);
            System.exit(-1);
        }
    }

    private void initModules(){
        try {
            System.out.println("Initializing Modules...");
            JSONObject root = (JSONObject) ModulesLoader.readJsonFromFile(modulesFile);
            for(Object oName : root.keySet()){
                String name = oName.toString();
                String moduleFolder = root.get(oName).toString();

                if(!moduleFolder.endsWith("/")) moduleFolder += "/";

                String modulePath = moduleFolder + "sy.module";
                JSONObject oModule = (JSONObject) ModulesLoader.readJsonFromPath(modulePath);

                File installedModule = new File(moduleFolder);

                if(!installedModule.exists()){
                    throw new IOException("Module [" + name + "] is not installed properly!");
                }

                String modulePackage = oModule.get("module-package").toString();
                String moduleEntry = oModule.get("module-entry").toString();

                ClassLoader classLoader = new URLClassLoader(new URL[] { installedModule.toURI().toURL() });
                String startClass = modulePackage + "." + moduleEntry;

                try {
                    Class<?> moduleEntryClass = classLoader.loadClass(startClass);
                    Object o = moduleEntryClass.getConstructor().newInstance();

                    if(o instanceof Module){
                        Module m = (Module) o;
                        ScriptyContext context = new ScriptyContext() {
                            @Override
                            public String getModuleName() {
                                return "test-module";
                            }

                            @Override
                            public IOutput out() {
                                return new IOutput() {
                                    @Override
                                    public void print(String s) {
                                        System.out.print(s);
                                    }

                                    @Override
                                    public void println(String s) {
                                        System.out.println(s);
                                    }

                                    @Override
                                    public void newLine() {
                                        System.out.println();
                                    }
                                };
                            }
                        };
                        m.init(context);
                    }

                } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException | ParseException e){
            System.err.println("Error reading sy.modules file: " + e);
            System.exit(-1);
        }
    }

    public void run(String command, Args args){

    }

}
