package com.programm.projects.scripty.app.newSy;

import com.programm.projects.scripty.app.newSy.ex.CommandInstantiationException;
import com.programm.projects.scripty.app.newSy.ex.MagicMethodException;
import com.programm.projects.scripty.app.utils.ClassHelper;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.commands.ICommand;
import com.programm.projects.scripty.module.api.events.Get;
import com.programm.projects.scripty.module.api.events.PostSetup;
import com.programm.projects.scripty.module.api.events.PreSetup;
import com.programm.projects.scripty.module.api.events.PreShutdown;
import lombok.AllArgsConstructor;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptyGetManager {

    @AllArgsConstructor
    private static class WaitEntry {
        Field toSet;
        Object instance;
        String name;
    }

    private final Map<String, CommandImpl> commands = new HashMap<>();
    private final Map<Class<?>, Map<String, Object>> instances = new HashMap<>();

    private final Map<Class<?>, List<WaitEntry>> waitForMap = new HashMap<>();

    private final List<MagicMethod> postSetupMethods = new ArrayList<>();
    private final List<MagicMethod> preShutdownMethods = new ArrayList<>();

    public ScriptyGetManager(IContext ctx){
        instances.put(IContext.class, Map.of("", ctx));
    }

    void registerServices(Class<?> cls){
        Object instance = instantiateClass(cls);
        registerInstance(cls, instance, "");
        callPreSetup(cls, instance);
    }

    void registerCommands(Class<?> cls) throws CommandInstantiationException {
        String commandName = getOrGenerateCommandName(cls);
        Object instance = instantiateClass(cls);
        MagicMethod commandMethod = getCommandMethod(cls, instance);

        CommandImpl cmd = new CommandImpl(commandName, commandMethod);
        commands.put(commandName, cmd);

        registerInstance(cls, instance, "");
        registerInstance(ICommand.class, cmd, commandName);

        callPreSetup(cls, instance);
    }

    private void callPreSetup(Class<?> cls, Object o){
        Method[] methods = cls.getDeclaredMethods();

        for(Method method : methods) {
            int mods = method.getModifiers();

            if(Modifier.isStatic(mods)) continue;

            if(method.isAnnotationPresent(PreSetup.class)){
                try {
                    runMagicMethod(o, method);
                }
                catch (MagicMethodException e){
                    System.err.println("Error running Pre Setup Method!");
                    e.printStackTrace();
                }
            }
        }
    }

    void callPostSetup(){
        for(MagicMethod method : postSetupMethods){
            method.run();
        }
    }

    void callPreShutdown(){
        for(MagicMethod method : preShutdownMethods){
            method.run();
        }
    }

    private Object instantiateClass(Class<?> cls){
        try {
            Object instance = ClassHelper.createInstanceFromEmptyConstructor(cls, Exception::new);

            for(Field field : cls.getDeclaredFields()){
                Get getAnnotation = field.getAnnotation(Get.class);
                if(getAnnotation != null){
                    int mods = field.getModifiers();
                    if(Modifier.isStatic(mods)) continue;
                    if(Modifier.isFinal(mods)) continue;

                    String valName = getAnnotation.value();

                    Class<?> valueCls = field.getType();
                    Map<String, Object> valueMap = instances.get(valueCls);
                    Object value = null;

                    //Wait for it
                    if(valueMap == null || valueMap.size() == 0){
                        WaitEntry entry = new WaitEntry(field, instance, valName);
                        waitForMap.computeIfAbsent(valueCls, c -> new ArrayList<>()).add(entry);
                    }
                    else {
                        if(valueMap.size() == 1){
                            for(String key : valueMap.keySet()){
                                value = valueMap.get(key);
                                break;
                            }
                        }
                        else {

                            if(valName.equals("")){
                                throw new Exception();//TODO
                            }

                            if(!valueMap.containsKey(valName)){
                                throw new Exception();//TODO
                            }

                            value = valueMap.get(valName);
                        }
                    }

                    if(value != null) {
                        setField(field, instance, value);
                    }
                }
            }

            for(Method method : cls.getDeclaredMethods()){
                if(method.isAnnotationPresent(PostSetup.class)) {
                    int mods = method.getModifiers();
                    if (Modifier.isStatic(mods)) continue;

                    postSetupMethods.add(new MagicMethod(method, instance, this::getFunction));
                }
                if(method.isAnnotationPresent(PreShutdown.class)) {
                    int mods = method.getModifiers();
                    if (Modifier.isStatic(mods)) continue;

                    preShutdownMethods.add(new MagicMethod(method, instance, this::getFunction));
                }
            }

            return instance;
        }
        catch (Exception e){
            e.printStackTrace();//TODO
        }

        return null;
    }

    private MagicMethod getCommandMethod(Class<?> cls, Object instance) throws CommandInstantiationException {
        Method m = null;
        Method[] methods = cls.getDeclaredMethods();

        for(Method method : methods){
            int mods = method.getModifiers();

            if(Modifier.isStatic(mods)) continue;

            Command cmdAnnotation = method.getAnnotation(Command.class);
            if(cmdAnnotation != null){
                if(m != null){
                    throw new CommandInstantiationException("Cannot have muliple @Command methods!");
                }

                m = method;
            }
        }

        if(m == null){
            throw new CommandInstantiationException("Command must specify its command method via the @Command annotation.");
        }

        return new MagicMethod(m, instance, this::getFunction);
    }

    private String getOrGenerateCommandName(Class<?> cls){
        Command cmdAnnotation = cls.getAnnotation(Command.class);
        String commandName = cmdAnnotation.value();

        //Generate name
        if(commandName.equals("")){
            String clsName = cls.getSimpleName();

            if(clsName.startsWith("cmd")) clsName = clsName.substring(3);
            if(clsName.startsWith("Cmd")) clsName = clsName.substring(3);
            if(clsName.startsWith("command")) clsName = clsName.substring(7);
            if(clsName.startsWith("Command")) clsName = clsName.substring(7);

            if(clsName.endsWith("cmd")) clsName = clsName.substring(0, clsName.length() - 3);
            if(clsName.endsWith("Cmd")) clsName = clsName.substring(0, clsName.length() - 3);
            if(clsName.endsWith("command")) clsName = clsName.substring(0, clsName.length() - 7);
            if(clsName.endsWith("Command")) clsName = clsName.substring(0, clsName.length() - 7);

            StringBuilder sb = new StringBuilder();

            for(int i=0;i<clsName.length();i++){
                char c = clsName.charAt(i);

                if(Character.isUpperCase(c)){
                    if(i != 0){
                        sb.append("-");
                    }

                    sb.append(Character.toLowerCase(c));
                }
                else {
                    sb.append(c);
                }
            }

            commandName = sb.toString();
        }

        return commandName;
    }

    private void registerInstance(Class<?> cls, Object instance, String name){
        instances.computeIfAbsent(cls, c -> new HashMap<>()).put(name, instance);

        List<WaitEntry> entries = waitForMap.get(cls);
        if(entries != null){
            for(WaitEntry entry : entries){
                if(entry.name.equals(name)) {
                    try {
                        setField(entry.toSet, entry.instance, instance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();//TODO
                    }
                }
            }
        }
        waitForMap.remove(cls);
    }

    private void setField(Field field, Object instance, Object value) throws IllegalAccessException {
        boolean access = field.canAccess(instance);

        if (!access) field.setAccessible(true);

        field.set(instance, value);

        if (!access) field.setAccessible(false);
    }

    private void runMagicMethod(Object instance, Method method, Object... args) throws MagicMethodException {
        int goalLength = method.getParameterCount();
        Object[] actualArgs = new Object[goalLength];

        TypeVariable<Method>[] typeParams = method.getTypeParameters();
        Class<?>[] types = method.getParameterTypes();

        int c = 0;
        for(int i=0;i<goalLength;i++){
            Get getAnnotation = typeParams[i].getAnnotation(Get.class);

            if(getAnnotation == null){
                if(c >= args.length) {
                    throw new MagicMethodException("Not enough arguments for Magic Method!");
                }
                actualArgs[i] = args[c];
                c++;
            }
            else {
                Object obj = getFunction(types[i], getAnnotation);
                actualArgs[i] = obj;
            }
        }

        try {
            method.invoke(instance, actualArgs);
        } catch (IllegalAccessException e) {
            throw new MagicMethodException(e);
        }
        catch (InvocationTargetException e){
            throw new MagicMethodException(e.getMessage(), e);
        }
    }

    private Object getFunction(Class<?> cls, Get annotation){
        String valName = annotation.value();
        Object value = null;

        Map<String, Object> namedValues = instances.get(cls);
        if(namedValues.size() == 1){
            for(String key : namedValues.keySet()){
                value = namedValues.get(key);
                break;
            }
        }
        else {
            if(valName.equals("")){
                throw new RuntimeException();//TODO
            }

            if(!namedValues.containsKey(valName)){
                throw new RuntimeException();//TODO
            }

            value = namedValues.get(valName);
        }

        return value;
    }

}
