package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.core.ModuleFileConfig;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.SyIO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ScriptyModulesManager {

    private final SyIO io;

    private final List<Module> modules = new ArrayList<>();
    private final Map<Module, ModuleFileConfig> moduleConfigs = new HashMap<>();
    private final Map<String, Module> moduleMap = new HashMap<>();

    public ScriptyModulesManager(SyIO io) {
        this.io = io;
    }

    public void loadModules(URL[] classPaths, List<String> entryPoints, Map<String, ModuleFileConfig> namedModuleConfigs){
        io.log().println("Adding [" + classPaths.length + "] classPaths to ClassLoader ...");

        URLClassLoader classLoader = new URLClassLoader(classPaths);


        for(String entry : entryPoints){
            Class<?> entryClass;
            try {
                entryClass = classLoader.loadClass(entry);
            }
            catch (ClassNotFoundException e){
                io.err().println("Could not find entry-point class: [" + entry + "].");
                continue;
            }

            ModuleFileConfig config = namedModuleConfigs.get(entry);
            Module module = _initEntryClass(entryClass);

            if(module != null) {
                modules.add(module);
                moduleConfigs.put(module, config);
                moduleMap.put(config.getName(), module);
            }
        }
    }

    public Module loadSingleModule(URL classPath, String entryPoint) throws ClassNotFoundException {
        URL[] urls = new URL[]{ classPath };
        URLClassLoader classLoader = new URLClassLoader(urls);

        Class<?> entryClass = classLoader.loadClass(entryPoint);
        return _initEntryClass(entryClass);
    }

    public void initRegisterCommands(ScriptyCommandManager commandManager){
        for(Module module : modules){
            module.registerCommands(commandManager);
        }
    }

    public void initModules(ScriptyCoreContext ctx, ModuleIO moduleIO){
        // NOW INIT ALL MODULES
        io.log().println("Initializing Modules ...");

        for(Module module : modules){
            ModuleFileConfig config = moduleConfigs.get(module);
            moduleIO.setModuleName(config.getName());

            ModuleFileConfig moduleConfig = moduleConfigs.get(module);
            module.setup(moduleIO);
            module.init(ctx, moduleConfig);
        }

        io.log().println("Finished initialization of Modules.");
    }

    private Module _initEntryClass(Class<?> cls){
        if(Module.class.isAssignableFrom(cls)){
            return _createModule(cls);
        }

        return null;
    }

    private Module _createModule(Class<?> moduleClass){
        try {
            Constructor<?> emptyConstructor = moduleClass.getConstructor();
            Object oModule = emptyConstructor.newInstance();
            return (Module) oModule;
        }
        catch (NoSuchMethodException e){
            io.err().println("No empty constructor defined for class: [" + moduleClass.getName() + "].");
        } catch (InvocationTargetException e) {
            io.err().println("Empty constructor of class: [" + moduleClass.getName() + "] threw an Error: " + e.getMessage());
        } catch (InstantiationException e) {
            io.err().println("Cannot instantiate class: [" + moduleClass.getName() + "] as it is abstract.");
        } catch (IllegalAccessException e) {
            io.err().println("Empty constructor of class: [" + moduleClass.getName() + "] could not be accessed.");
        }

        return null;
    }

    public ModuleFileConfig getConfig(String name){
        Module m = moduleMap.get(name);
        if(m == null) return null;
        return moduleConfigs.get(m);
    }

}
