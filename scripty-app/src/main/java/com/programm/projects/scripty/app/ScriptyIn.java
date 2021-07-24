package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IInput;

import java.util.Scanner;

public class ScriptyIn implements IInput {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public String next() {
        return scanner.nextLine();
    }
}
