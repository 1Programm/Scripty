package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.ScriptyContext;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.util.HashMap;
import java.util.Map;

class CoreScriptyContext implements ScriptyContext {

    private final IOutput out;
    private final IOutput log;
    private final IOutput err;

    private final Map<String, SyCommand> commandMap = new HashMap<>();

    public CoreScriptyContext(IOutput out, IOutput log, IOutput err) {
        this.out = out;
        this.log = log;
        this.err = err;
    }

    @Override
    public IOutput out() {
        return out;
    }

    @Override
    public IOutput log() {
        return log;
    }

    @Override
    public IOutput err() {
        return err;
    }

    @Override
    public void registerCommand(String name, SyCommand command) throws InvalidNameException {
        if(name == null) throw new NullPointerException("Name cannot be null!");
        if(command == null) throw new NullPointerException("Command cannot be null!");

        _checkInvalidFormat(name);
        _checkSystemCommands(name);

        if(commandMap.containsKey(name)){
            throw new InvalidNameException("Name is already used for a custom command!");
        }

        commandMap.put(name, command);
    }

    private void _checkInvalidFormat(String name) throws InvalidNameException {
        if(!(name.matches("[_a-zA-Z0-9]+"))){
            throw new InvalidNameException("Name must match the pattern: '[_a-zA-Z0-9]+'");
        }
    }

    private void _checkSystemCommands(String name) throws InvalidNameException {
        if(name.equals("modules-list")
        || name.equals("modules-add")
        || name.equals("modules-remove")){
            throw new InvalidNameException("Name is used by a system command and therefore cannot be used!");
        }
    }

    public SyCommand getCommand(String name){
        return commandMap.get(name);
    }

}
