package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.core.ModuleFileConfig;
import com.programm.projects.scripty.modules.api.Module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ScriptyModulesManager {

    private final IOutput log;
    private final IOutput err;

    private final List<Module> modules = new ArrayList<>();
    private final Map<Module, ModuleFileConfig> moduleConfigs = new HashMap<>();

    public ScriptyModulesManager(IOutput log, IOutput err) {
        this.log = log;
        this.err = err;
    }

    public void initModules(URL[] classPaths, List<String> entryPoints, Map<String, ModuleFileConfig> namedModuleConfigs, ScriptyCoreContext ctx){
        log.println("Adding [" + classPaths.length + "] classPaths to ClassLoader ...");

        URLClassLoader classLoader = new URLClassLoader(classPaths);


        for(String entry : entryPoints){
            Class<?> entryClass;
            try {
                entryClass = classLoader.loadClass(entry);
            }
            catch (ClassNotFoundException e){
                err.println("Could not find entry-point class: [" + entry + "].");
                continue;
            }

            ModuleFileConfig config = namedModuleConfigs.get(entry);
            _initEntryClass(entryClass, config);
        }



        // NOW INIT ALL MODULES
        log.println("Initializing Modules ...");

        for(Module module : modules){
            ModuleFileConfig moduleConfig = moduleConfigs.get(module);
            module.init(ctx, moduleConfig);
        }

        log.println("Finished initialization of Modules.");
    }

    private void _initEntryClass(Class<?> cls, ModuleFileConfig config){
        if(Module.class.isAssignableFrom(cls)){
            Module module = _createModule(cls);
            modules.add(module);
            moduleConfigs.put(module, config);
        }
    }

    private Module _createModule(Class<?> moduleClass){
        try {
            Constructor<?> emptyConstructor = moduleClass.getConstructor();
            Object oModule = emptyConstructor.newInstance();
            return (Module) oModule;
        }
        catch (NoSuchMethodException e){
            err.println("No empty constructor defined for class: [" + moduleClass.getName() + "].");
        } catch (InvocationTargetException e) {
            err.println("Empty constructor of class: [" + moduleClass.getName() + "] threw an Error: " + e.getMessage());
        } catch (InstantiationException e) {
            err.println("Cannot instantiate class: [" + moduleClass.getName() + "] as it is abstract.");
        } catch (IllegalAccessException e) {
            err.println("Empty constructor of class: [" + moduleClass.getName() + "] could not be accessed.");
        }

        return null;
    }

}
