package com.programm.projects.scripty.module.test;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.ModuleFileConfig;
import com.programm.projects.scripty.module.test.func.Function;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.Module;
import com.programm.projects.scripty.modules.api.ScriptyContext;
import com.programm.projects.scripty.modules.api.ex.InvalidNameException;

import java.util.List;
import java.util.Scanner;

public class TestModule extends Module {

    @Override
    public void init(ScriptyContext context, ModuleFileConfig moduleConfig) {
        try {
            context.registerCommand("calc", this::math_calc);
        }
        catch (InvalidNameException e){
            context.err().println("Error registering command: " + e.getMessage());
        }
    }

    public void math_calc(ScriptyContext ctx, String name, Args args) throws CommandExecutionException {
        String rest = args.join();

        Function function = new Function(rest);
        ctx.out().println(function.toString());


        List<String> neededVars = function.getNeededVarNames();

        Scanner scanner = new Scanner(System.in);
        for(String var : neededVars){
            Double value = null;

            while(value == null) {
                ctx.out().print("What should [" + var + "] be: ");
                String in = scanner.nextLine();
                try {
                    value = Double.parseDouble(in);
                } catch (NumberFormatException e) {
                    ctx.err().println("Invalid number!");
                }
            }

            function.set(var, value);
        }
        scanner.close();


        double result = function.run();

        ctx.out().println("Result: " + result);
    }
}
