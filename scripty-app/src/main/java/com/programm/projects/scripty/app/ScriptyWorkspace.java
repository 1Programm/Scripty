package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ScriptyWorkspace {

    private static final String ERR_WORKSPACE = "Workspace Folder";
    private static final String ERR_REPOS = "Repositories Config File";
    private static final String ERR_MODULES = "Modules Config File";

    private static final String FILE_SY_REPOS = "sy.repos";
    private static final String FILE_SY_MODULES = "sy.modules";
    private static final String FILE_SY_MODULE = "sy.module";
    private static final String CONTENT_DEFAULT_REPO = "https://raw.githubusercontent.com/1Programm/Scripty/master/sy.repo";

    private final IOutput log;
    private final IOutput err;

    private File workspaceFolder;

    //Config files
    private File reposFile;
    private File modulesFile;

    private final List<String> repositoryUrls = new ArrayList<>();

    public ScriptyWorkspace(IOutput log, IOutput err) {
        this.log = log;
        this.err = err;
    }

    // Setting up the Workspace
    // > This should load, create and check the [workspace-folder, sy.repos-file, sy.modules-file] if they exist

    public void setupWorkspace(String workspace) throws IOException {
        workspaceFolder = FileUtils.getCreateDir(workspace, ERR_WORKSPACE);

        reposFile = new File(workspaceFolder, FILE_SY_REPOS);
        if(!reposFile.exists()){
            createSyRepoFile();
        }

        modulesFile = new File(workspaceFolder, FILE_SY_MODULES);
        if(!modulesFile.exists()){
            createSyModulesFile();
        }

        collectRepos();

        loadAndCreateModules();
    }

    private void createSyRepoFile() throws IOException {
        log.println("[" + ERR_REPOS + "] could not be found. Creating new default ...");

        FileUtils.createFile(reposFile, ERR_REPOS);
        try {
            _createSyRepoContent();
        }
        catch (ParseException e){
            throw new IOException("Could not create [" + ERR_REPOS + "]'s content!", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void _createSyRepoContent() throws IOException, ParseException {
        JSONArray repoArray = new JSONArray();
        repoArray.add(CONTENT_DEFAULT_REPO);

        try(FileWriter writer = new FileWriter(reposFile)){
            writer.write(repoArray.toJSONString());
        }
    }

    private void createSyModulesFile() throws IOException{
        log.println("[" + ERR_MODULES + "] could not be found. Creating new default ...");

        FileUtils.createFile(modulesFile, ERR_MODULES);
        try {
            _createSyModulesContent();
        }
        catch (ParseException e){
            throw new IOException("Could not create [" + ERR_MODULES + "]'s content!", e);
        }
    }

    private void _createSyModulesContent() throws IOException, ParseException {
        JSONObject modulesObject = new JSONObject();

        try(FileWriter writer = new FileWriter(reposFile)){
            writer.write(modulesObject.toJSONString());
        }
    }

    private void collectRepos() throws IOException {
        try {
            JSONArray reposArray = (JSONArray) JSONUtils.readJsonFromFile(reposFile);

            for(Object oRepoUrl : reposArray){
                String repoUrl = oRepoUrl.toString();
                repositoryUrls.add(repoUrl);
            }
        }
        catch (ParseException e){
            throw new IOException("Could not read [" + ERR_REPOS + "]'s content!", e);
        }

        log.println("Found [" + repositoryUrls.size() + "] repository - urls to search from.");
    }

    private void loadAndCreateModules() throws IOException {
        try {
            JSONObject modulesObject = (JSONObject) JSONUtils.readJsonFromFile(modulesFile);
            for(Object oModuleName : modulesObject.keySet()){
                String moduleName = oModuleName.toString();

                Object oModuleDest = modulesObject.get(oModuleName);
                String moduleDest = oModuleDest.toString();

                _createModule(moduleName, moduleDest);
            }
        }
        catch (ParseException e){
            throw new IOException("Could not read [" + ERR_MODULES + "]'s content.", e);
        }
    }

    private void _createModule(String name, String destination) throws IOException {
        String moduleUrl = _lookupModuleName(name);

        if(moduleUrl == null){
            throw new IOException("Could not find module with name: [" + name + "].");
        }

        _copyModuleFromUrl(name, moduleUrl, destination);
    }

    private String _lookupModuleName(String name) throws IOException {
        for(String repoUrl : repositoryUrls) {
            String repoName = repoUrl;

            try {
                JSONObject oRepo = (JSONObject) JSONUtils.readJsonFromUrl(repoUrl);
                repoName = oRepo.get("name").toString() + " (" + repoUrl + ")";

                Object oModulesArray = oRepo.get("modules");

                if(!(oModulesArray instanceof JSONArray)){
                    err.println("Corrupted Repo File: [" + repoName + "]. Modules should be specified in an array!");
                    continue;
                }

                JSONArray modulesArray = (JSONArray) oModulesArray;

                for (Object oModule : modulesArray) {
                    if (!(oModule instanceof JSONObject)) {
                        err.println("Corrupted Module in repo: [" + repoName + "]: A Module should be specified in as an JSON Object!");
                        continue;
                    }

                    JSONObject module = (JSONObject) oModule;

                    Object oModuleName = module.get("name");
                    if (oModuleName == null) {
                        err.println("Corrupted Module in repo: [" + repoName + "]: No name defined!");
                        continue;
                    }

                    String moduleName = oModuleName.toString();

                    Object oModuleUrl = module.get("url");
                    if (oModuleUrl == null) {
                        err.println("Corrupted Module [" + moduleName + "] in repo: [" + repoName + "]: No url defined!");
                        continue;
                    }

                    String moduleUrl = oModuleUrl.toString();

                    if (moduleName.equals(name)) {
                        log.println("Found module [" + moduleName + "] in repository: [" + repoName + "].");
                        return moduleUrl;
                    }
                }
            } catch (ParseException e) {
                throw new IOException("Could not read [" + repoName + "].", e);
            }
        }

        return null;
    }

    private void _copyModuleFromUrl(String moduleName, String url, String destination) throws IOException {
        log.println("Downloading module [" + moduleName + "] ...");

        String moduleFileUrl = _ensureModuleUrl(url);

        JSONObject moduleFileObject;
        try{
            moduleFileObject = (JSONObject) JSONUtils.readJsonFromUrl(moduleFileUrl);
        }
        catch (ParseException e){
            throw new IOException("Could not read Module [" + moduleName + "] from url: " + moduleFileUrl);
        }

        Object oVersion = moduleFileObject.get("version");
        String version = null;

        if(oVersion == null){
            log.println("Module [" + moduleName + "] does not specify a version.");
        }
        else {
            version = oVersion.toString();
        }

        Object oAuthors = moduleFileObject.get("authors");
        JSONArray authors = null;

        if(oAuthors == null){
            log.println("Module [" + moduleName + "] does not specify authors.");
        }
        else {
            if(oVersion instanceof JSONArray) {
                authors = (JSONArray) oVersion;
            }
            else {
                err.println("Corrupted module [" + moduleName + "]. [authors] should be a List of Strings.");
            }
        }

        Object oModulePackage = moduleFileObject.get("module-package");
        String modulePackage = "";

        if(oModulePackage != null){
            modulePackage = oModulePackage.toString();
        }

        Object oModuleFiles = moduleFileObject.get("module-files");

        if(!(oModuleFiles instanceof JSONArray)){
            throw new IOException("Corrupted module [" + moduleName + "]. [module-files] should be a List of relative URLs.");
        }

        JSONArray moduleFiles = (JSONArray) oModuleFiles;
        List<String> moduleFilesList = new ArrayList<>();

        for(Object moduleFile : moduleFiles){
            String path = moduleFile.toString();
            moduleFilesList.add(path);
        }

        Object oModuleEntry = moduleFileObject.get("module-entry");

        if(oModuleEntry == null){
            throw new IOException("Corrupted module [" + moduleName + "]. [module-entry] should be a specified File.");
        }


        // --- ACTUAL DOWNLOAD ---
        log.println("Downloading [" + moduleName + "]" + (version == null ? "" : " - v" + version) + "" + (authors == null ? "" : " - " + authors.toJSONString()));

        // Copy the sy.module file
        URL syModuleFileUrl = new File(moduleFileUrl).toURI().toURL();
        String syModuleDest = _ensureUrlConcat(destination, FILE_SY_MODULE);

        try (InputStream in = syModuleFileUrl.openStream()) {
            Files.copy(in, Paths.get(syModuleDest));
        }


        // Copy the module-files

        String modulePackagePath = _packageToUrl(modulePackage);
        String baseUrl = _ensureUrlConcat(url, modulePackagePath);

        for(String moduleFilePath : moduleFilesList){
            String completeFilePath = _ensureUrlConcat(baseUrl, moduleFilePath);

            //Files can specify the type or we will assume the file should be a .class file
            completeFilePath = _ensureCorrectOrEnding(completeFilePath, ".class");

            String fileDest = _ensureUrlConcat(destination, modulePackagePath, moduleFilePath);

            URL completeFilePathUrl;
            try {
                completeFilePathUrl = new File(completeFilePath).toURI().toURL();
            }
            catch (MalformedURLException e){
                throw new IOException("Not a valid url: [" + completeFilePath + "].");
            }

            try (InputStream in = completeFilePathUrl.openStream()){
                Files.copy(in, Paths.get(fileDest));
            }
        }

        log.println("Downloaded module [" + moduleName + "].");
    }

    private String _ensureModuleUrl(String url){
        //URL can be with or without '/sy.module'
        if(url.endsWith("/")){
            return url + FILE_SY_MODULE;
        }
        else if(!url.endsWith("sy.module")){
            return url + "/" + FILE_SY_MODULE;
        }

        return url;
    }

    private String _packageToUrl(String pkg){
        return pkg.replaceAll("\\.", "/");
    }

    private String _ensureUrlConcat(String url, String... appends){
        for(String append : appends){
            url = _ensureUrlConcat(url, append);
        }

        return url;
    }

    private String _ensureUrlConcat(String url, String append){
        if(append.startsWith("/")){
            append = append.substring(1);
        }

        if(url.endsWith("/")) {
            return url + append;
        }
        else {
            return url + "/" + append;
        }
    }

    private String _ensureCorrectOrEnding(String file, String ending){
        int lastSlash = file.lastIndexOf('/');
        if(lastSlash == -1) lastSlash = 0;

        String end = file.substring(lastSlash);
        int lastDot = end.lastIndexOf('.');

        if(lastDot == -1){
            return file + ending;
        }

        return file;
    }


    // Loading and initializing modules into java
    // > Should load the defined modules into classpath and init each module

    public void loadAndInitModules() {

    }




}
