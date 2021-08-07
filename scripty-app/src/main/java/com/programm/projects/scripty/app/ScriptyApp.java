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
        String installationPath = args.get(0);
        String workspace = args.get(1);
        String userPath = args.get(2);
        args = args.sub(3);

        Scripty scripty = new Scripty();
        scripty.init(installationPath, workspace, userPath);
        scripty.run(args);
    }

}
