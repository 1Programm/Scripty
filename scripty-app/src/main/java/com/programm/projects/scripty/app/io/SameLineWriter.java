package com.programm.projects.scripty.app.io;

import com.programm.projects.ioutils.log.api.out.IOutput;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SameLineWriter {

    private final IOutput out;
    private int lastLineLength = -1;

    public void print(String msg){
        int len = msg.length();
        if(lastLineLength == -1){
            out.print(msg);
        }
        else {
            out.print("${back}" + msg + " ".repeat(Math.max(0, lastLineLength - len)));
        }

        lastLineLength = len;
    }

}
