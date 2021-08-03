package com.programm.projects.scripty.module.test;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.module.test.func.Function;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.*;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.util.List;
import java.util.Scanner;

public class TestModule extends Module {

    @Override
    public void registerCommands(SyCommandManager commandManager) {
        try {
            commandManager.registerCommand("calc", this::math_calc);
        }
        catch (InvalidNameException e){
            err().println("Error registering command: " + e.getMessage());
        }
    }

    @Override
    public void init(SyContext context, SyModuleConfig moduleConfig) {
        log().println("Hello :D");
        log().println("At your service.");
    }

    public void math_calc(SyContext ctx, SyIO io, String name, Args args) throws CommandExecutionException {
        String rest = args.join();

        Function function = new Function(rest);
        io.out().println(function.toString());


        List<String> neededVars = function.getNeededVarNames();

        Scanner scanner = new Scanner(System.in);
        for(String var : neededVars){
            Double value = null;

            while(value == null) {
                io.out().print("What should [" + var + "] be: ");
                String in = scanner.nextLine();
                try {
                    value = Double.parseDouble(in);
                } catch (NumberFormatException e) {
                    io.err().println("Invalid number!");
                }
            }

            function.set(var, value);
        }
        scanner.close();


        double result = function.run();

        io.out().println("Result: " + result);
    }
}
