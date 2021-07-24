package com.programm.projects.scripty.app;

import com.programm.projects.scripty.app.utils.AlignmentFormatter;
import com.programm.projects.scripty.core.IOutput;

import java.io.PrintStream;

public class ScriptyOut implements IOutput {

    private final PrintStream out;
    private boolean enabled;

    public ScriptyOut(PrintStream out, boolean enabled) {
        this.out = out;
        this.enabled = enabled;
    }

    @Override
    public void print(String s) {
        if(!enabled) return;
        s = AlignmentFormatter.format(s);
        out.print(s);
    }

    @Override
    public void newLine() {
        if(!enabled) return;
        out.println();
    }

    public void enable(boolean enable) {
        this.enabled = enable;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }
}
