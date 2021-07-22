package com.programm.projects.scripty.module.test.func;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

class FunctionParser {

    static final Map<Character, BiFunction<Double, Double, Double>> operations = new HashMap<>();
    static final Map<String, FFunction> functions = new HashMap<>();

    public static double op_add(double a, double b){
        return a + b;
    }

    public static double op_sub(double a, double b){
        return a - b;
    }

    public static double op_mul(double a, double b){
        return a * b;
    }

    public static double op_div(double a, double b){
        return a / b;
    }

    public static double op_pow(double a, double b){
        return Math.pow(a, b);
    }

    public static final FFunction func_ln = new FFunction("ln", 1, Math::log);

    public static final FFunction func_exp = new FFunction("exp", 1, Math::exp);



    static {
        operations.put('+', FunctionParser::op_add);
        operations.put('-', FunctionParser::op_sub);
        operations.put('*', FunctionParser::op_mul);
        operations.put('/', FunctionParser::op_div);
        operations.put('^', FunctionParser::op_pow);

        functions.put("ln", func_ln);
        functions.put("exp", func_exp);
    }




    /*
     * # Split Keys:
     * | [OPERATIONS]
     * | +
     * | -
     * | *
     * | /
     * | ^
     * |
     * | [BRACKETS]
     * | (
     * | )
     * |
     * | [VARIABLES]
     * | [a-zA-Z][a-zA-Z0-9]*
     * |
     * | [NUMBER]
     * | [0-9]+\\.?[0-9]*
     */
    public static FNode parse(String text) throws FunctionParseException {
        text = text.trim();

        int nextOperation = getOperation(text);

        if(nextOperation == -1){
            if(text.charAt(0) == '(' && text.charAt(text.length() - 1) == ')'){
                text = text.substring(1, text.length() - 1);
                nextOperation = getOperation(text);
            }
        }


        if(nextOperation == -1){
            if(isVariable(text)){
                return new VarNode(text, 1, 1);
            }
            else {
                Double num = getNumber(text);

                if(num != null){
                    return new ValueNode(num);
                }
                else {
                    int scaledVar = getScaledVar(text);

                    if(scaledVar != -1){
                        String _scale = text.substring(0, scaledVar);
                        double scale = Double.parseDouble(_scale);
                        String _rest = text.substring(scaledVar);

                        FNode rest = parse(_rest);

                        if(rest instanceof VarNode){
                            VarNode varNode = (VarNode) rest;
                            return new VarNode(varNode.name, varNode.factor * scale, varNode.pow);
                        }
                        else {
                            return new OperationNode('*', new ValueNode(scale), rest);
                        }
                    }
                    else {
                        //Check functions

                        int nextOpenBracket = text.indexOf('(');

                        if (nextOpenBracket == -1) {
                            throw new FunctionParseException("Invalid function definition: [" + text + "]");
                        }

                        String name = text.substring(0, nextOpenBracket).trim();

                        FFunction function = functions.get(name);

                        if (function == null) {
                            throw new FunctionParseException("No such function: [" + name + "]");
                        }

                        int nextClosingBracket = StringUtils.findClosing(text, nextOpenBracket + 1, "(", ")");

                        if (nextClosingBracket == -1) {
                            throw new FunctionParseException("No closing function - bracket!");
                        }

                        String content = text.substring(nextOpenBracket + 1, nextClosingBracket);
                        FNode contentNode = parse(content);

                        return new FunctionNode(function, contentNode);
                    }
                }
            }
        }
        else{
            String left = text.substring(0, nextOperation);
            String right = text.substring(nextOperation + 1);
            char operation = text.charAt(nextOperation);

            if(right.isBlank()){
                throw new FunctionParseException("Right of operation [" + operation + "] cannot be left blanc!");
            }

            if(left.isBlank()){
                if(operation == '-'){
                    FNode rightNode = parse(right);

                    if(rightNode instanceof ValueNode){
                        ValueNode valueNode = (ValueNode) rightNode;
                        return new ValueNode(-1 * valueNode.value);
                    }
                    else if(rightNode instanceof VarNode){
                        VarNode varNode = (VarNode) rightNode;
                        return new VarNode(varNode.name, -1 * varNode.factor, varNode.pow);
                    }
                    else {
                        return new OperationNode('-', new ValueNode(0), rightNode);
                    }
                }
                else {
                    throw new FunctionParseException("Left of operation [" + operation + "] cannot be left blanc!");
                }
            }



            FNode leftNode = parse(left);
            FNode rightNode = parse(right);

            return new OperationNode(operation, leftNode, rightNode);
        }

//        throw new FunctionParseException("Could not parse: [" + text + "]");
    }

    private static int getOperation(String text){
        int index = -1;
        int level = 0;

        int bracketCount = 0;

        for(int i=text.length() - 1;i>=0;i--){
            char c = text.charAt(i);

            if(c == '('){
                bracketCount++;
            }
            else if(c == ')'){
                bracketCount--;
            }
            else if(bracketCount == 0){
                if(index == -1){
                    if(c == '+' || c == '-'){
                        index = i;
                        level = 1;
                    }
                    else if(c == '*' || c == '/' || c == '^'){
                        index = i;
                        level = 2;
                    }
                }
                else if(level == 2){
                    if(c == '+' || c == '-'){
                        index = i;
                        level = 1;
                    }
                }
            }
        }

        return index;
    }

    private static boolean isVariable(String text){
        return text.matches("[a-zA-Z][a-zA-Z0-9]*");
    }

    private static Double getNumber(String text){
        try{
            return Double.parseDouble(text);
        }
        catch (NumberFormatException e){
            return null;
        }
    }

    private static int getScaledVar(String text){
        boolean valid = false;

        for(int i=0;i<text.length();i++){
            char c = text.charAt(i);

            if(Character.isDigit(c)){
                valid = true;
            }
            else if(valid){
                return i;
            }
            else {
                return -1;
            }
        }

        return -1;
    }

}
