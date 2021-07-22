package com.programm.projects.scripty.core;

import java.util.*;
import java.util.function.Predicate;

public class Args implements Iterable<String>{

    public static Args OfInput(String args){
        String[] split = StringUtils.advancedSplit(args, " ", "\"", "\"", "'", "'");
        for(int i=0;i<split.length;i++){
            String arg = split[i];

            if((arg.startsWith("\"") && arg.endsWith("\"")) || (arg.startsWith("'") && arg.endsWith("'"))){
                split[i] = arg.substring(1, arg.length() - 1);
            }
        }

        return new Args(split);
    }

    private final String[] args;

    public Args(Collection<String> args){
        this(args.toArray(new String[0]));
    }

    public Args(String... args) {
        this.args = args;
    }




    // ITERABLE

    @Override
    public Iterator<String> iterator() {
        return list().iterator();
    }


    // OPERATIONS

    public Args withoutOptionals(){
        return collect(s -> !s.startsWith("-"));
    }

    public Args optionals(){
        return collect(s -> s.startsWith("-"));
    }

    public Args collect(Predicate<String> predicate){
        List<String> nArgs = new ArrayList<>();

        for(String arg : args){
            if(predicate.test(arg)){
                nArgs.add(arg);
            }
        }

        return new Args(nArgs);
    }

    public Args sub(int start){
        return sub(start, size());
    }

    public Args sub(int start, int end){
        if(start < 0) throw new IndexOutOfBoundsException("start: [" + start + "] is below 0!");
        if(end < 0) throw new IndexOutOfBoundsException("end: [" + end + "] is below 0!");
        if(start > size()) throw new IndexOutOfBoundsException("start: [" + start + "] is greater than size(" + size() + ")!");
        if(end > size()) throw new IndexOutOfBoundsException("end: [" + end + "] is greater than size(" + size() + ")!");

        String[] nArgs = new String[end - start];

        System.arraycopy(args, start, nArgs, 0, nArgs.length);

        return new Args(nArgs);
    }

    public String join(){
        return join(" ");
    }

    public String join(String delimiter){
        StringBuilder sb = new StringBuilder();

        for(String arg : args){
            if(sb.length() != 0){
                sb.append(delimiter);
            }

            sb.append(arg);
        }

        return sb.toString();
    }

    private void testBounds(int i, String varName){
        if(i < 0) throw new IndexOutOfBoundsException(varName + ": [" + i + "] is below 0!");
        if(i >= size()) throw new IndexOutOfBoundsException(varName + ": [" + i + "] is greater than size(" + size() + ")!");
    }




    // GETTERS

    public int size(){
        return args.length;
    }

    public String get(int i){
        testBounds(i, "i");
        return args[i];
    }

    public boolean contains(String v){
        for(String arg : args){
            if(arg.equals(v)) return true;
        }

        return false;
    }

    public int indexOf(String arg){
        for(int i=0;i<args.length;i++){
            if(args[i].equals(arg)) return i;
        }

        return -1;
    }

    public boolean is(int i, String arg) {
        testBounds(i, "i");
        return args[i].equals(arg);
    }

    public String[] array(){
        return Arrays.copyOf(args, args.length);
    }

    public List<String> list(){
        return Arrays.asList(args);
    }

    public String nextNonOptional(){
        for(String arg : args){
            if(!arg.startsWith("-")){
                return arg;
            }
        }

        return null;
    }




    // OVERRIDES


    @Override
    public String toString() {
        return Arrays.toString(args);
    }
}
