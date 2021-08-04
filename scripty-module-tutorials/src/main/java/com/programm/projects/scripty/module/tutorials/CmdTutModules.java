package com.programm.projects.scripty.module.tutorials;

import com.programm.projects.scripty.core.Args;
import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.CommandExecutionException;
import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

import java.util.HashMap;
import java.util.Map;

public class CmdTutModules implements TutCommand {

    @Override
    public void run(SyContext ctx, SyIO io, String commandName, Args args) throws CommandExecutionException {
        io.out().println("Welcome to the tutorial about [modules].");
        io.out().newLine();

        String[] questions = {
                "What are modules ?",
                "How can i see installed modules ?",
                "How can i add a module ?",
                "How can i remove a module ?",
                "How can i create a custom module ?"
        };

        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, this::explainModules);
        answers.put(2, this::explainListModules);
        answers.put(3, this::explainAddModules);
        answers.put(4, this::explainRemoveModules);
        answers.put(5, this::explainDevModules);

        while(true) {
            for(int i=0;i<questions.length;i++){
                io.out().println("[" + (i+1) + "] | " + questions[i]);
            }
            io.out().newLine();

            io.out().print("What do you want to know (1 - " + questions.length + ")? [Enter to exit]: ");

            String answer = io.in().next();

            if(answer.isBlank()){
                break;
            }

            try {
                int i_answer = Integer.parseInt(answer);

                if(i_answer < 1 || i_answer > questions.length) {
                    throw new NumberFormatException();
                }

                io.out().newLine();
                String question = questions[i_answer-1];
                io.out().println(" " + question);
                io.out().println("-" + "-".repeat(question.length()) + "-");

                boolean shouldReturn = answers.get(i_answer).answer(ctx, io);
                if(shouldReturn){
                    return;
                }
            }
            catch (NumberFormatException e){
                io.err().println("'" + answer + "' is not a number between 1 - 5!");
                io.out().print("[Press enter to continue]");
                io.in().next();
                io.out().newLine();
            }
        }
    }

    private boolean explainModules(SyContext ctx, SyIO io) {
        io.out().println("Modules are plugins for the scripty-engine.");
        io.out().println("A module can register new commands to the scripty context so when you install that module you will have access to these new 'commands'.");
        io.out().println("A module is nothing more that a class file which extends a Module-class from the scripty-modules-api and a config file (sy.module).");
        io.out().newLine();

        io.out().print("[Press enter to continue]");
        io.in().next();
        io.out().newLine();

        return false;
    }

    private boolean explainListModules(SyContext ctx, SyIO io) {
        io.out().println("A 'built-in' command 'modules-list' exists to see all installed modules.");
        io.out().newLine();

        io.out().println("Try that command by entering 'sy modules-list'! [Or hit enter if you dont want to]");

        while(true) {
            io.out().print("> ");
            String answer = io.in().next();

            if(answer.isBlank()){
                io.out().newLine();
                return false;
            }

            if(answer.equals("sy modules-list") || answer.startsWith("sy modules-list ")){
                String rest = answer.substring("sy modules-list".length());
                rest = rest.trim();

                Args args = Args.OfInput(rest);
                try {
                    ctx.run("modules-list", args);
                } catch (CommandExecutionException e) {
                    io.err().print("Error running command [modules-list]: " + e.getMessage());
                }
                break;
            }
            else {
                io.err().println("Try again: 'sy modules-list' [Or hit enter if you want to skip]");
            }
        }

        io.out().newLine();

        io.out().print("[Press enter to continue]");
        io.in().next();
        io.out().newLine();

        return false;
    }

    private boolean explainAddModules(SyContext ctx, SyIO io) {
        io.out().println("To add a module you can use the command 'modules-add [name]'.");
        io.out().println("Scripty will go through all specified repositories and will search inside each for the [name].");
        io.out().println("If it successfully found a module with that name scripty will then download that module and include it into scripty.");
        io.out().newLine();

        io.out().println("Try that command with the simple module 'hello-world'.");
        io.out().println("Type 'sy modules-add hello-world'. [Or hit enter if you want to skip]");

        while(true){
            io.out().print("> ");
            String answer = io.in().next();

            if(answer.isBlank()){
                io.out().newLine();
                return false;
            }

            if(answer.equals("sy modules-add hello-world")) {

                try {
                    ctx.run("modules-add", "hello-world");
                } catch (CommandExecutionException e) {
                    io.err().print("Error running command [modules-add]: " + e.getMessage());
                }
                break;
            }
            else {
                io.err().println("Try again: 'sy modules-add' [Or hit enter if you want to skip]");
            }
        }

        io.out().newLine();

        io.out().print("[Press enter to continue]");
        io.in().next();
        io.out().newLine();

        return false;
    }

    private boolean explainRemoveModules(SyContext ctx, SyIO io) {
        io.out().println("To remove a module you can use the command 'modules-remove [name]'.");
        io.out().newLine();

        io.out().println("Try that command if you have the module 'hello-world' installed.");
        io.out().println("Type 'sy modules-remove hello-world'. [Or hit enter if you want to skip]");

        while(true){
            io.out().print("> ");
            String answer = io.in().next();

            if(answer.isBlank()){
                io.out().newLine();
                return false;
            }

            if(answer.equals("sy modules-remove hello-world")) {
                try {
                    ctx.run("modules-remove", "hello-world");
                } catch (CommandExecutionException e) {
                    io.err().print("Error running command [modules-remove]: " + e.getMessage());
                }
                break;
            }
            else {
                io.err().println("Try again: 'sy modules-remove' [Or hit enter if you want to skip]");
            }
        }

        io.out().newLine();

        io.out().print("[Press enter to continue]");
        io.in().next();
        io.out().newLine();

        return false;
    }

    private boolean explainDevModules(SyContext ctx, SyIO io) {
        io.out().println("For that topic a seperate tutorial 'tut-dev' has been created.");
        io.out().print("Do you want to jump into that tutorial? [y/N]: ");

        String answer = io.in().next();

        if(!answer.equals("y")){
            return false;
        }

        try {
            ctx.run("tut-dev");
        } catch (CommandExecutionException e) {
            io.err().println("[tut-dev]: " + e.getMessage());
        }

        return true;
    }

    @Override
    public void printHelp(IOutput out, String commandName) {
        out.println("Try it (sy " + commandName + "). It will explain how to list, add and remove modules!");
    }
}
