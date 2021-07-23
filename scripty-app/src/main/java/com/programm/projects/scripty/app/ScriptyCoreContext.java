package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyCommand;
import com.programm.projects.scripty.modules.api.SyWorkspace;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.util.HashMap;
import java.util.Map;

class ScriptyCoreContext implements SyContext {

    private final IOutput out;
    private final IOutput log;
    private final IOutput err;
    private final SyWorkspace workspace;

    final Map<String, SyCommand> commandMap = new HashMap<>();

    public ScriptyCoreContext(IOutput out, IOutput log, IOutput err, SyWorkspace workspace) {
        this.out = out;
        this.log = log;
        this.err = err;
        this.workspace = workspace;
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
    public SyWorkspace workspace() {
        return workspace;
    }

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

    public SyCommand getCommand(String name){
        return commandMap.get(name);
    }

}
