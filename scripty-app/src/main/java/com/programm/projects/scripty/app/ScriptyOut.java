package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;

import java.io.PrintStream;

public class ScriptyOut implements IOutput {

    private final PrintStream out;
    private final boolean enabled;

    public ScriptyOut(PrintStream out, boolean enabled) {
        this.out = out;
        this.enabled = enabled;
    }

    @Override
    public void print(String s) {
        if(!enabled) return;
        out.print(s);
    }

    @Override
    public void println(String s) {
        if(!enabled) return;
        out.println(s);
    }

    @Override
    public void newLine() {
        if(!enabled) return;
        out.println();
    }
}
