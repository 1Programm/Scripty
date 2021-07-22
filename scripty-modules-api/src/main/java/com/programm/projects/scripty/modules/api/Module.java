package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.ModuleFileConfig;

public abstract class Module {

    public abstract void init(ScriptyContext context, ModuleFileConfig moduleConfig);
}
