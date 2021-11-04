package com.programm.projects.scripty.app.newSy;

import com.programm.projects.scripty.module.api.commands.ICommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CommandImpl implements ICommand {

    private final String name;
    private final MagicMethod method;

    @Override
    public void run(String... args) {
        if(args.length == 0){
            Object[] oArr = new Object[1];
            oArr[0] = args;
            method.run(oArr);
        }
        else {
            method.run((Object[]) args);
        }
    }

    @Override
    public String name() {
        return name;
    }
}
