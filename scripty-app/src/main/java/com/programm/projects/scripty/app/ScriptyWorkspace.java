package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.SyIO;
import com.programm.projects.scripty.modules.api.SyModuleConfig;
import com.programm.projects.scripty.modules.api.SyWorkspace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ScriptyWorkspace implements SyWorkspace {

    private static final String ERR_WORKSPACE = "Workspace Folder";
    private static final String ERR_REPOS = "Repositories Config File";
    private static final String ERR_MODULES = "Modules Config File";

    private static final String FILE_SY_REPOS = "sy.repos";
    private static final String FILE_SY_MODULES = "sy.modules";
    private static final String FILE_SY_MODULE = "sy.module";
    private static final String CONTENT_DEFAULT_REPO = "https://raw.githubusercontent.com/1Programm/Scripty/master/sy.repo";

    private final SyIO io;

    private File workspaceFolder;
    private String userPath;

    //Config files
    private File reposFile;
    private File modulesFile;

    private final List<String> repositoryUrls = new ArrayList<>();

    public ScriptyWorkspace(SyIO io) {
        this.io = io;
    }

    // Setting up the Workspace
    // > This should load, create and check the [workspace-folder, sy.repos-file, sy.modules-file] if they exist

    public void setupWorkspace(String workspace, String userPath) throws IOException {
        workspaceFolder = FileUtils.getCreateDir(workspace, ERR_WORKSPACE);
        this.userPath = userPath;

        reposFile = new File(workspaceFolder, FILE_SY_REPOS);
        if(!reposFile.exists()){
            _createSyRepoFile();
        }

        modulesFile = new File(workspaceFolder, FILE_SY_MODULES);
        if(!modulesFile.exists()){
            _createSyModulesFile();
        }

        _collectRepos();

        _loadAndCreateModules();
    }

    private void _createSyRepoFile() throws IOException {
        io.log().println("[" + ERR_REPOS + "] could not be found. Creating new default ...");

        FileUtils.createFile(reposFile, ERR_REPOS);
        try {
            _createSyRepoContent();
        }
        catch (ParseException e){
            throw new IOException("Could not create [" + ERR_REPOS + "]'s content!", e);
        }
    }

    private void _createSyRepoContent() throws IOException, ParseException {
        try(FileWriter writer = new FileWriter(reposFile)){
            writer.write("[\n   \"" + CONTENT_DEFAULT_REPO + "\"\n]");
        }
    }

    private void _createSyModulesFile() throws IOException{
        io.log().println("[" + ERR_MODULES + "] could not be found. Creating new default ...");

        FileUtils.createFile(modulesFile, ERR_MODULES);
        try {
            _createSyModulesContent();
        }
        catch (ParseException e){
            throw new IOException("Could not create [" + ERR_MODULES + "]'s content!", e);
        }
    }

    private void _createSyModulesContent() throws IOException, ParseException {
        try(FileWriter writer = new FileWriter(modulesFile)){
            writer.write("{\n\n}");
        }
    }

    private void _collectRepos() throws IOException {
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

        io.log().println("Found [" + repositoryUrls.size() + "] repository - urls to search from.");
    }

    private void _loadAndCreateModules() throws IOException {
        try {
            JSONObject modulesObject = (JSONObject) JSONUtils.readJsonFromFile(modulesFile);
            for(Object oModuleName : modulesObject.keySet()){
                String moduleName = oModuleName.toString();
                io.log().println("Checking Module [" + moduleName + "] ...");

                Object oModuleDest = modulesObject.get(oModuleName);
                String moduleDest = oModuleDest.toString();

                if(_checkModuleExists(moduleDest)){
                    continue;
                }


                io.log().println("Module not installed yet. Downloading it.");
                _createModule(moduleName, moduleDest);
            }
        }
        catch (ParseException e){
            throw new IOException("Could not read [" + ERR_MODULES + "]'s content.", e);
        }
    }

    private boolean _checkModuleExists(String path){
        File moduleFolder = new File(path);

        //TODO: More in depth test if module is properly installed
        return moduleFolder.exists();
    }

    private void _createModule(String name, String destination) throws IOException {
        String moduleUrl = _lookupModuleName(name);

        if(moduleUrl == null){
            throw new IOException("Could not find module with name: [" + name + "].");
        }

        _copyModuleFromUrl(name, moduleUrl, destination, false);
    }

    private String _lookupModuleName(String name) throws IOException {
        for(String repoUrl : repositoryUrls) {
            String repoName = repoUrl;

            try {
                JSONObject oRepo = (JSONObject) JSONUtils.readJsonFromUrl(repoUrl);
                repoName = oRepo.get("name").toString() + " (" + repoUrl + ")";

                Object oModulesArray = oRepo.get("modules");

                if(!(oModulesArray instanceof JSONArray)){
                    io.err().println("Corrupted Repo File: [" + repoName + "]. Modules should be specified in an array!");
                    continue;
                }

                JSONArray modulesArray = (JSONArray) oModulesArray;

                for (Object oModule : modulesArray) {
                    if (!(oModule instanceof JSONObject)) {
                        io.err().println("Corrupted Module in repo: [" + repoName + "]: A Module should be specified in as an JSON Object!");
                        continue;
                    }

                    JSONObject module = (JSONObject) oModule;

                    Object oModuleName = module.get("name");
                    if (oModuleName == null) {
                        io.err().println("Corrupted Module in repo: [" + repoName + "]: No name defined!");
                        continue;
                    }

                    String moduleName = oModuleName.toString();

                    Object oModuleUrl = module.get("url");
                    if (oModuleUrl == null) {
                        io.err().println("Corrupted Module [" + moduleName + "] in repo: [" + repoName + "]: No url defined!");
                        continue;
                    }

                    String moduleUrl = oModuleUrl.toString();

                    if (moduleName.equals(name)) {
                        io.log().println("Found module [" + moduleName + "] in repository: [" + repoName + "].");
                        return moduleUrl;
                    }
                }
            } catch (ParseException e) {
                throw new IOException("Could not read [" + repoName + "].", e);
            }
        }

        return null;
    }

    private void _copyModuleFromUrl(String moduleName, String url, String destination, boolean silent) throws IOException {
        try {
            String moduleFileUrl = _ensureModuleUrl(url);

            ScriptyModuleConfig config;
            try {
                config = readModuleConfig(moduleFileUrl);
            } catch (ParseException e) {
                throw new IOException("Could not read Module [" + moduleName + "] from url: " + moduleFileUrl);
            }


            // --- ACTUAL DOWNLOAD ---
            (silent ? io.log() : io.out()).println("Downloading [" + moduleName + "], version: " + config.version() + ", by " + config.authors());

            // Copy the sy.module file
            String syModuleDest = _ensureUrlConcat(destination, FILE_SY_MODULE);
            _ensureFoldersForFile(syModuleDest);

            String actualRootFolder = config.rootFolder;
            config.rootFolder = "";
            writeModuleConfigFile(config, new File(syModuleDest));

            String modulePackagePath = _packageToUrl(config.rootPackage());
            String baseUrl = _ensureUrlConcat(url, actualRootFolder, modulePackagePath);

            for (String moduleFilePath : config.files()) {
                String completeFilePath = _ensureUrlConcat(baseUrl, moduleFilePath);

                String fileDest = _ensureUrlConcat(destination, modulePackagePath, moduleFilePath);
                _ensureFoldersForFile(fileDest);

                URL completeFilePathUrl;
                try {
                    completeFilePathUrl = new URL(completeFilePath);
                } catch (MalformedURLException e) {
                    throw new IOException("Not a valid url: [" + completeFilePath + "].");
                }

                (silent ? io.log() : io.out()).println("Downloading [" + completeFilePath + "] ...");

                try (InputStream in = completeFilePathUrl.openStream()) {
                    Files.copy(in, Paths.get(fileDest));
                }
                catch (IOException e){
                    throw new IOException("Could not find or download file [" + completeFilePath + "].");
                }
            }
        }
        catch (IOException e){
            removeModuleFiles(destination);
            throw e;
        }
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

    private void _ensureFoldersForFile(String path) throws IOException {
        int lastSlash = path.lastIndexOf('/');
        if(lastSlash == -1 || lastSlash == 0) return;

        String folderPath = path.substring(0, lastSlash);
        File folder = new File(folderPath);

        if(!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IOException("Could not create all folders for file: [" + path + "].");
            }
        }
    }






    // Loading and initializing modules into java
    // > Should load the defined modules into classpath and init each module

    public void loadModules(ScriptyModulesManager modulesManager) throws IOException {
        Map<String, String> modulesMap = listModules();

        URL[] urls = new URL[modulesMap.size()];
        List<String> entryPoints = new ArrayList<>();
        Map<String, SyModuleConfig> moduleConfigs = new HashMap<>();

        int i=0;
        for(String moduleName : modulesMap.keySet()){
            String moduleDest = modulesMap.get(moduleName);

            urls[i] = _tryCleanOrFileURL(moduleDest);

            try {
                String moduleConfigFilePath = _ensureUrlConcat(moduleDest, FILE_SY_MODULE);
                SyModuleConfig config = readModuleConfig(moduleConfigFilePath);

                String moduleEntryFile = config.moduleEntry();

                //IT SHOULD
                if(moduleEntryFile.endsWith(".class")){
                    moduleEntryFile = moduleEntryFile.substring(0, moduleEntryFile.length() - 6);
                }

                String entryClassPath = config.rootPackage() + "." + moduleEntryFile;
                entryPoints.add(entryClassPath);
                moduleConfigs.put(entryClassPath, config);
            }
            catch (ParseException e){
                io.err().println("Could not read Module [" + moduleName + "]: " + e.getMessage());
            }

            i++;
        }

        modulesManager.loadModules(urls, entryPoints, moduleConfigs);
    }

    public Module loadSingleModule(ScriptyModulesManager modulesManager, String moduleName) throws IOException {
        Map<String, String> modulesMap = listModules();

        if(!modulesMap.containsKey(moduleName)){
            throw new IOException("Module [" + moduleName + "] is not an installed module.");
        }

        String moduleDest = modulesMap.get(moduleName);

        try {
            String moduleConfigFilePath = _ensureUrlConcat(moduleDest, FILE_SY_MODULE);
            SyModuleConfig config = readModuleConfig(moduleConfigFilePath);

            String rootFolder = config.rootFolder();
            String moduleEntryFile = config.moduleEntry();

            //IT SHOULD
            if(moduleEntryFile.endsWith(".class")){
                moduleEntryFile = moduleEntryFile.substring(0, moduleEntryFile.length() - 6);
            }

            String entryClassPath = config.rootPackage() + "." + moduleEntryFile;

            String moduleRootClassPath = _ensureUrlConcat(moduleDest, rootFolder);
            URL moduleClassPathUrl = _tryCleanOrFileURL(moduleRootClassPath);

            Module module = modulesManager.loadSingleModule(moduleClassPathUrl, entryClassPath);

            if(module == null){
                throw new IOException("Module could not be created from entry class: [" + entryClassPath + "]!");
            }

            return module;
        }
        catch (ClassNotFoundException e){
            throw new IOException("Could not load module [" + moduleName + "]: " + e.getMessage());
        }
        catch (ParseException e){
            throw new IOException("Could not read module [" + moduleName + "]: " + e.getMessage());
        }
    }

    public ScriptyModuleConfig readModuleConfig(String path) throws IOException, ParseException{
        URL url = _tryCleanOrFileURL(path);
        Object oModuleObject = JSONUtils.readJsonFromUrl(url);

        if(!(oModuleObject instanceof JSONObject)){
            throw new IOException("Module at [" + path + "] is corrupted. It should be an JSONObject.");
        }

        JSONObject moduleFileObject = (JSONObject) oModuleObject;

        Object oName = moduleFileObject.get("name");

        if(oName == null){
            throw new IOException("Module at [" + path + "] is corrupted. It should specify a [name]");
        }

        String name = oName.toString();

        Object oVersion = moduleFileObject.get("version");
        String version = null;

        if (oVersion == null) {
            io.log().println("Module [" + name + "] does not specify a version.");
        } else {
            version = oVersion.toString();
        }

        Object oAuthors = moduleFileObject.get("authors");
        JSONArray authors = null;

        if (oAuthors == null) {
            io.log().println("Module [" + name + "] does not specify authors.");
        } else {
            if (oAuthors instanceof JSONArray) {
                authors = (JSONArray) oAuthors;
            } else {
                throw new IOException("Corrupted module [" + name + "]. [authors] should be a List of Strings.");
            }
        }

        List<String> authorList = null;

        if(authors != null) {
            authorList = new ArrayList<>();
            for (Object author : authors){
                authorList.add(author.toString());
            }
        }

        Object oModuleFolder = moduleFileObject.get("root-folder");
        String moduleFolder = "";

        if(oModuleFolder != null){
            moduleFolder = oModuleFolder.toString();
        }

        Object oModulePackage = moduleFileObject.get("root-package");
        String modulePackage = "";

        if (oModulePackage != null) {
            modulePackage = oModulePackage.toString();
        }

        Object oModuleFiles = moduleFileObject.get("files");

        if (!(oModuleFiles instanceof JSONArray)) {
            throw new IOException("Corrupted module [" + name + "]. [files] should be a List of relative URLs.");
        }

        JSONArray moduleFiles = (JSONArray) oModuleFiles;
        List<String> moduleFilesList = new ArrayList<>();

        for (Object moduleFile : moduleFiles) {
            String mPath = moduleFile.toString();
            moduleFilesList.add(mPath);
        }

        Object oModuleEntry = moduleFileObject.get("module-entry");

        if (oModuleEntry == null) {
            throw new IOException("Corrupted module [" + name + "]. [module-entry] should be a specified File.");
        }

        String moduleEntry = oModuleEntry.toString();

        return new ScriptyModuleConfig(name, version, authorList, moduleFolder, modulePackage, moduleFilesList, moduleEntry);
    }

    private URL _tryCleanOrFileURL(String modulePath) throws IOException {
        try {
            return new URL(modulePath);
        }
        catch (MalformedURLException e){
            return new File(modulePath).toURI().toURL();
        }
    }





    @SuppressWarnings("unchecked")
    private void writeToRepositoryFile(List<String> repositoryUrls) throws IOException{
        JSONArray repositories = new JSONArray();
        repositories.addAll(repositoryUrls);

        try (Writer writer = new FileWriter(reposFile)){
            repositories.writeJSONString(writer);
        }
    }

    public void addRepository(String repositoryUrl) throws IOException {
        List<String> repositories = listRepositories();

        if(repositories.contains(repositoryUrl)){
            throw new IOException("Repository [" + repositoryUrl + "] is already added!");
        }

        repositories.add(repositoryUrl);
        writeToRepositoryFile(repositories);
    }

    public void removeRepository(String repositoryUrl) throws IOException {
        List<String> repositories = listRepositories();

        if(!repositories.contains(repositoryUrl)){
            throw new IOException("Repository [" + repositoryUrl + "] does not exist!");
        }

        repositories.remove(repositoryUrl);
        writeToRepositoryFile(repositories);
    }

    public List<String> listRepositories() throws IOException {
        try {
            Object oRepositories = JSONUtils.readJsonFromFile(reposFile);

            if(!(oRepositories instanceof JSONArray)){
                throw new IOException("Corrupted File: [" + ERR_REPOS + "]: ");
            }

            JSONArray repositories = (JSONArray) oRepositories;

            List<String> repositoryList = new ArrayList<>();

            for(Object oRepo : repositories){
                repositoryList.add(oRepo.toString());
            }

            return repositoryList;
        }
        catch (ParseException e){
            throw new IOException("Corrupted File: [" + ERR_REPOS + "]: ", e);
        }
    }










    // Add module to sy.modules file

    @SuppressWarnings("unchecked")
    public void addModule(String name, String destination, boolean silent) throws IOException{
        String url = _lookupModuleName(name);

        if(url == null){
            io.err().println("Could not find a Module with name [" + name + "] in the specified repositories.");
            return;
        }

        io.log().println("Adding Module [" + name + "] to workspace at [" + destination + "] ...");
        try {
            JSONObject modulesObject = (JSONObject) JSONUtils.readJsonFromFile(modulesFile);

            if(modulesObject.containsKey(name)){
                (silent ? io.log() : io.out()).println("Module [" + name + "] already exists.");
                return;
            }

            modulesObject.put(name, destination);

            String content = modulesObject.toJSONString();
            writeToFile(modulesFile, content);
            io.log().println("Updated [" + ERR_MODULES + "].");

            _copyModuleFromUrl(name, url, destination, silent);
        }
        catch (ParseException e){
            throw new IOException("Corrupted File: [" + ERR_MODULES + "]: ", e);
        }

        (silent ? io.log() : io.out()).println("Added Module [" + name + "] at [" + destination + "].");
    }

    public void removeModule(String name) throws IOException{
        io.log().println("Removing Module [" + name + "] ...");
        try {
            JSONObject modulesObject = (JSONObject) JSONUtils.readJsonFromFile(modulesFile);

            if(!modulesObject.containsKey(name)){
                io.out().println("No such module [" + name + "] installed.");
                return;
            }

            String destination = modulesObject.remove(name).toString();

            String content = modulesObject.toJSONString();
            writeToFile(modulesFile, content);
            io.log().println("Updated [" + ERR_MODULES + "].");


            removeModuleFiles(destination);
        }
        catch (ParseException e){
            throw new IOException("Corrupted File: [" + ERR_MODULES + "]: ", e);
        }

        io.log().println("Removed Module [" + name + "].");
    }

    public void updateModule(String moduleName) throws IOException {
        Map<String, String> modulesMap = listModules();

        if(!modulesMap.containsKey(moduleName)){
            throw new IOException("Module [" + moduleName + "] is not an installed module.");
        }

        String moduleDest = modulesMap.get(moduleName);
        SyModuleConfig config;

        try {
            String moduleConfigFilePath = _ensureUrlConcat(moduleDest, FILE_SY_MODULE);
            config = readModuleConfig(moduleConfigFilePath);

        }
        catch (ParseException e){
            throw new IOException("Could not read module [" + moduleName + "]: " + e.getMessage());
        }

        String installedVersion = config.version();

        String url = _lookupModuleName(moduleName);

        if(url == null){
            io.err().println("Could not find a Module with name [" + moduleName + "] in the specified repositories.");
            return;
        }

        String moduleFileUrl = _ensureModuleUrl(url);

        JSONObject moduleFileObject;
        try {
            moduleFileObject = (JSONObject) JSONUtils.readJsonFromUrl(moduleFileUrl);
        } catch (ParseException e) {
            throw new IOException("Could not read Module [" + moduleName + "] from url: " + moduleFileUrl);
        }

        Object oVersion = moduleFileObject.get("version");
        String version;

        if (oVersion == null) {
            throw new IOException("Module [" + moduleName + "] does not specify a version.");
        } else {
            version = oVersion.toString();
        }

        if(version.equals(installedVersion)){
            io.out().println("Module [" + moduleName + "] already up to date (version " + version + ")");
            return;
        }

        io.out().println("Updating [" + moduleName + "] ...");
        removeModule(moduleName);
        addModule(moduleName, moduleDest, true);
    }

    public Map<String, String> listModules() throws IOException {
        try {
            JSONObject modulesObject = (JSONObject) JSONUtils.readJsonFromFile(modulesFile);

            Map<String, String> modulesMap = new HashMap<>();

            for(Object oKey : modulesObject.keySet()){
                String key = oKey.toString();
                String value = modulesObject.get(oKey).toString();

                modulesMap.put(key, value);
            }

            return modulesMap;
        }
        catch (ParseException e){
            throw new IOException("Corrupted File: [" + ERR_MODULES + "]: ", e);
        }
    }

    private void writeToFile(File file, String content) throws IOException {
        try(FileWriter writer = new FileWriter(file)){
            writer.write(content);
        }
    }










    private void removeModuleFiles(String destination){
        File destFolder = new File(destination);
        if(destFolder.exists()){
            try {
                recRemoveFile(destFolder);

                if(!destFolder.delete()){
                    io.err().println("Could not delete unfinished installed moduel at: " + destination);
                }
            }
            catch (IOException e){
                io.err().println(e.getMessage());
            }
        }
    }

    private void recRemoveFile(File file) throws IOException{
        if(!file.exists() || file.isFile()) return;

        File[] children = file.listFiles();
        if(children != null){
            for(File child : children){
                recRemoveFile(child);
                if(!child.delete()){
                    throw new IOException("Could not delete file: [" + child.getAbsolutePath() + "].");
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void writeModuleConfigFile(SyModuleConfig config, File file) throws IOException{
        JSONObject moduleObject = new JSONObject();
        moduleObject.put("name", config.name());
        moduleObject.put("version", config.version());

        JSONArray authorsArray = new JSONArray();
        authorsArray.addAll(config.authors());

        moduleObject.put("authors", authorsArray);
        moduleObject.put("root-folder", config.rootFolder());
        moduleObject.put("root-package", config.rootPackage());

        JSONArray filesArray = new JSONArray();
        filesArray.addAll(config.files());

        moduleObject.put("files", filesArray);
        moduleObject.put("module-entry", config.moduleEntry());


        try (Writer writer = new FileWriter(file)){
            moduleObject.writeJSONString(writer);
        }
    }




    @Override
    public String workspacePath(){
        return workspaceFolder.getAbsolutePath();
    }

    @Override
    public String userPath() {
        return userPath;
    }
}
