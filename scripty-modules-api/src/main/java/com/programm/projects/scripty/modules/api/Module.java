package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.core.ModuleFileConfig;

public abstract class Module implements SyIO {

    private SyIO io;

    public final void setup(SyIO io){
        this.io = io;
    }

    public abstract void registerCommands(SyCommandManager commandManager);

    public abstract void init(SyContext context, ModuleFileConfig moduleConfig);

    @Override
    public IOutput out() {
        return io.out();
    }

    @Override
    public IOutput log() {
        return io.log();
    }

    @Override
    public IOutput err() {
        return io.err();
    }
}
