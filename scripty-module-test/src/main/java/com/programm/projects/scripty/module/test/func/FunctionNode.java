package com.programm.projects.scripty.module.test.func;

import java.util.List;

class FunctionNode implements FNode {

    private final FFunction function;
    private final FNode input;

    public FunctionNode(FFunction function, FNode input) {
        this.function = function;
        this.input = input;
    }

    @Override
    public double run(FContext context) {
        double result = input.run(context);
        return function.run(result);
    }

    @Override
    public void collectVars(List<String> names) {
        input.collectVars(names);
    }

    @Override
    public FNode copy() {
        return new FunctionNode(function, input.copy());
    }

    @Override
    public String toString() {
        return function.name + "(" + input + ")";
    }
}
