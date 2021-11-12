package com.programm.projects.scripty.app.commands;

import com.programm.projects.plugz.magic.MagicEnvironment;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.Help;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private static String removeStartingWith(String s, String... beginnings){
        for(String begin : beginnings){
            if(s.startsWith(begin)){
                s = s.substring(begin.length());
                break;
            }
        }

        return s;
    }

    private static String removeEndingWith(String s, String... endings){
        for(String end : endings){
            if(s.endsWith(end)){
                s = s.substring(0, s.length() - end.length());
                break;
            }
        }

        return s;
    }

    private static String camelCaseToHyphenString(String s){
        StringBuilder sb = new StringBuilder();
        boolean hasLowerBefore = false;

        for(int i=0;i<s.length();i++){
            char c = s.charAt(i);

            if(Character.isUpperCase(c)){
                if(hasLowerBefore){
                    sb.append("-");
                }
                hasLowerBefore = false;
                sb.append(Character.toLowerCase(c));
            }
            else {
                hasLowerBefore = true;
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private static String generateNameForCommand(Class<?> cls) throws CommandNamingException{
        Command cmdAnnotation = cls.getAnnotation(Command.class);
        //Should not be null as Classes passed in here must be annotated with @Command

        boolean generated = false;
        String name = cmdAnnotation.value();

        if(name.equals("")){
            generated = true;

            name = cls.getSimpleName();
            name = removeStartingWith(name, "cmd", "Cmd", "command", "Command");
            name = removeEndingWith(name, "cmd", "Cmd", "command", "Command");
            name = camelCaseToHyphenString(name);
        }

        char fc = name.charAt(0);
        char lc = name.charAt(name.length() - 1);
        if(!Character.isLetter(fc)){
            throw new CommandNamingException("Name [" + name + "] " + (generated ? ("generated from class: [" + cls.getName() + "] ") : "") + "must start with an alphabetic letter!");
        }

        if(!Character.isDefined(lc) && !Character.isLetter(lc)){
            throw new CommandNamingException("Name [" + name + "] " + (generated ? ("generated from class: [" + cls.getName() + "] ") : "") + "must end with an alphabetic letter or a number!");
        }

        return name;
    }

    @RequiredArgsConstructor
    private static class CommandWrapper {
        private final Object commandInstance;
        private final Method commandMethod;
        private final Method helpMethod;

        public void run(String name, String input) throws CommandExecutionException {
            try {
                commandMethod.invoke(commandInstance, name, input);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("INVALID STATE: Method should have already been tested if it can be accessed!");
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if(cause == null) {
                    throw new CommandExecutionException("${yellow}([" + name + "]): Unspecified invocation exception!", e);
                }
                else {
                    throw new CommandExecutionException("${yellow}([" + name + "]): " + cause.getMessage(), cause);
                }
            }
        }

        public void runHelp(String name) throws CommandExecutionException{
            if(helpMethod == null) throw new NullPointerException("No help for command [" + name + "] defined!");

            try {
                helpMethod.invoke(commandInstance, name);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("INVALID STATE: Method should have already been tested if it can be accessed!");
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if(cause == null) {
                    throw new CommandExecutionException("${yellow}([" + name + "]): Unspecified invocation exception!", e);
                }
                else {
                    throw new CommandExecutionException("${yellow}([" + name + "]): " + cause.getMessage(), cause);
                }
            }
        }
    }

    private final Map<String, CommandWrapper> commandInstances = new HashMap<>();

    public void run(String name, String input) throws CommandExecutionException {
        CommandWrapper cmd = commandInstances.get(name);

        if(cmd == null) {
            throw new CommandExecutionException("No such command: [" + name + "]!");
        }

        cmd.run(name, input);
    }

    public void runHelp(String name) throws CommandExecutionException {
        CommandWrapper cmd = commandInstances.get(name);

        if(cmd == null){
            throw new NullPointerException("No such command: [" + name + "]!");
        }

        cmd.runHelp(name);
    }

    public void registerCommand(Object command) throws CommandNamingException, CommandSetupException {
        String name = generateNameForCommand(command.getClass());

        if(commandInstances.containsKey(name)){
            throw new CommandNamingException("Command with name [" + name + "] is already registered!");
        }

        Class<?> cmdClass = command.getClass();
        Method commandMethod = null;
        Method helpMethod = null;

        for(Method method : cmdClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(Command.class)){
                commandMethod = method;
            }
            else if(method.isAnnotationPresent(Help.class)){
                helpMethod = method;
            }
        }

        if(commandMethod == null){
            throw new CommandSetupException("Command method could not be found in class: [" + cmdClass.getName() + "]!");
        }

        Class<?>[] types = commandMethod.getParameterTypes();
        if(commandMethod.getParameterCount() != 2 || types[0] != String.class || types[1] != String.class){
            throw new CommandSetupException("Command method in class [" + cmdClass.getName() + "] must have exactly 2 Strings as parameters!");
        }

        if(helpMethod != null){
            Class<?>[] helpTypes = helpMethod.getParameterTypes();
            if(helpMethod.getParameterCount() != 1 || helpTypes[0] != String.class){
                throw new CommandSetupException("Help method in class [" + cmdClass.getName() + "] must have exactly 1 String as a parameter!");
            }
        }

        commandInstances.put(name, new CommandWrapper(command, commandMethod, helpMethod));
    }

    public List<String> getCommandNames(){
        return new ArrayList<>(commandInstances.keySet());
    }

}
