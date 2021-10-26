package com.programm.projects.scripty.app.modules;

import com.programm.projects.scripty.module.api.IContext;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ExecutableModule {

    public final Object module;
    public final List<MethodRunner> preSetupMethods;
    public final List<MethodRunner> postSetupMethods;

    public ExecutableModule(Object module, List<MethodRunner> preSetupMethods, List<MethodRunner> postSetupMethods) {
        this.module = module;
        this.preSetupMethods = preSetupMethods;
        this.postSetupMethods = postSetupMethods;
    }

    public void preSetup(IContext ctx){
        for(MethodRunner runner : preSetupMethods){
            try {
                runner.run();
            }
            catch (InvocationTargetException e){
                ctx.err().println("[PRE-SETUP ]: Exception in Module [{}] in Method [{}]: {}", module.getClass().getSimpleName(), runner.method.getName(), e.getMessage());
            }
        }
    }

    public void postSetup(IContext ctx){
        for(MethodRunner runner : postSetupMethods){
            try {
                runner.run();
            }
            catch (InvocationTargetException e){
                ctx.err().println("[POST-SETUP]: Exception in Module [{}] in Method [{}]: {}", module.getClass().getSimpleName(), runner.method.getName(), e.getMessage());
            }
        }
    }

}
