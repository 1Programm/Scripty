package com.programm.projects.scripty.app;

import com.programm.projects.scripty.core.IOutput;
import com.programm.projects.scripty.core.PrependOut;
import com.programm.projects.scripty.modules.api.SyIO;

public class ModuleIO implements SyIO {

    private final PrependOut out = new PrependOut(new ScriptyOut(System.out, true));
    private final PrependOut log = new PrependOut(new ScriptyOut(System.out, false));
    private final PrependOut err = new PrependOut(new ScriptyOut(System.err, true));

    public void setModuleName(String moduleName){
        this.out.setPrepend(moduleName + ": ");
        this.log.setPrepend(moduleName + ": ");
        this.err.setPrepend(moduleName + ": ");
    }

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
        log.enable(true);
    }
}
