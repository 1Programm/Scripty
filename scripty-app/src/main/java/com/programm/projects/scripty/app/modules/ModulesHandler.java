package com.programm.projects.scripty.app.modules;

import java.util.ArrayList;
import java.util.List;

public class ModulesHandler {

    private final List<IModule> modules = new ArrayList<>();

    public void add(IModule module){
        modules.add(module);
    }

    public void remove(IModule module){
        modules.remove(module);
    }

}
