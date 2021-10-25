package com.programm.projects.scripty.app.modules;

import com.programm.projects.scripty.module.api.SyContext;
import com.programm.projects.scripty.module.api.SyModule;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ExecutableModule {

    public final SyModule module;
    public final List<MethodRunner> preSetupMethods;
    public final List<MethodRunner> postSetupMethods;
    public final List<MethodRunner> onMessageMethods;

    public ExecutableModule(SyModule module, List<MethodRunner> preSetupMethods, List<MethodRunner> postSetupMethods, List<MethodRunner> onMessageMethods) {
        this.module = module;
        this.preSetupMethods = preSetupMethods;
        this.postSetupMethods = postSetupMethods;
        this.onMessageMethods = onMessageMethods;
    }

    public void preSetup(SyContext ctx){
        for(MethodRunner runner : preSetupMethods){
            try {
                runner.run();
            }
            catch (InvocationTargetException e){
                ctx.err().println("[PRE-SETUP ]: Exception in Module [{}] in Method [{}]: {}", module.getClass().getSimpleName(), runner.method.getName(), e.getMessage());
            }
        }
    }

    public void setup(SyContext ctx){
        module.setup(ctx);
    }

    public void postSetup(SyContext ctx){
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
