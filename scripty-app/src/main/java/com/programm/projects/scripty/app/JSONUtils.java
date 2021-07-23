package com.programm.projects.scripty.app;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

class JSONUtils {

    public static Object readJsonFromUrl(String url) throws IOException, ParseException {
        URL theUrl = new URL(url);
        URLConnection connection = theUrl.openConnection();
        InputStream in = connection.getInputStream();

        return readJson(in);
    }

    public static Object readJsonFromPath(String path) throws IOException, ParseException {
        return readJson(new FileInputStream(path));
    }

    public static Object readJsonFromFile(File file) throws IOException, ParseException {
        return readJson(new FileInputStream(file));
    }

    public static Object readJson(InputStream in) throws IOException, ParseException{
        JSONParser parser = new JSONParser();
        return parser.parse(new InputStreamReader(in));
    }

}
