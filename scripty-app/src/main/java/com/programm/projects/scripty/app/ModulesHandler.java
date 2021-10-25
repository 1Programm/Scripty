package com.programm.projects.scripty.app;

import com.programm.projects.scripty.module.api.SyModule;

import java.util.ArrayList;
import java.util.List;

public class ModulesHandler {

    private final List<SyModule> modules = new ArrayList<>();

    public void add(SyModule module){
        modules.add(module);
    }

    public void remove(SyModule module){
        modules.remove(module);
    }

}
