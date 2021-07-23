package com.programm.projects.scripty.core;

public class PrependOut implements IOutput {

    private final IOutput out;
    private String prepend;

    public PrependOut(IOutput out) {
        this.out = out;
    }

    public PrependOut(IOutput out, String prepend) {
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

    @Override
    public void enable(boolean enable) {
        out.enable(enable);
    }

    public void setPrepend(String prepend) {
        this.prepend = prepend;
    }
}
