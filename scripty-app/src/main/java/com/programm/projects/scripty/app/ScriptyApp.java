package com.programm.projects.scripty.app;

import com.programm.projects.scripty.app.io.InteractiveInput;

public class ScriptyApp {

    public static void main(String[] args) {
        String staticWorkspace = args[0];
        String syWorkspace = args[1];
        String userPath = args[2];
        String _input = collectInput(args);

        Scripty scripty = new Scripty(syWorkspace);
        scripty.init();

        if(!_input.equals("")){
            scripty.run(_input);
            return;
        }

        scripty.out().print("${clear}");
        scripty.out().println("%20|[-]([${yellow}(Scripty)])");
        InteractiveInput input = new InteractiveInput();

        String in;
        while(!(in = input.next()).equals("q") && !in.equals("quit")){
            scripty.run(in);
        }

        try {
            input.close();
        } catch (Exception e) {
            throw new IllegalStateException("Could not close interactive input.", e);
        }
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
