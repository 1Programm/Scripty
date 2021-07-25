package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IInput;

class IOUtils {

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
