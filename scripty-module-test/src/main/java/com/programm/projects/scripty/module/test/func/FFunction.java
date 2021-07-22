package com.programm.projects.scripty.module.test.func;

class FFunction  implements IFunction {

    final String name;
    final int numArgs;
    final IFunction function;

    public FFunction(String name, int numArgs, IFunction function) {
        this.name = name;
        this.numArgs = numArgs;
        this.function = function;
    }

    @Override
    public double run(double input){
        return function.run(input);
    }
}
