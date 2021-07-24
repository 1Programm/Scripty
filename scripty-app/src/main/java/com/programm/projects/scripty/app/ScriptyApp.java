package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.Args;

public class ScriptyApp {

    /*
     * Possible args:
     * ([workspace-path] is always 1st)
     * ([user-path] is always 2nd)
     *
     * Optionals for Scripty:
     *
     * sy [options-for-scripty] [command] [options-for-command]
     *
     */

    public static void main(String[] _args) {
        Args args = new Args(_args);
        String workspace = args.get(0);
        String userPath = args.get(1);
        args = args.sub(2);

        Scripty scripty = new Scripty();
        scripty.init(workspace, userPath);
        scripty.run(args);
    }

}
