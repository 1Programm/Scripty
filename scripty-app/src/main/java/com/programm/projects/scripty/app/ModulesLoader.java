package com.programm.projects.scripty.app;

import com.programm.projects.scripty.modules.api.Module;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

class ModulesLoader {

    public static void loadRepos(File repoFile, List<String> repos) throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(repoFile)) {
            Object root_obj = parser.parse(reader);

            if(root_obj instanceof JSONArray){
                JSONArray root = (JSONArray)root_obj;

                for (Object o : root) {
                    String repo = o.toString();
                    repos.add(repo);
                }
            }
            else {
                throw new IOException("Repositories file is not an Array!");
            }
        }
    }

    public static void loadModules(List<String> repos, List<Module> modules) throws IOException, ParseException {

    }

}
