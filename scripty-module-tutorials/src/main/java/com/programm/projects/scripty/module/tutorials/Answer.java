package com.programm.projects.scripty.module.tutorials;

import com.programm.projects.scripty.modules.api.SyContext;
import com.programm.projects.scripty.modules.api.SyIO;

public interface Answer {
    boolean answer(SyContext ctx, SyIO io);
}