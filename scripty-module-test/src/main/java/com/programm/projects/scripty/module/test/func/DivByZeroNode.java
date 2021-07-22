package com.programm.projects.scripty.module.test.func;

import java.util.List;

class DivByZeroNode implements FNode{

    @Override
    public double run(FContext context) {
        throw new DivisionByZeroException();
    }

    @Override
    public void collectVars(List<String> names) {}

    @Override
    public FNode copy() {
        return new DivByZeroNode();
    }

    @Override
    public String toString() {
        return "{{Division by zero!}}";
    }
}
