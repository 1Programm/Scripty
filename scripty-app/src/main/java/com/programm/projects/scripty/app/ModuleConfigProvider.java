package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.Module;

public interface ModuleConfigProvider {

    ModuleFileConfig provide(Module from);

}
