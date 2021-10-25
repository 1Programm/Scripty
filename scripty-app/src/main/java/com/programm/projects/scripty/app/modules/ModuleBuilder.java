package com.programm.projects.scripty.app.modules;

import com.programm.projects.scripty.module.api.SyContext;
import com.programm.projects.scripty.module.api.SyModule;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.OnMessage;
import com.programm.projects.scripty.module.api.events.PostSetup;
import com.programm.projects.scripty.module.api.events.PreSetup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModuleBuilder {

    public static void setupModule(SyModule module, SyContext ctx){

    }

    public static ExecutableModule buildModule(Class<?> cls, Function<Class<?>, Object> getMap) throws SyModuleBuildException {
        if(!SyModule.class.isAssignableFrom(cls)) {
            throw new SyModuleBuildException("Cannot build a module out of non - module class [" + cls.getName() + "]!");
        }

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
        List<MethodRunner> onMessageMethods = new ArrayList<>();

        for(Method method : cls.getDeclaredMethods()){
            if(method.isAnnotationPresent(PreSetup.class)){
                preSetupMethods.add(buildMethodRunner(cls, method, instance, getMap));
            }
            else if(method.isAnnotationPresent(PostSetup.class)){
                postSetupMethods.add(buildMethodRunner(cls, method, instance, getMap));
            }
            else if(method.isAnnotationPresent(OnMessage.class)){
                onMessageMethods.add(new MethodRunner(method, instance, null));
            }
        }

        return new ExecutableModule((SyModule) instance, preSetupMethods, postSetupMethods, onMessageMethods);
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
