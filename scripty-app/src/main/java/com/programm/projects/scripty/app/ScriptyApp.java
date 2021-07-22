package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;

public class ScriptyApp {

    /*
     * Possible args:
     * ([workspace-path] is always 1st)
     *
     * COMMANDS ALWAYS 1st and then the optionals
     *
     */

    public static void main(String[] _args) {
        Args args = new Args(_args);
        String workspace = args.get(0);
        args = args.sub(1);


        if(args.size() == 0){
            System.out.println("No command specified!");
            System.exit(-1);
        }

        String command = args.get(0);

        Scripty scripty = new Scripty();
        scripty.init(workspace);
        scripty.run(command, args.sub(1));
    }

}
