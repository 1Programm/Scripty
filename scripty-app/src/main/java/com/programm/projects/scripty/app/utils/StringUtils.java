package com.programm.projects.scripty.app.utils;

public class StringUtils {

    public static String substring(String str, int start, int end){
        int len = str.length();

        if(start < 0) start = len + start;
        if(end < 0) end = len + end;

        return str.substring(start, end);
    }

    public static String substring(String str, int start){
        return substring(str, start, str.length());
    }

}
