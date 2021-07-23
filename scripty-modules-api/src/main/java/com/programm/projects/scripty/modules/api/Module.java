package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.ModuleFileConfig;

public abstract class Module {

    public abstract void init(SyContext context, ModuleFileConfig moduleConfig);
}
