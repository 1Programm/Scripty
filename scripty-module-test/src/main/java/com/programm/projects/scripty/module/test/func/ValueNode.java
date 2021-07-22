package com.programm.projects.scripty.module.test.func;

import java.util.List;

class ValueNode implements FNode {

    final double value;

    public ValueNode(double value) {
        this.value = value;
    }

    @Override
    public double run(FContext context) {
        return value;
    }

    @Override
    public void collectVars(List<String> names) {}

    @Override
    public FNode copy() {
        return new ValueNode(value);
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
