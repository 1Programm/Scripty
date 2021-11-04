package com.programm.projects.scripty.app;

public class ScriptyApp {

    public static void main(String[] args) {
        String staticWorkspace = args[0];
        String syWorkspace = args[1];
        String userPath = args[2];
        String input = collectInput(args);

        Scripty scripty = new Scripty(syWorkspace);
        scripty.init();
        scripty.run(input);
    }

    private static String collectInput(String[] args){
        StringBuilder sb = new StringBuilder();

        for(int i=3;i<args.length;i++){
            if(sb.length() != 0){
                sb.append(" ");
            }

            sb.append(args[i]);
        }

        return sb.toString();
    }

}
