package com.programm.projects.scripty.module.test.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Function implements FContext {

    final FNode rootNode;
    private final Map<String, Double> vars = new HashMap<>();

    public Function(String function) {
        this(FunctionParser.parse(function));
    }

    Function(FNode rootNode){
        this.rootNode = rootNode;
    }

    /**
     *
     * @return the result provided by this function
     * @throws DivisionByZeroException when a zero division is detected
     */
    public double run() throws DivisionByZeroException {
        return rootNode.run(this);
    }

    public Function set(String name, double value){
        vars.put(name, value);
        return this;
    }

    @Override
    public double var(String name) {
        return vars.get(name);
    }

    public boolean needsVar(){
        return getNeededVarNames().size() > 0;
    }

    public List<String> getNeededVarNames(){
        List<String> names = new ArrayList<>();
        rootNode.collectVars(names);
        return names;
    }

    @Override
    public String toString() {
        return rootNode.toString();
    }
}
