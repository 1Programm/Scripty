package com.programm.projects.scripty.module.test.func;

import java.util.List;
import java.util.function.BiFunction;

class OperationNode implements FNode {

    final char operationName;
    final FNode left, right;

    public OperationNode(char operationName, FNode left, FNode right) {
        this.operationName = operationName;
        this.left = left;
        this.right = right;
    }

    @Override
    public double run(FContext context) {
        double leftVal = left.run(context);
        double rightVal = right.run(context);

        if(operationName == '/' && rightVal == 0){
            throw new DivisionByZeroException();
        }

        return apply(leftVal, rightVal);
    }

    double apply(double left, double right){
        BiFunction<Double, Double, Double> operation = FunctionParser.operations.get(operationName);
        return operation.apply(left, right);
    }

    @Override
    public void collectVars(List<String> names) {
        left.collectVars(names);
        right.collectVars(names);
    }

    @Override
    public FNode copy() {
        return new OperationNode(operationName, left.copy(), right.copy());
    }

    @Override
    public String toString() {
        return "(" + left + " " + operationName + " " + right + ")";
    }
}
