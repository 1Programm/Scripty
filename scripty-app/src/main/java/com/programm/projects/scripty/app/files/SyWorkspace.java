package com.programm.projects.scripty.app.files;

import com.programm.projects.scripty.app.io.SameLineWriter;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.IWorkspace;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class SyWorkspace implements IWorkspace {

    private final String workspacePath;
    private File reposFile;
    private File modulesFile;

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

        reposFile = new File(workspaceFolder, "sy.repos");

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



        modulesFile = new File(workspaceFolder, "sy.modules");

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


    //SAVE CONFIGS

    private void saveModulesConfig(ModulesConfigFile configFile) throws IOException {
        Properties p = new Properties();
        p.putAll(configFile.getModuleNameToUrlMap());

        try (FileWriter fw = new FileWriter(modulesFile)) {
            p.store(fw, null);
        }
    }



    private Properties readPropertiesFileFromURL(String urlString) throws WorkspaceException{
        Properties properties = new Properties();

        try {
            URL url = new URL(urlString);
            try (InputStream in = url.openStream()){
                properties.load(in);
            }
            catch (IOException e){
                throw new WorkspaceException("Exception while reading from url [" + url + "]", e);
            }
        }
        catch (MalformedURLException e){
            throw new WorkspaceException("Invalid url: [" + urlString + "]", e);
        }

        return properties;
    }

    private String urlConcat(String root, String add){
        if(root.endsWith("/")){
            if(add.startsWith("/")){
                return root + add.substring(1);
            }
            else {
                return root + add;
            }
        }
        else {
            if(add.startsWith("/")){
                return root + add;
            }
            else {
                return root + "/" + add;
            }
        }
    }

    private void ensureFolders(String path) throws IOException{
        File f = new File(path);

        if(!f.exists() && !f.mkdirs()){
            throw new IOException("Could not create directories for path: [" + path + "]!");
        }
    }

    //COMMAND USE


    // -------
    public String findModuleUrl(IContext ctx, String name){
        List<String> repoUrls = reposConfigFile.getRepoUrls();

        for(String repoUrl : repoUrls){
            ctx.log().println("Looking in repo [" + repoUrl + "] ...");

            try {
                String mappedUrl = findModuleUrl(ctx, name, repoUrl);

                if (mappedUrl != null) {
                    ctx.log().println("Found module [{}] in repo [{}].", name, repoUrl);
                    return mappedUrl;
                }
            }
            catch (WorkspaceException e){
                ctx.err().println(e.getMessage());
            }
        }

        return null;
    }

    private String findModuleUrl(IContext ctx, String name, String repoUrl) throws WorkspaceException{
        Properties properties = readPropertiesFileFromURL(repoUrl);
        RepoConfigFile configFile = ConfigFileLoader.repoConfigFileLoader(properties);

        for(int i=0;i<configFile.getModuleNames().size();i++){
            if(configFile.getModuleNames().get(i).equals(name)){
                return configFile.getModuleUrls().get(i);
            }
        }

        return null;
    }

    public void downloadAndAddModule(IContext ctx, String moduleUrl) throws WorkspaceException {
        SameLineWriter slw = new SameLineWriter(ctx.out());

        slw.print("[#-------]: Reading Config File...");

        String moduleFileUrl = urlConcat(moduleUrl, "sy.module");
        Properties properties = readPropertiesFileFromURL(moduleFileUrl);
        ModuleConfigFile moduleConfigFile = ConfigFileLoader.moduleConfigFileLoader(properties);

        slw.print("[##------]: Reading properties of module [" + moduleConfigFile.getName() + "]...");
        try { Thread.sleep(1000); } catch (InterruptedException ignore) {}

        slw.print("[###-----]: Setting up folders in scripty home...");
        String modulePath = createModuleFolders(ctx, moduleConfigFile);
        try { Thread.sleep(1000); } catch (InterruptedException ignore) {}


        slw.print("[####----]: Downloading module files...");
        copyModule(ctx, moduleUrl, moduleConfigFile);


        slw.print("[#######-]: Saving module name in sy.modules...");
        Map<String, String> modulesMap = new HashMap<>(modulesConfigFile.getModuleNameToUrlMap());
        modulesMap.put(moduleConfigFile.getName(), modulePath);

        try {
            saveModulesConfig(new ModulesConfigFile(modulesMap));
        }
        catch (IOException e){
            throw new WorkspaceException("Could not save modules config file for module: [" + moduleConfigFile.getName() + "]!", e);
        }

        slw.print("${back}[########]: Successfully downloaded ${yellow}([" + moduleConfigFile.getName() + "])\n");
    }

    private String createModuleFolders(IContext ctx, ModuleConfigFile config) throws WorkspaceException{
        File modulesFolder = new File(workspacePath, "modules");

        if(!modulesFolder.exists()){
            if(!modulesFolder.mkdirs()){
                throw new WorkspaceException("Could not create modules folder at: [" + modulesFolder.getAbsolutePath() + "]");
            }
        }

        File moduleFolder = new File(modulesFolder, config.getName());

        if(!moduleFolder.exists()){
            if(!moduleFolder.mkdirs()){
                throw new WorkspaceException("Could not create module folder at: [" + moduleFolder.getAbsolutePath() + "]");
            }
        }

        return moduleFolder.getAbsolutePath();
    }

    private void copyModule(IContext ctx, String rootUrl, ModuleConfigFile config) throws WorkspaceException{
        String fileRootPath = urlConcat(rootUrl, config.getRootFolder());
        String basePackageAsFilePath = config.getBasePackage().replaceAll("\\.", "/");

        String classesRootPath = urlConcat(fileRootPath, basePackageAsFilePath);

        String destModuleRootPath = workspacePath + "/modules/" + config.getName();
        String destModuleFilesPath = destModuleRootPath + "/" + config.getRootFolder() + "/" + basePackageAsFilePath;

        try {
            ensureFolders(destModuleFilesPath);
        }
        catch (IOException e){
            throw new WorkspaceException(e.getMessage());
        }

        try {
            String moduleConfigFileOrig = urlConcat(rootUrl, "sy.module");
            File moduleConfigFileDest = new File(destModuleRootPath, "sy.module");

            if(!moduleConfigFileDest.exists()) {
                URL url = new URL(moduleConfigFileOrig);
                try (InputStream in = url.openStream()) {
                    Files.copy(in, moduleConfigFileDest.toPath());
                } catch (IOException e) {
                    throw new WorkspaceException("Could not find or download module config file.", e);
                }
            }
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }

        for(String file : config.getFiles()){
            try {
                String fileOrig = classesRootPath + "/" + file;
                File fileDest = new File(destModuleFilesPath, file);

                if(fileDest.exists()) continue;

                URL url = new URL(fileOrig);

                try (InputStream in = url.openStream()) {
                    Files.copy(in, fileDest.toPath());
                }
                catch (IOException e){
                    throw new WorkspaceException("Could not find or download file [" + fileOrig + "].", e);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    // -------


    // -------
    public void removeModule(IContext ctx, String name) throws WorkspaceException {
        String filePath = modulesConfigFile.getModuleNameToUrlMap().get(name);

        if(filePath == null){
            throw new WorkspaceException("No such module installed: [" + name + "]!");
        }

        File file = new File(filePath);
        if(file.exists()){
            if(!recRemove(file)){
                throw new WorkspaceException("File path at [" + filePath + "] could not be fully removed!");
            }
        }

        Map<String, String> modulesMap = new HashMap<>(modulesConfigFile.getModuleNameToUrlMap());
        modulesMap.remove(name);

        try {
            saveModulesConfig(new ModulesConfigFile(modulesMap));
        }
        catch (IOException e){
            throw new WorkspaceException("Could not save modules config file!", e);
        }
    }

    private boolean recRemove(File f){
        boolean result = true;

        if(f.isDirectory()){
            File[] children = f.listFiles();
            if(children != null){
                for(File child : children){
                    if(!recRemove(child)){
                        result = false;
                    }
                }
            }

            if(!f.delete()) return false;
            return result;
        }
        else {
            return f.delete();
        }
    }
    // -------



    public List<String> listModules(){
        return new ArrayList<>(modulesConfigFile.getModuleNameToUrlMap().keySet());
    }
}
