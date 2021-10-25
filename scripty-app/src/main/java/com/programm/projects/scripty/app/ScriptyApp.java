package com.programm.projects.scripty.app;

public class ScriptyApp {

    public static void main(String[] args) {
        String staticWorkspace = args[0];
        String syWorkspace = args[1];
        String userPath = args[2];

        Scripty scripty = new Scripty(syWorkspace);
        scripty.init();
    }

}
