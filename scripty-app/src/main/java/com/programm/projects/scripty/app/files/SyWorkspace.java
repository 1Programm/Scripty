package com.programm.projects.scripty.app.files;

import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.IWorkspace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SyWorkspace implements IWorkspace {

    private final String workspacePath;

    private ReposConfigFile reposConfigFile;
    private ModulesConfigFile modulesConfigFile;

    public SyWorkspace(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public boolean loadWorkspace(IContext ctx){
        File workspaceFolder = new File(workspacePath);

        if(!workspaceFolder.exists()){
            ctx.log().println("Workspace could not be found!");
            ctx.log().println("Setting up workspace at [{}] ...", workspacePath);

            if(!workspaceFolder.mkdirs()){
                ctx.err().println("Could not create workspace directory at [{}] ...", workspacePath);
                ctx.log().println("Cancelling workspace setup.");
                return false;
            }
        }

        File reposFile = new File(workspaceFolder, "sy.repos");

        if(!reposFile.exists()){
            ctx.log().println("Repos file could not be found!");
            ctx.log().println("Creating new repos file with default repo at [{}] ...", reposFile.getAbsolutePath());

            try {
                if (!reposFile.createNewFile()) {
                    throw new IOException();
                }
            }
            catch (IOException e){
                String msg = e.getMessage();
                ctx.err().println("Could not create [sy.repos] - file at [{}].", reposFile.getAbsolutePath());
                if(msg != null) ctx.err().println(msg);
                ctx.log().println("Cancelling workspace setup.");
                return false;
            }

            Properties properties = new Properties();
            properties.put("repos.repo0", "https://raw.githubusercontent.com/1Programm/Scripty/master/sy.repo");

            try (FileOutputStream outputStream = new FileOutputStream(reposFile)){
                properties.store(outputStream, null);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("INVALID STATE - File [" + reposFile.getAbsolutePath() + "] should exist as it was just created without problems but does not.", e);
            } catch (IOException e) {
                ctx.err().println("Could not write to [sy.repos] - file at [{}].", reposFile.getAbsolutePath());
                ctx.err().println(e.getMessage());
                ctx.log().println("Cancelling workspace setup.");
                return false;
            }
        }

        try {
            reposConfigFile = ConfigFileLoader.reposConfigFileLoader(reposFile);
        } catch (IOException e) {
            ctx.err().println("Could not read [sy.repos] - file at: [{}].", reposFile.getAbsolutePath());
            ctx.err().println(e.getMessage());
            ctx.log().println("Cancelling workspace setup.");
            return false;
        }



        File modulesFile = new File(workspaceFolder, "sy.modules");

        if(!modulesFile.exists()){
            ctx.log().println("Modules file could not be found!");
            ctx.log().println("Creating new modules file with no modules ...");

            try {
                if (!modulesFile.createNewFile()) {
                    throw new IOException();
                }
            }
            catch (IOException e){
                String msg = e.getMessage();
                ctx.err().println("Could not create [sy.modules] - file at [{}].", modulesFile.getAbsolutePath());
                if(msg != null) ctx.err().println(msg);
                ctx.log().println("Cancelling workspace setup.");
                return false;
            }
        }

        try {
            modulesConfigFile = ConfigFileLoader.modulesConfigFileLoader(modulesFile);
        } catch (IOException e) {
            ctx.err().println("Could not read [sy.modules] - file at: [{}].", modulesFile.getAbsolutePath());
            ctx.err().println(e.getMessage());
            ctx.log().println("Cancelling workspace setup.");
            return false;
        }



        return true;
    }

    public Map<URL, String> collectModuleUrls(IContext ctx){
        Map<URL, String> moduleUrls = new HashMap<>();
        Map<String, String> nameToUrlMap = modulesConfigFile.getModuleNameToUrlMap();

        for(String name : nameToUrlMap.keySet()){
            String rootModulePath = nameToUrlMap.get(name);
            File moduleFile = new File(rootModulePath, "sy.module");

            ModuleConfigFile moduleConfigFile;

            try {
                moduleConfigFile = ConfigFileLoader.moduleConfigFileLoader(moduleFile);
            } catch (IOException e) {
                ctx.err().println("Could not read module file at: [" + moduleFile.getAbsolutePath() + "]!");
                continue;
            }

            String rootFolder = moduleConfigFile.getRootFolder();

            File rootFolderFile = new File(rootModulePath, rootFolder);
            URL url;

            try {
                url = rootFolderFile.toURI().toURL();
            } catch (MalformedURLException e) {
                ctx.err().println("File [" + rootFolderFile.getAbsolutePath() + "] could not be parsed to a URL.");
                continue;
            }

            String basePackage = moduleConfigFile.getBasePackage();
            moduleUrls.put(url, basePackage);
        }

        return moduleUrls;
    }

    public void deleteWorkspace(IContext ctx){
        ctx.log().println("Deleting Workspace ...");

        File file = new File(workspacePath);
        if(!_recDeleteWorkspace(file)){
            ctx.err().println("Could not completely delete Workspace ...");
        }
        else {
            ctx.log().println("Workspace was deleted successfully!");
        }
    }

    private boolean _recDeleteWorkspace(File file){
        boolean success = true;

        if(file.isDirectory()){
            File[] children = file.listFiles();
            if(children != null){
                for(File child : children){
                    if(!_recDeleteWorkspace(child)){
                        success = false;
                    }
                }
            }
        }

        if(!file.delete()){
            success = false;
        }

        return success;
    }

}
