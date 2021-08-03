package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;
import com.programm.projects.scripty.modules.api.SyModuleConfig;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdModulesDev implements SySysCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        ScriptyCoreContext context = (ScriptyCoreContext) ctx;

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
            String moduleFolder;
            String modulePackage;
            List<String> moduleFiles = new ArrayList<>();
            String moduleEntry;

            io.out().newLine();
            io.out().println("--- Creating new Module (interactive) ---");
            io.out().println("(Hitting only 'Enter' at questions will use [defaults]");
            io.out().newLine();

            String defaultName = getDefaultName(userPathFolder.getName());
            io.out().print("What name should your module have? [" + defaultName + "]: ");
            String a_name = io.in().next();

            if(a_name.isBlank()){
                name = defaultName;
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
                io.out().println(">");
            }

            io.out().newLine();
            io.out().println("What is the starting point - folder for your module (Root folder)?");
            moduleFolder = getModuleFolder(io, userPathFolder);


            io.out().newLine();
            io.out().println("What is the root package (The package under which all classes are found)?");
            modulePackage = getModulePackage(io, userPathFolder, moduleFolder);


            io.out().newLine();
            String modulePackagePath = modulePackage.replace('.', '/');
            File rootModulePackageFolder = new File(new File(userPathFolder, moduleFolder), modulePackagePath);
            if(containsClasses(rootModulePackageFolder)){
                io.out().print("Do you want to add all classes inside root package (" + rootModulePackageFolder.getAbsolutePath() + ")? [Y/n]: ");
                boolean a_addClasses = IOUtils.getYNAnswer(io.in(), true);
                if(a_addClasses) {
                    addClasses(rootModulePackageFolder, moduleFiles, "");

                    for(String moduleFile : moduleFiles) {
                        io.out().println("> " + moduleFile);
                    }
                }
            }
            else {
                io.out().println("No classes found.");
            }


            io.out().newLine();
            io.out().println("What should the entry - point - class be for your module?");
            moduleEntry = getModuleEntry(io, moduleFiles);


            SyModuleConfig config = new ScriptyModuleConfig(name, version, authors, moduleFolder, modulePackage, moduleFiles, moduleEntry);

            try {
                context.workspace.writeModuleConfigFile(config, devModuleFile);
            }
            catch (IOException e){
                throw new CommandExecutionException("Exception writing module file: " + e.getMessage());
            }
        }
        else {
            ScriptyModuleConfig config;
            try {
                config = context.workspace.readModuleConfig(devModuleFile.getAbsolutePath());
            } catch (IOException | ParseException e) {
                throw new CommandExecutionException(e.getMessage());
            }

            String moduleFolder = config.rootFolder();
            String modulePackage = config.rootPackage();
            String modulePackagePath = modulePackage.replace('.', '/');

            File rootModulePackageFolder = new File(new File(userPathFolder, moduleFolder), modulePackagePath);
            List<String> moduleFiles = new ArrayList<>();
            addClasses(rootModulePackageFolder, moduleFiles, "");

            List<String> currentModuleFiles = new ArrayList<>(config.files());


            boolean shouldUpdateFiles = false;

            for(String moduleFile : moduleFiles){
                int index = currentModuleFiles.indexOf(moduleFile);
                if(index != -1){
                    currentModuleFiles.remove(index);
                }
                else {
                    shouldUpdateFiles = true;
                    break;
                }
            }

            if(!shouldUpdateFiles) {
                shouldUpdateFiles = currentModuleFiles.size() > 0;
            }

            if(shouldUpdateFiles){
                io.out().print("Detected changed (added / removed) files. Do you want to update the files? [Y/n]: ");
                boolean a_update = IOUtils.getYNAnswer(io.in(), true);

                if(a_update){
                    config.files().clear();
                    config.files().addAll(moduleFiles);

                    try {
                        context.workspace.writeModuleConfigFile(config, devModuleFile);
                        io.out().println("Updated your files!");
                        return;
                    } catch (IOException e) {
                        throw new CommandExecutionException(e.getMessage(), e);
                    }
                }

                io.out().newLine();
            }

            io.out().print("Do you want to make changes to your module - config? [Y/n]: ");
            boolean a_changes = IOUtils.getYNAnswer(io.in(), true);

            if(a_changes){
                while(true){
                    io.out().println("[1] | name: " + config.name());
                    io.out().println("[2] | version: " + config.version());
                    io.out().println("[3] | authors: " + config.authors());
                    io.out().println("[4] | root-folder: " + config.rootFolder());
                    io.out().println("[5] | root-package: " + config.rootPackage());
                    io.out().println("[6] | module-entry: " + config.moduleEntry());
                    io.out().newLine();

                    io.out().print("What do you want to change (1 - 6)? [Enter to exit]: ");
                    String answer = io.in().next();

                    if(answer.isBlank()){
                        break;
                    }
                    else {
                        try {
                            int i_answer = Integer.parseInt(answer);

                            if(i_answer < 1 || i_answer > 6){
                                throw new NumberFormatException();
                            }

                            switch(i_answer){
                                case 1:
                                    config.name = IOUtils.areYouSureAnswer(io, "name: ");
                                    io.out().println("( Remember that changing the name of the module does not change the name in the repo file with which this module can be found! )");
                                    break;
                                case 2:
                                    config.version = IOUtils.areYouSureAnswer(io, "version: ");
                                    break;
                                case 3:
                                    String a_authors = IOUtils.answerMatches(io, "authors (Comma seperated list): ", "", null);
                                    config.authors = Arrays.stream(a_authors.split(",")).map(String::trim).collect(Collectors.toList());
                                    break;
                                case 4:
                                    config.rootFolder = IOUtils.areYouSureAnswer(io, "root-folder: ");
                                    break;
                                case 5:
                                    config.rootPackage = IOUtils.areYouSureAnswer(io, "root-package: ");
                                    break;
                                case 6:
                                    config.moduleEntry = IOUtils.areYouSureAnswer(io, "module-entry: ");
                                    break;
                            }

                            try {
                                context.workspace.writeModuleConfigFile(config, devModuleFile);
                            }
                            catch (IOException e){
                                throw new CommandExecutionException("Exception writing module file: " + e.getMessage());
                            }
                        }
                        catch (NumberFormatException e){
                            io.out().println("'" + answer + "' is not a number between 1 - 6!");
                        }
                    }
                }
            }
        }
    }

    private String getDefaultName(String folderName){
        StringBuilder sb = new StringBuilder();

        boolean newWord = false;

        for(int i=0;i<folderName.length();i++){
            char c = folderName.charAt(i);

            if(Character.isUpperCase(c)){
                if(!newWord) {
                    if (sb.length() != 0) {
                        sb.append("-");
                    }
                    newWord = true;
                }

                sb.append(Character.toLowerCase(c));
            }
            else {
                newWord = false;
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private void addClasses(File rootFolder, List<String> classFiles, String curPath){
        File[] children = rootFolder.listFiles();

        if(children != null){
            for(File child : children){
                String cName = child.getName();
                if(child.isFile() && cName.endsWith(".class")){
                    classFiles.add(curPath + cName);
                }
                else if(child.isDirectory()){
                    addClasses(child, classFiles, curPath + cName + "/");
                }
            }
        }
    }

    private String getModuleFolder(SyIO io, File userPath){
        List<String> moduleFolderProposals = getModuleFolderProposals(userPath);

        if(moduleFolderProposals.size() == 0){
            io.out().print("No fitting folders found. Please specify a root folder. [No root folder]: ");
        }
        else if(moduleFolderProposals.size() == 1){
            io.out().print("> " + moduleFolderProposals.get(0) + "? [Y/n]: ");
            boolean a_moduleFolder = IOUtils.getYNAnswer(io.in(), true);

            if(a_moduleFolder){
                return moduleFolderProposals.get(0);
            }
            else {
                io.out().print("What should the root folder be? [No root folder]: ");
            }
        }
        else {
            while(true) {
                for (int i = 0; i < moduleFolderProposals.size(); i++) {
                    io.out().println((i + 1) + ": " + moduleFolderProposals.get(i));
                }

                io.out().print("What should the root folder be? (1 - " + moduleFolderProposals.size() + "). [Enter for custom root folder]: ");
                String a_moduleFolder = io.in().next();

                if (a_moduleFolder.isBlank()){
                    io.out().print("What should the root folder be? [No root folder]: ");
                    break;
                }
                else {
                    try {
                        int i_a_moduleFolder = Integer.parseInt(a_moduleFolder);
                        if(i_a_moduleFolder < 1 || i_a_moduleFolder > moduleFolderProposals.size()){
                            throw new NumberFormatException();
                        }

                        String picked = moduleFolderProposals.get(i_a_moduleFolder - 1);
                        io.out().println("> " + picked);
                        return picked;
                    }
                    catch (NumberFormatException e){
                        io.out().println("'" + a_moduleFolder + "' is not a number between 1 - " + moduleFolderProposals.size() + "!");
                    }
                }
            }
        }

        String moduleFolder = "";
        boolean found = false;

        while(!found) {
            String a_moduleFolder = io.in().next();

            if (a_moduleFolder.isBlank()) {
                moduleFolder = "";
                io.out().println("> " + userPath.getAbsolutePath());
                found = true;
            } else {
                File f_moduleFolder = new File(userPath, a_moduleFolder);
                io.out().print("> " + f_moduleFolder.getAbsolutePath() + "? [Y/n]: ");
                boolean a_y_moduleFolder = IOUtils.getYNAnswer(io.in(), true);

                if(a_y_moduleFolder){
                    moduleFolder = a_moduleFolder;
                    found = true;
                }
                else {
                    io.out().print("What should the root folder be? [No root folder]: ");
                }
            }
        }

        return moduleFolder;
    }

    private List<String> getModuleFolderProposals(File userFolder){
        List<String> proposals = new ArrayList<>();
        File[] children = userFolder.listFiles();

        if(children != null){
            for(File child : children){

                //Check java - maven - project structure
                if(child.getName().equals("target")){
                    File[] targetChildren = child.listFiles();

                    if(targetChildren != null){
                        for(File targetChild : targetChildren){
                            if(targetChild.getName().equals("classes")){
                                proposals.add("target/classes");
                                break;
                            }
                        }
                    }
                }

                //Find folders where .class files are in
                if(containsClasses(child)){
                    proposals.add(child.getName());
                }
            }
        }



        return proposals;
    }

    private String getModulePackage(SyIO io, File userPath, String moduleFolderPath){
        File moduleFolder = userPath;

        if(!moduleFolderPath.isBlank()){
            moduleFolder = new File(userPath, moduleFolderPath);
        }

        List<String> packageProposals = getModulePackageProposals(moduleFolder);

        if(packageProposals.size() == 0){
            io.out().print("No fitting package found. Please specify a root package. [No root package]: ");
        }
        else if(packageProposals.size() == 1){
            io.out().print("> " + packageProposals.get(0) + "? [Y/n]: ");
            boolean a_modulePackage = IOUtils.getYNAnswer(io.in(), true);

            if(a_modulePackage){
                return packageProposals.get(0);
            }
            else {
                io.out().print("What should the root package be? [No root package]: ");
            }
        }
        else {
            while(true) {
                for (int i = 0; i < packageProposals.size(); i++) {
                    io.out().println((i + 1) + ": " + packageProposals.get(i));
                }

                io.out().print("What should the root package be? (1 - " + packageProposals.size() + "). [Enter for custom root package]: ");
                String a_modulePackage = io.in().next();

                if (a_modulePackage.isBlank()){
                    io.out().print("What should the root package be? [No root package]: ");
                    break;
                }
                else {
                    try {
                        int i_a_modulePackage = Integer.parseInt(a_modulePackage);
                        if(i_a_modulePackage < 1 || i_a_modulePackage > packageProposals.size()){
                            throw new NumberFormatException();
                        }

                        String picked = packageProposals.get(i_a_modulePackage - 1);
                        io.out().println("> " + picked);
                        return picked;
                    }
                    catch (NumberFormatException e){
                        io.out().println("'" + a_modulePackage + "' is not a number between 1 - " + packageProposals.size() + "!");
                    }
                }
            }
        }

        String modulePackage = "";
        boolean found = false;

        while(!found) {
            String a_modulePackage = io.in().next();

            if (a_modulePackage.isBlank()) {
                modulePackage = "";
                io.out().println("> ");
                found = true;
            } else {
                if(a_modulePackage.contains("/")){
                    io.out().println("Invaild package. A package should be separated by [.]!");
                    io.out().print("What should the root package be? [No root package]: ");
                    continue;
                }

                io.out().print("> " + a_modulePackage + "? [Y/n]: ");
                boolean a_y_modulePackage = IOUtils.getYNAnswer(io.in(), true);

                if(a_y_modulePackage){
                    modulePackage = a_modulePackage;
                    found = true;
                }
                else {
                    io.out().print("What should the root package be? [No root package]: ");
                }
            }
        }

        return modulePackage;
    }

    private List<String> getModulePackageProposals(File rootFolder){
        List<String> proposals = new ArrayList<>();

        File rootPackageCandidate = findRootPackage(rootFolder);
        if(rootPackageCandidate != null){
            String rootPath = rootFolder.getAbsolutePath();
            String path = rootPackageCandidate.getAbsolutePath();
            path = path.substring(rootPath.length());
            if(path.startsWith("/")) path = path.substring(1);
            path = path.replace('/', '.');
            proposals.add(path);
        }

        return proposals;
    }

    private File findRootPackage(File folder){
        File[] children = folder.listFiles();
        List<File> childFolders = new ArrayList<>();

        if(children != null){
            for(File child : children){
                if(child.isFile() && child.getName().endsWith(".class")){
                    return folder;
                }
                else if(child.isDirectory()){
                    childFolders.add(child);
                }
            }

            File aRootCandidate = null;
            for(File childFolder : childFolders){
                if(aRootCandidate == null){
                    aRootCandidate = findRootPackage(childFolder);
                }
                else {
                    File anotherCandidate = findRootPackage(childFolder);

                    if(anotherCandidate != null){
                        //this folder contains 2 other folders which contains classes so this folder is the root - package - folder
                        return folder;
                    }
                }
            }

            return aRootCandidate;
        }

        return null;
    }

    private boolean containsClasses(File file){
        if(file.isFile()){
            String name = file.getName();
            int lastDot = name.lastIndexOf('.');

            if(lastDot == -1) return false;
            return name.substring(lastDot + 1).equals("class");
        }

        if(file.isDirectory()){
            File[] children = file.listFiles();

            if(children != null){
                for(File child : children){
                    if(containsClasses(child)){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private String getModuleEntry(SyIO io, List<String> classFiles){
        List<String> entryProposals = getModuleEntryProposals(classFiles);

        if(entryProposals.size() == 0){
            io.out().print("No fitting entry found. Please specify an entry - class: ");
        }
        else if(entryProposals.size() == 1){
            io.out().print("> " + entryProposals.get(0) + "? [Y/n]: ");
            boolean a_moduleEntry = IOUtils.getYNAnswer(io.in(), true);

            if(a_moduleEntry){
                return entryProposals.get(0);
            }
            else {
                io.out().print("Please specify an entry - class: ");
            }
        }
        else {
            while(true) {
                for (int i = 0; i < entryProposals.size(); i++) {
                    io.out().println((i + 1) + ": " + entryProposals.get(i));
                }

                io.out().print("What should your entry - class be? (1 - " + entryProposals.size() + "). [Enter for custom entry - class]: ");
                String a_moduleEntry = io.in().next();

                if (a_moduleEntry.isBlank()){
                    io.out().print("Please specify an entry - class: ");
                    break;
                }
                else {
                    try {
                        int i_a_moduleEntry = Integer.parseInt(a_moduleEntry);
                        if(i_a_moduleEntry < 1 || i_a_moduleEntry > entryProposals.size()){
                            throw new NumberFormatException();
                        }

                        String picked = entryProposals.get(i_a_moduleEntry - 1);
                        io.out().println("> " + picked);
                        return picked;
                    }
                    catch (NumberFormatException e){
                        io.out().println("'" + a_moduleEntry + "' is not a number between 1 - " + entryProposals.size() + "!");
                    }
                }
            }
        }

        String moduleEntry;
        while(true) {
            String a_moduleEntry = io.in().next();

            if (a_moduleEntry.isBlank()) {
                io.out().print("Cannot leave blank. Please specify an entry - class: ");
            } else {
                io.out().print("> " + a_moduleEntry + "? [Y/n]: ");
                boolean a_y_moduleEntry = IOUtils.getYNAnswer(io.in(), true);

                if(a_y_moduleEntry){
                    moduleEntry = a_moduleEntry;
                    break;
                }
                else {
                    io.out().print("Please specify an entry - class: ");
                }
            }
        }

        return moduleEntry;
    }

    private List<String> getModuleEntryProposals(List<String> classFiles){
        List<String> proposals = new ArrayList<>();

        for(String classFile : classFiles){
            if(classFile.endsWith(".class")){
                String name = classFile.substring(0, classFile.length() - 6);
                if(name.endsWith("Module") || name.endsWith("module")){
                    proposals.add(classFile);
                }
            }
        }

        return proposals;
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("--- Command [" + commandName + "] ---");
        out.println("A command to manage custom modules.");
        out.newLine();

        out.println("# Usage:");
        out.println("|");
        out.println("| " + commandName + "  -> A command to manage the development of a custom module. It will start an interactive conversation to help you set up a module or to update it.");
    }
}
