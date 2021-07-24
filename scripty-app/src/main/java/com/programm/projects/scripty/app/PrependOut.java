package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;

class PrependOut implements IOutput {

    private final ScriptyOut out;
    private String prepend;

    public PrependOut(ScriptyOut out) {
        this.out = out;
    }

    public PrependOut(ScriptyOut out, String prepend) {
        this.out = out;
        this.prepend = prepend;
    }

    @Override
    public final void print(String s) {
        out.print(prepend + s);
    }

    @Override
    public void newLine() {
        out.newLine();
    }

    public void enable(boolean enable) {
        out.enable(enable);
    }

    @Override
    public boolean enabled() {
        return out.enabled();
    }

    public void setPrepend(String prepend) {
        this.prepend = prepend;
    }
}
