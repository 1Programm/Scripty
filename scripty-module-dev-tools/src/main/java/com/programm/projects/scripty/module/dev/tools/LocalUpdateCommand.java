package com.programm.projects.scripty.module.dev.tools;

import com.programm.projects.plugz.magic.Get;
import com.programm.projects.plugz.magic.PostSetup;
import com.programm.projects.scripty.module.api.Command;
import com.programm.projects.scripty.module.api.IContext;
import com.programm.projects.scripty.module.api.IStore;
import com.programm.projects.scripty.module.api.StoreException;

@Command
public class LocalUpdateCommand {

    private static final String KEY_LOCAL_PATH = "CMD_LOCAL_UPDATE_LOCAL_PATH";

    @Get
    private IContext ctx;

    private IStore store;

    @PostSetup
    public void init() throws StoreException {
        store = ctx.workspace().store(DevtoolsConfig.DEV_TOOLS_STORE_NAME);
    }

    @Command
    public void run(String name, String input) throws StoreException {
        if(input.startsWith("config ")){
            String val = input.substring("config ".length());
            store.save(KEY_LOCAL_PATH, val);
            ctx.out().println("Configured the local dev project.");
        }
        else {
            String localPath = store.load(KEY_LOCAL_PATH);

            if(localPath == null){
                throw new RuntimeException("Local installation path is not set up!");
            }

            ctx.out().println("Local Path: {}", localPath);
        }
    }

}
