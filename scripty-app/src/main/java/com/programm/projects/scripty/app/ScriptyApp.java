package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;

public class ScriptyApp {

    public static void main(String[] _args) {
        Args args = new Args(_args);
        String workspace = args.get(0);
        args = args.sub(1);


        Args nOptArgs = args.withoutOptionals();
        if(nOptArgs.size() == 0){
            System.out.println("No command specified!");
            System.exit(-1);
        }

        String command = nOptArgs.get(0);
        int index = args.indexOf(command);

        if(index > 0){
            System.out.println("Invalid optionals before command: " + args.sub(0, index));
            System.exit(-1);
        }

        Scripty scripty = new Scripty();
        scripty.init(workspace);
        scripty.run(command, args.sub(index));
    }

}
