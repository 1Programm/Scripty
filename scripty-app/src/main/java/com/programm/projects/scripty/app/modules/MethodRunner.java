package com.programm.projects.scripty.app.modules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

 class MethodRunner {

    public final Method method;
    public final Object instance;
    public final Object[] args;

    public MethodRunner(Method method, Object instance, Object[] args) {
        this.method = method;
        this.instance = instance;
        this.args = args;
    }

    public void run(Object... args) throws InvocationTargetException {
        try {
            method.invoke(instance, args);
        }
        catch (IllegalAccessException e){
            System.err.println("ILLEGAL ACCESS EXCEPTION: SHOULD NOT HAPPEN");
            e.printStackTrace();
        }
    }

    public void run() throws InvocationTargetException {
        run(args);
    }

}
