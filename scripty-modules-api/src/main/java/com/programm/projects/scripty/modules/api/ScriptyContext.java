package com.programm.projects.scripty.modules.api;

import com.programm.projects.scripty.core.IOutput;

public interface ScriptyContext {

    String getModuleName();

    IOutput out();

}
