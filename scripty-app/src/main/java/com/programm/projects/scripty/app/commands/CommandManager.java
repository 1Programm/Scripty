package com.programm.projects.scripty.app.commands;

import com.programm.projects.scripty.module.api.Command;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private static String generateNameForCommand(Class<?> cls) throws CommandNamingException{
        Command cmdAnnotation = cls.getAnnotation(Command.class);

        boolean generated = false;
        String name = cmdAnnotation.value();
        //Should not be null as Objects passed in here must be annotated with @Command

        if(name.equals("")){
            generated = true;
            name = cls.getSimpleName();

            if(name.startsWith("command")){
                name = name.substring("command".length());
            }
            else if(name.startsWith("Command")){
                name = name.substring("Command".length());
            }
            else if(name.endsWith("command")){
                name = name.substring(0, name.length() - "command".length());
            }
            else if(name.endsWith("Command")){
                name = name.substring(0, name.length() - "Command".length());
            }

            StringBuilder sb = new StringBuilder();
            boolean hasLowerBefore = false;

            for(int i=0;i<name.length();i++){
                char c = name.charAt(i);

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

            name = sb.toString();
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

        public void run(String name, String input) throws CommandExecutionException {
            try {
                commandMethod.invoke(commandInstance, name, input);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("INVALID STATE: Method should have already been tested if it can be accessed!");
            } catch (InvocationTargetException e) {
                throw new CommandExecutionException("Exception while executing command - method of command: [" + name + "]!");
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

    public void registerCommand(Object command) throws CommandNamingException, CommandSetupException {
        String name = generateNameForCommand(command.getClass());

        if(commandInstances.containsKey(name)){
            throw new CommandNamingException("Command with name [" + name + "] is already registered!");
        }

        Class<?> cmdClass = command.getClass();
        Method commandMethod = null;

        for(Method method : cmdClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(Command.class)){
                commandMethod = method;
                break;
            }
        }

        if(commandMethod == null){
            throw new CommandSetupException("Command method could not be found in class: [" + cmdClass.getName() + "]!");
        }

        Class<?>[] types = commandMethod.getParameterTypes();
        if(commandMethod.getParameterCount() != 2 || types[0] != String.class || types[1] != String.class){
            throw new CommandSetupException("Command method in class [" + cmdClass.getName() + "] must have exactly 2 Strings as parameters!");
        }

        commandInstances.put(name, new CommandWrapper(command, commandMethod));
    }

}
