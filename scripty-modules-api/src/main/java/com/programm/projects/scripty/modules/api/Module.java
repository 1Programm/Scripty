package com.programm.projects.scripty.modules.api;

public abstract class Module {

    private ScriptyContext context;

    protected abstract void onInit(ScriptyContext context);

    public final void init(ScriptyContext context){
        this.context = context;

        onInit(context);
    }

    public String getName() {
        return context.getModuleName();
    }
}
