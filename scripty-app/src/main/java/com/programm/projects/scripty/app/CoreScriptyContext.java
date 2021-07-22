package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.ScriptyContext;

public class CoreScriptyContext implements ScriptyContext {

    private final ModuleConfigProvider moduleConfigProvider;
    private final IOutput out;
    private Module currentModuleContext;

    public CoreScriptyContext(ModuleConfigProvider moduleConfigProvider, IOutput out) {
        this.moduleConfigProvider = moduleConfigProvider;
        this.out = out;
    }

    @Override
    public String getModuleName() {
        if(currentModuleContext == null) return "";

        ModuleFileConfig config = moduleConfigProvider.provide(currentModuleContext);

        if(config == null) return "";

        return config.getName();
    }

    @Override
    public IOutput out() {
        return out;
    }

    public void setCurrentModuleContext(Module currentModuleContext) {
        this.currentModuleContext = currentModuleContext;
    }
}
