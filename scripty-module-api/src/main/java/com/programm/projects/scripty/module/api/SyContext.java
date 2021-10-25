package com.programm.projects.scripty.module.api;

import com.programm.projects.scripty.module.api.io.IInput;
import com.programm.projects.scripty.module.api.io.IOutput;

public interface SyContext {

    IOutput log();
    IOutput out();
    IOutput err();
    IInput in();

}
