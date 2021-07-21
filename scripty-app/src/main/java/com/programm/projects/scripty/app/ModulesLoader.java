package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ModulesLoader {

    public static void loadRepos(File repoFile, List<String> repos) throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(repoFile)) {
            Object root_obj = parser.parse(reader);

            if(root_obj instanceof JSONArray){
                JSONArray root = (JSONArray)root_obj;

                for (Object o : root) {
                    String repo = o.toString();
                    repos.add(repo);
                }
            }
            else {
                throw new IOException("Repositories file is not an Array!");
            }
        }
    }

    public static void createModule(String url, File installFolder) throws IOException, ParseException {
        if(!url.endsWith("/")){
            url += "/";
        }

        String moduleFileUrl = url + "sy.module";

        JSONObject oModule = (JSONObject) readJsonFromUrl(moduleFileUrl);

        String name = oModule.get("name").toString();
        String version = oModule.get("version").toString();
        String authors = oModule.get("authors").toString();

        System.out.println("Loading [" + name + "] version " + version + " by: " + authors);

        String modulePackage = oModule.get("module-package").toString();
        JSONArray moduleClasses = (JSONArray) oModule.get("module-classes");

        String modulePackageUrl = getModulePackageUrl(modulePackage);

        List<String> urlsToCopy = new ArrayList<>();
        urlsToCopy.add("sy.module");

        for(Object oClassPath : moduleClasses){
            String classPath = oClassPath.toString();

            String classPathUrl = modulePackageUrl + classPath;
            urlsToCopy.add(classPathUrl);
        }

        copyUrlsToModuleFolder(installFolder, url, urlsToCopy);
    }

    private static void copyUrlsToModuleFolder(File moduleFolder, String baseUrl, List<String> urls) throws IOException {
        if (!moduleFolder.exists()) {
            if(!moduleFolder.mkdirs()){
                throw new IOException("Could not create module folder: " + moduleFolder.getAbsolutePath());
            }
        }

        for(String fileUrl : urls){
            int last = fileUrl.lastIndexOf('/');
            if(last == -1) last = 0;
            String part = fileUrl.substring(last);
            int lastDot = part.lastIndexOf('.');
            if(lastDot == -1){
                fileUrl += ".class";
            }

            String remoteUrl = baseUrl + fileUrl;
            URL theUrl = new URL(remoteUrl);
            String localFileName = moduleFolder.getAbsolutePath() + "/" + fileUrl;

            int lastSlash = localFileName.lastIndexOf("/");
            if(lastSlash != -1) {
                String prevFolderPath = localFileName.substring(0, lastSlash);
                File prevFolder = new File(prevFolderPath);
                if(!prevFolder.exists()){
                    if(!prevFolder.mkdirs()){
                        throw new IOException("Could not create local folder: " + prevFolderPath);
                    }
                }
            }

            try (InputStream in = theUrl.openStream()) {
                Files.copy(in, Paths.get(localFileName));
            }
        }
    }

    private static String getModulePackageUrl(String modulePackage){
        String url = modulePackage.replaceAll("\\.", "/");

        if(!url.endsWith("/")){
            url += "/";
        }

        return url;
    }

//    public static void loadModules(List<String> repos, List<Module> modules) throws IOException, ParseException {
//        for(String repo : repos){
//            JSONObject root = (JSONObject) readJsonFromUrl(repo);
//
//            System.out.println("Loading Repository: " + root.get("name"));
//            JSONArray rModules = (JSONArray) root.get("modules");
//
//            for(Object o : rModules){
//                String rModule = o.toString();
//                String folder;
//
//                System.out.println("Loading: " + rModule + " ...");
//
//                if(rModule.endsWith("sy.module")){
//                    folder = rModule.substring(0, rModule.length() - 9);
//                }
//                else if(rModule.endsWith("/")){
//                    folder = rModule;
//                    rModule += "sy.module";
//                }
//                else {
//                    folder = rModule + "/";
//                    rModule += "/sy.module";
//                }
//
//                JSONObject mroot = (JSONObject) readJsonFromUrl(rModule);
//                System.out.println("Loaded module: [" + mroot.get("name") + "]");
//            }
//        }
//    }

    public static Object readJsonFromUrl(String url) throws IOException, ParseException {
        URL theUrl = new URL(url);
        URLConnection connection = theUrl.openConnection();
        InputStream in = connection.getInputStream();

        return readJson(in);
    }

    public static Object readJsonFromPath(String path) throws IOException, ParseException {
        return readJson(new FileInputStream(path));
    }

    public static Object readJsonFromFile(File file) throws IOException, ParseException {
        return readJson(new FileInputStream(file));
    }

    private static Object readJson(InputStream in) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        return parser.parse(new InputStreamReader(in));
    }

}
