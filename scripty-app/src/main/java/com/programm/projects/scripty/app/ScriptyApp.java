package com.programm.projects.scripty.app;

import com.programm.projects.scripty.module.api.SyContext;
import com.programm.projects.scripty.module.api.io.IInput;
import com.programm.projects.scripty.module.api.io.IOutput;

import java.util.HashMap;
import java.util.Map;

public class ScriptyApp implements SyContext {

    private static final Map<Class<?>, Object> getMap = new HashMap<>();
    private static final ScriptyApp APP = new ScriptyApp();

    static {
        getMap.put(SyContext.class, APP);
    }

    public static void main(String[] args) throws Exception{
        ExecutableModule res = ModuleBuilder.buildModule(TestModule.class, ScriptyApp::getFunc);
        ModuleBuilder.setupModule(res.module, APP);
        System.out.println("");
    }

    private static Object getFunc(Class<?> cls){
        return getMap.get(cls);
    }

    @Override
    public IOutput log() {
        return null;
    }

    @Override
    public IOutput out() {
        return null;
    }

    @Override
    public IOutput err() {
        return null;
    }

    @Override
    public IInput in() {
        return null;
    }
}
