package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.modules.api.SyIO;

class ScriptyIO implements SyIO {

    private final ScriptyOut out = new ScriptyOut(System.out, true);
    private final ScriptyOut log = new ScriptyOut(System.out, false);
    private final ScriptyOut err = new ScriptyOut(System.err, true);

    @Override
    public IOutput out() {
        return out;
    }

    @Override
    public IOutput log() {
        return log;
    }

    @Override
    public IOutput err() {
        return err;
    }

    public void enableLog(){
        log.enable();
    }
}
