package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyCommandManager;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.util.HashMap;
import java.util.Map;

class ScriptyCommandManager implements SyCommandManager {

    final Map<String, SyCommand> commandMap = new HashMap<>();

    @Override
    public void registerCommand(String name, SyCommand command) throws InvalidNameException {
        if(name == null) throw new NullPointerException("Name cannot be null!");
        if(command == null) throw new NullPointerException("Command cannot be null!");

        _checkInvalidFormat(name);

        if(commandMap.containsKey(name)){
            throw new InvalidNameException("Name is already used for a custom command!");
        }

        commandMap.put(name, command);
    }

    private void _checkInvalidFormat(String name) throws InvalidNameException {
        if(!(name.matches("[_a-zA-Z0-9\\-]+"))){
            throw new InvalidNameException("Name must match the pattern: '[_a-zA-Z0-9]+'");
        }
    }

}
