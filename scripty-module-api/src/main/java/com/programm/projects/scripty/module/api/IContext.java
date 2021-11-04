package com.programm.projects.scripty.module.api;

import com.programm.projects.ioutils.log.api.in.IInput;
import com.programm.projects.ioutils.log.api.out.IOutput;

public interface IContext {

    IOutput log();
    IOutput out();
    IOutput err();
    IInput in();

    IWorkspace workspace();

}
