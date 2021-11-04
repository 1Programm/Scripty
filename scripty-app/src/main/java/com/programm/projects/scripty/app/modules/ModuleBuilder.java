package com.programm.projects.scripty.app.modules;

import com.programm.projects.scripty.app.Scripty;
import com.programm.projects.scripty.app.files.ConfigFileLoader;
import com.programm.projects.scripty.app.files.ModuleConfigFile;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.PostSetup;
import com.programm.projects.scripty.module.api.events.PreSetup;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class ModuleBuilder {

    private final Function<Class<?>, Object> getMap;

    public static ExecutableModule buildModule(File moduleFolder) throws SyModuleBuildException {
        File moduleConfigFile = new File(moduleFolder, Scripty.FILE_MODULE);

        if(!moduleConfigFile.exists()) throw new SyModuleBuildException("Invalid Module! No [sy.module] file found inside: " + moduleFolder.getAbsolutePath());

        ModuleConfigFile moduleConfig;
        try {
            moduleConfig = ConfigFileLoader.moduleConfigFileLoader(moduleConfigFile);
        } catch (IOException e) {
            throw new SyModuleBuildException("Could not load module config file!", e);
        }


        return null;
    }

    public static ExecutableModule buildModule(Class<?> cls, Function<Class<?>, Object> getMap) throws SyModuleBuildException {
        Object instance = createInstanceFromEmptyConstructor(cls);



        for(Field field : cls.getDeclaredFields()){
            if(field.isAnnotationPresent(Get.class)){
                Class<?> typeCls = field.getType();
                Object getVal = getMap.apply(typeCls);

                if(getVal == null){
                    throw new SyModuleBuildException("Field [" + field.getName() + "] in class [" + cls.getSimpleName() + "] could not be loaded by the @Get Annotation.");
                }

                try {
                    boolean access = field.canAccess(instance);
                    if(!access) field.setAccessible(true);

                    field.set(instance, getVal);

                    if(!access) field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();//TODO warning instead stacktrace
                }
            }
        }

        List<MethodRunner> preSetupMethods = new ArrayList<>();
        List<MethodRunner> postSetupMethods = new ArrayList<>();

        for(Method method : cls.getDeclaredMethods()){
            if(method.isAnnotationPresent(PreSetup.class)){
                preSetupMethods.add(buildMethodRunner(cls, method, instance, getMap));
            }
            else if(method.isAnnotationPresent(PostSetup.class)){
                postSetupMethods.add(buildMethodRunner(cls, method, instance, getMap));
            }
        }

        return new ExecutableModule(instance, preSetupMethods, postSetupMethods);
    }

    private static MethodRunner buildMethodRunner(Class<?> cls, Method method, Object instance, Function<Class<?>, Object> getMap) throws SyModuleBuildException{
        Class<?>[] paramClasses = method.getParameterTypes();
        Object[] args = new Object[paramClasses.length];

        for(int i=0;i<paramClasses.length;i++){
            Class<?> paramCls = paramClasses[i];
            Object getVal = getMap.apply(paramCls);

            if(getVal == null){
                throw new SyModuleBuildException("Param [" + method.getTypeParameters()[0].getName() + "] in class [" + cls.getSimpleName() + "] could not be loaded by the @Get Annotation.");
            }

            args[i] = getVal;
        }

        return new MethodRunner(method, instance, args);
    }

//    private static Object getGetMapValue(Class<?> cls, Function<Class<?>, Object> getMap) throws SyModuleBuildException {
//        Object getVal = getMap.apply(cls);
//
//        if(getVal == null){
//            throw new SyModuleBuildException("Field [" + cls.getName() + "] in class [" + cls.getSimpleName() + "] could not be loaded by the @Get Annotation.");
//        }
//    }

    private static Object createInstanceFromEmptyConstructor(Class<?> cls) throws SyModuleBuildException {
        try {
            return cls.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new SyModuleBuildException("Class [" + cls.getName() + "] represents an abstract class and cannot be instantiated!", e);
        } catch (IllegalAccessException e) {
            throw new SyModuleBuildException("Empty constructor of class [" + cls.getName() + "] is private and cannot be accessed!", e);
        } catch (InvocationTargetException e) {
            throw new SyModuleBuildException("Exception in empty constructor of class [" + cls.getName() + "]!", e);
        } catch (NoSuchMethodException e) {
            throw new SyModuleBuildException("Cannot find empty constructor of class [" + cls.getName() + "]!", e);
        }
    }

}
