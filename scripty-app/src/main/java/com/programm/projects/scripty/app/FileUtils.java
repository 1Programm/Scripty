package com.programm.projects.scripty.app;

import java.io.File;

class FileUtils {

    public static File getCreateDir(File parent, String path) throws Exception {
        File file = new File(parent, path);
        return getCreateDir(file);
    }

    public static File getCreateDir(String path) throws Exception {
        File file = new File(path);
        return getCreateDir(file);
    }

    private static File getCreateDir(File file) throws Exception {
        if(!file.exists()){
            if(!file.mkdirs()){
                throw new Exception("Failed to create Directory [" + file.getAbsolutePath() + "]!");
            }
        }

        return file;
    }

    public static File getCreateFile(File parent, String path) throws Exception {
        File file = new File(parent, path);
        return getCreateFile(file);
    }

    public static File getCreateFile(String path) throws Exception {
        File file = new File(path);
        return getCreateFile(file);
    }

    private static File getCreateFile(File file) throws Exception {
        if(!file.exists()){
            if(!file.createNewFile()){
                throw new Exception("Failed to create File [" + file.getAbsolutePath() + "]!");
            }
        }

        return file;
    }
}
