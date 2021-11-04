package com.programm.projects.scripty.app.newSy;

import com.programm.projects.scripty.app.Scripty;
import com.programm.projects.scripty.app.files.ConfigFileLoader;
import com.programm.projects.scripty.app.files.ModuleConfigFile;
import com.programm.projects.scripty.app.modules.ExecutableModule;
import com.programm.projects.scripty.app.modules.SyModuleBuildException;
import com.programm.projects.scripty.app.newSy.ex.CommandInstantiationException;
import com.programm.projects.scripty.app.utils.StringUtils;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class TestBuilder {

    public static void main(String[] args) throws Exception{
        File module = new File("/Users/julian/Desktop/Programming/Java/projects/scripty/scripty-module-helloworld");
        Scripty ctx = new Scripty("");
        ScriptyGetManager getManager = new ScriptyGetManager(ctx);

        buildModules(getManager, module);
    }

    public static ExecutableModule buildModules(ScriptyGetManager getManager, File... moduleFolders) throws SyModuleBuildException {
        List<String> fullClassNames = new ArrayList<>();
        URL[] classPathUrls = new URL[moduleFolders.length];

        for(int i=0;i<moduleFolders.length;i++) {
            File moduleFolder = moduleFolders[i];
            File moduleConfigFile = new File(moduleFolder, Scripty.FILE_MODULE);

            if (!moduleConfigFile.exists())
                throw new SyModuleBuildException("Invalid Module! No [sy.module] file found inside: " + moduleFolder.getAbsolutePath());

            ModuleConfigFile moduleConfig;
            try {
                moduleConfig = ConfigFileLoader.moduleConfigFileLoader(moduleConfigFile);
            } catch (IOException e) {
                throw new SyModuleBuildException("Could not load module config file!", e);
            }

            File rootFolder = new File(moduleFolder, moduleConfig.getRootFolder());

            if (!rootFolder.exists())
                throw new SyModuleBuildException("Root folder [" + moduleConfig.getRootFolder() + "] could not be found at: " + rootFolder.getAbsolutePath());

            try {
                classPathUrls[i] = rootFolder.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String packagePath = moduleConfig.getBasePackage().replaceAll("\\.", "/");

            File basePackageFolder = new File(rootFolder, packagePath);

            if (!basePackageFolder.exists())
                throw new SyModuleBuildException("Base package [" + moduleConfig.getBasePackage() + "] could not be found at: " + basePackageFolder.getAbsolutePath());


            List<File> parents = new ArrayList<>();
            parents.add(basePackageFolder);

            while (!parents.isEmpty()) {
                File parent = parents.remove(0);

                File[] children = parent.listFiles();
                if (children != null) {
                    for (File child : children) {
                        if (child.isDirectory()) {
                            parents.add(child);
                        } else {
                            String name = child.getName();
                            if (name.endsWith(".class")) {
                                String fullClassName = StringUtils.substring(child.getAbsolutePath(), rootFolder.getAbsolutePath().length() + 1, -6);
                                fullClassName = fullClassName.replaceAll("/", ".");
                                fullClassNames.add(fullClassName);
                            }
                        }
                    }
                }
            }
        }

        List<Class<?>> services = new ArrayList<>();
        List<Class<?>> commands = new ArrayList<>();

        URLClassLoader loader = URLClassLoader.newInstance(classPathUrls);

        for(String fullClassName : fullClassNames){
            try {
                Class<?> cls = loader.loadClass(fullClassName);
                boolean isService = cls.isAnnotationPresent(Service.class);
                boolean isCommand = cls.isAnnotationPresent(Command.class);
                if(isService && isCommand){
                    System.err.println("Class [" + fullClassName + "] cannot use both @Command and @Service Annotations.");
                }
                else if(isService){
                    services.add(cls);
                }
                else if(isCommand){
                    commands.add(cls);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Something went wrong!");
                e.printStackTrace();
            }
        }

        for(Class<?> cls : services){
            getManager.registerServices(cls);
        }

        for(Class<?> cls : commands){
            try {
                getManager.registerCommands(cls);
            }
            catch (CommandInstantiationException e){
                e.printStackTrace();//TODO
            }
        }

        getManager.callPostSetup();

        return null;
    }

}
