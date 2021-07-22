package com.programm.projects.scripty.module.test.func;

import java.util.List;

interface FNode {

    double run(FContext context);

    void collectVars(List<String> names);

    FNode copy();

}
