package com.programm.projects.scripty.module.test.func;

public class FunctionAnalyzer {

    private final Function function;

    public FunctionAnalyzer(String function){
        this(new Function(function));
    }

    public FunctionAnalyzer(Function function){
        this.function = function;
    }

    public Function getFunction(){
        return function;
    }

    public Function simplify(){
        FNode simplified = getSimplified(function.rootNode);

        if(simplified == function.rootNode){
            return function;
        }
        else {
            return new Function(simplified);
        }
    }

    private FNode getSimplified(FNode node){
        if(node instanceof ValueNode){
            return node;
        }
        else if(node instanceof VarNode){
            return node;
        }
        else if(node instanceof OperationNode){
            OperationNode operationNode = (OperationNode) node;
            char op = operationNode.operationName;
            FNode left = operationNode.left;
            FNode right = operationNode.right;

            FNode sleft = getSimplified(left);
            FNode sright = getSimplified(right);

            if(sleft instanceof OperationNode){
                OperationNode oLeft = (OperationNode) sleft;
                char opl = oLeft.operationName;

                //((x + 1) + 2)
                //((x - 1) + 2)
                if(op == '+'){
                    if(opl == '+'){
                        //((x + 1) + 2) -> (x + (1 + 2))
                        FNode _try1 = new OperationNode('+', oLeft.left.copy(), new OperationNode('+', oLeft.right.copy(), sright));
                        FNode try1 = getSimplified(_try1);

                        if(_try1 != try1){
                            return try1;
                        }

                        //((x + 1) + 2) -> (1 + (x + 2))
                        FNode _try2 = new OperationNode('+', oLeft.right.copy(), new OperationNode('+', oLeft.left.copy(), sright));
                        FNode try2 = getSimplified(_try2);

                        if(_try2 != try2){
                            return try2;
                        }
                    }
                    else if(opl == '-'){
                        //((x - 1) + 2) -> (x + (2 - 1))
                        FNode _try1 = new OperationNode('+', oLeft.left.copy(), new OperationNode('-', sright, oLeft.right.copy()));
                        FNode try1 = getSimplified(_try1);

                        if(_try1 != try1){
                            return try1;
                        }

                        //((x - 1) + 2) -> ((x + 2) - 1)
                        FNode _try2 = new OperationNode('-', new OperationNode('+', oLeft.left.copy(), sright), oLeft.right.copy());
                        FNode try2 = getSimplified(_try2);

                        if(_try2 != try2){
                            return try2;
                        }
                    }
                }

                //((x + 1) - 2)
                //((x - 1) - 2)
                else if(op == '-'){
                    if(opl == '+'){
                        //((x + 1) - 2) -> (x + (1 - 2))
                        FNode _try1 = new OperationNode('+', oLeft.left.copy(), new OperationNode('-', oLeft.right.copy(), sright));
                        FNode try1 = getSimplified(_try1);

                        if(_try1 != try1){
                            return try1;
                        }

                        //((x + 1) - 2) -> (1 + (x - 2))
                        FNode _try2 = new OperationNode('+', oLeft.right.copy(), new OperationNode('-', oLeft.left.copy(), sright));
                        FNode try2 = getSimplified(_try2);

                        if(_try2 != try2){
                            return try2;
                        }
                    }
                    else if(opl == '-'){
                        //((x - 1) - 2) -> (x - (2 + 1))
                        FNode _try1 = new OperationNode('-', oLeft.left.copy(), new OperationNode('+', sright, oLeft.right.copy()));
                        FNode try1 = getSimplified(_try1);

                        if(_try1 != try1){
                            return try1;
                        }

                        //((x - 1) - 2) -> ((x - 2) - 1)
                        FNode _try2 = new OperationNode('-', new OperationNode('-', oLeft.left.copy(), sright), oLeft.right.copy());
                        FNode try2 = getSimplified(_try2);

                        if(_try2 != try2){
                            return try2;
                        }
                    }
                }
            }


            if(sleft instanceof ValueNode && sright instanceof ValueNode){
                ValueNode v1 = (ValueNode) sleft;
                ValueNode v2 = (ValueNode) sright;

                if(op == '/' && v2.value == 0){
                    return new DivByZeroNode();
                }

                double result = operationNode.apply(v1.value, v2.value);
                return new ValueNode(result);
            }
            else if(sleft instanceof VarNode && sright instanceof VarNode){
                VarNode v1 = (VarNode) sleft;
                VarNode v2 = (VarNode) sright;

                if(v1.name.equals(v2.name)) {
                    if (op == '+' || op == '-') {
                        if (v1.pow == v2.pow) {
                            double nFactor = operationNode.apply(v1.factor, v2.factor);

                            if (nFactor == 0) {
                                return new ValueNode(0);
                            } else {
                                return new VarNode(v1.name, nFactor, v1.pow);
                            }
                        } else {
                            return new OperationNode(op, sleft, sright);
                        }
                    } else if (op == '*') {
                        double nFactor = v1.factor * v2.factor;
                        double nPow = v1.pow + v2.pow;

                        if (nFactor == 0) {
                            return new ValueNode(0);
                        } else if (nPow == 0) {
                            return new ValueNode(nFactor);
                        } else {
                            return new VarNode(v1.name, nFactor, nPow);
                        }
                    } else if (op == '/') {
                        double nFactor = v1.factor / v2.factor;
                        double nPow = v1.pow - v2.pow;

                        if (nFactor == 0) {
                            return new ValueNode(0);
                        } else if (nPow == 0) {
                            return new ValueNode(nFactor);
                        } else {
                            return new VarNode(v1.name, nFactor, nPow);
                        }
                    }
                }
            }

            if(sleft.toString().equals(sright.toString())){
                if(op == '+'){
                    return new OperationNode('*', new ValueNode(2), sleft.copy());
                }
                else if(op == '*'){
                    return new OperationNode('^', sleft.copy(), new ValueNode(2));
                }
            }
            else if(op == '+'){
                if(sleft instanceof ValueNode){
                    ValueNode valueNode = (ValueNode) sleft;
                    if(valueNode.value == 0) {
                        return sright;
                    }
                    else if(valueNode.value < 0){
                        return new OperationNode('-', sright, new ValueNode(-1 * valueNode.value));
                    }
                }
                else if(sright instanceof ValueNode){
                    ValueNode valueNode = (ValueNode) sright;
                    if(valueNode.value == 0) {
                        return sleft;
                    }
                    else if(valueNode.value < 0){
                        return new OperationNode('-', sleft, new ValueNode(-1 * valueNode.value));
                    }
                }
            }
            else if(op == '-'){
                if(sleft instanceof ValueNode){
                    ValueNode valueNode = (ValueNode) sleft;
                    if (valueNode.value == 0 && sright instanceof VarNode) {
                        VarNode varNode = (VarNode) sright;
                        return new VarNode(varNode.name, -1 * varNode.factor, varNode.pow);
                    }
                }

                if(sright instanceof ValueNode){
                    ValueNode valueNode = (ValueNode) sright;
                    if(valueNode.value == 0) {
                        return sleft;
                    }
                    else if(valueNode.value < 0){
                        return new OperationNode('+', sleft, new ValueNode(-1 * valueNode.value));
                    }
                }
            }
            else if(op == '*'){
                if(sleft instanceof ValueNode){
                    ValueNode valNode = (ValueNode) sleft;

                    if(valNode.value == 0){
                        return new ValueNode(0);
                    }
                    else if(valNode.value == 1){
                        return sright;
                    }
                    else if(sright instanceof VarNode){
                        VarNode varNode = (VarNode) sright;
                        double nFactor = operationNode.apply(varNode.factor, valNode.value);
                        return new VarNode(varNode.name, nFactor, varNode.pow);
                    }
                }
                else if(sright instanceof ValueNode){
                    ValueNode valNode = (ValueNode) sright;
                    if(valNode.value == 0){
                        return new ValueNode(0);
                    }
                    else if(valNode.value == 1){
                        return sleft;
                    }
                    else if(sleft instanceof VarNode){
                        VarNode varNode = (VarNode) sleft;
                        double nFactor = operationNode.apply(varNode.factor, valNode.value);
                        return new VarNode(varNode.name, nFactor, varNode.pow);
                    }
                }
            }
            else if(op == '/'){
                if(sleft instanceof ValueNode){
                    ValueNode valNode = (ValueNode) sleft;
                    if(valNode.value == 0){
                        return new ValueNode(0);
                    }
                    else {
                        return operationNode;
                    }
                }
                else if(sright instanceof ValueNode){
                    ValueNode valNode = (ValueNode) sright;
                    if(valNode.value == 0){
                        return new DivByZeroNode();
                    }
                    else if(valNode.value == 1){
                        return sleft;
                    }
                    else if(sleft instanceof VarNode){
                        VarNode varNode = (VarNode) sleft;
                        double nFactor = operationNode.apply(varNode.factor, valNode.value);
                        return new VarNode(varNode.name, nFactor, varNode.pow);
                    }
                    else {
                        return operationNode;
                    }
                }
            }
            else if(op == '^'){
                if(sleft instanceof VarNode && sright instanceof ValueNode){
                    VarNode v1 = (VarNode) sleft;
                    ValueNode v2 = (ValueNode) sright;

                    double nPow = v1.pow * v2.value;

                    if(nPow == 0){
                        return new ValueNode(1);
                    }

                    return new VarNode(v1.name, v1.factor, nPow);
                }
                else if(sleft instanceof ValueNode && sright instanceof VarNode){
                    ValueNode v1 = (ValueNode) sleft;
                    VarNode v2 = (VarNode) sright;

                    double nPow = v2.pow * v1.value;

                    if(nPow == 0){
                        return new ValueNode(1);
                    }

                    return new VarNode(v2.name, v2.factor, nPow);
                }
            }

            if(left == sleft && right == sright){
                return node;
            }
            else {
                return new OperationNode(op, sleft, sright);
            }
        }

        throw new IllegalStateException("UNEXPECTED STATE");
    }

    public Function getDerivative(){
        return getDerivative(1);
    }

    public Function getDerivative(int degree){
        FNode cur = getSimplified(function.rootNode);


        for(int i=0;i<degree;i++){
            cur = getDerivative(cur);
        }

        FNode simplified = getSimplified(cur);
        return new Function(simplified);
    }

    private FNode getDerivative(FNode node){
        if(node instanceof ValueNode){
            return new ValueNode(0);
        }
        else if(node instanceof VarNode){
            VarNode varNode = (VarNode) node;

            if(varNode.pow == 1){
                return new ValueNode(varNode.factor);
            }
            else {
                double nFactor = varNode.factor * varNode.pow;
                double nPow = varNode.pow - 1;

                return new VarNode(varNode.name, nFactor, nPow);
            }
        }
        else if(node instanceof OperationNode){
            OperationNode operationNode = (OperationNode) node;

            char op = operationNode.operationName;
            FNode left = operationNode.left;
            FNode right = operationNode.right;

            if(op == '+' || op == '-'){
                FNode dleft = getDerivative(left);
                FNode dright = getDerivative(right);
                return new OperationNode(op, dleft, dright);
            }
            else if(op == '*'){
                if(left instanceof ValueNode){
                    FNode dright = getDerivative(right);
                    return new OperationNode(op, left, dright);
                }
                else if(right instanceof ValueNode){
                    FNode dleft = getDerivative(left);
                    return new OperationNode(op, dleft, right);
                }
                else {
                    FNode dleft = getDerivative(left);
                    FNode dright = getDerivative(right);

                    FNode partLeft = new OperationNode('*', dleft, right);
                    FNode partRight = new OperationNode('*', left, dright);

                    return new OperationNode('+', partLeft, partRight);
                }
            }
            else if(op == '/'){
                if(left instanceof ValueNode){
                    FNode dright = getDerivative(right);
                    return new OperationNode(op, left, dright);
                }
                else if(right instanceof ValueNode){
                    FNode dleft = getDerivative(left);
                    return new OperationNode(op, dleft, right);
                }
                else {
                    FNode dleft = getDerivative(left);
                    FNode dright = getDerivative(right);

                    FNode partLeft = new OperationNode('*', dleft, right);
                    FNode partRight = new OperationNode('*', left, dright);

                    FNode partUpper = new OperationNode('-', partLeft, partRight);
                    FNode partLower = new OperationNode('*', right, right);

                    return new OperationNode('/', partUpper, partLower);
                }
            }
            else if(op == '^'){
                if(right instanceof ValueNode){
                    ValueNode valueNode = (ValueNode) right;

                    double nFactor = valueNode.value;
                    double nPow = valueNode.value - 1;

                    FNode inner = new OperationNode('^', left.copy(), new ValueNode(nPow));
                    return new OperationNode('*', new ValueNode(nFactor), inner);
                }
                //TODO...
            }
        }

        throw new IllegalStateException("UNEXPECTED STATE");
    }

}
