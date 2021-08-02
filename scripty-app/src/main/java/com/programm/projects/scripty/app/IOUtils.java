package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IInput;
import com.programm.projects.scripty.modules.api.SyIO;

class IOUtils {

    public static String areYouSureAnswer(SyIO io, String question){
        String answer;

        while(true){
            io.out().print(question);
            answer = io.in().next();
            io.out().print("Are you sure [" + answer + "]? [Y/n]: ");

            boolean sure = getYNAnswer(io.in(), true);

            if(sure){
                break;
            }
        }

        return answer;
    }

    public static String answerMatches(SyIO io, String question, String pattern, String errMessage){
        String answer;

        while(true){
            io.out().print(question);
            answer = io.in().next();

            if(answer.matches(pattern)){
                break;
            }
            else {
                if(errMessage == null) {
                    io.out().println("'" + answer + "' does not match pattern: [" + pattern + "]!");
                }
                else {
                    io.out().println(errMessage);
                }
            }
        }

        return answer;
    }

    public static boolean getYNAnswer(IInput in, boolean defaultYes){
        if(defaultYes){
            String answer = getAnswerOrDefault(in, "Y", "n", true);
            return !answer.equals("n");
        }
        else {
            String answer = getAnswerOrDefault(in, "y", "N", false);
            return answer.equals("y");
        }
    }

    public static String getAnswerOrDefault(IInput in, String a, String b, boolean aIsDefault){
        String answer = in.next();

        return aIsDefault ?
                (answer.equalsIgnoreCase(b) ? b : a) :
                (answer.equalsIgnoreCase(a) ? a : b);
    }

}
