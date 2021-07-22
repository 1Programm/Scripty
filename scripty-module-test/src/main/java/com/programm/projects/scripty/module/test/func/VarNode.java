package com.programm.projects.scripty.module.test.func;

import java.util.List;

class VarNode implements FNode {

    final String name;
    double factor;
    double pow;

    public VarNode(String name, double factor, double pow) {
        this.name = name;
        this.factor = factor;
        this.pow = pow;
    }

    @Override
    public double run(FContext context) {
        double var1 = context.var(name);
        double var2 = Math.pow(var1, pow);

        return factor * var2;
    }

    @Override
    public void collectVars(List<String> names) {
        if(!names.contains(name)) {
            names.add(name);
        }
    }

    @Override
    public FNode copy() {
        return new VarNode(name, factor, pow);
    }

    @Override
    public String toString() {
        if(pow == 1 && factor == 1){
            return name;
        }
        else if(factor == 1){
            return name + "^" + powString();
        }
        else if(pow == 1){
            return factorString() + name;
        }
        else {
            return factorString() + "(" + name + "^" + powString() + ")";
        }
    }

    private String factorString(){
        int factorInt = (int)factor;

        if(factor == factorInt){
            return "" + factorInt;
        }
        else {
            return "" + factor;
        }
    }

    private String powString(){
        int powInt = (int)pow;

        if(pow == powInt){
            return "" + powInt;
        }
        else {
            return "" + pow;
        }
    }
}
