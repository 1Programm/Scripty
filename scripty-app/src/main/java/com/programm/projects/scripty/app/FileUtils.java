package com.programm.projects.scripty.app;

import java.io.File;
import java.io.IOException;

class FileUtils {

    public static void createDir(File dir, String errName) throws IOException {
        if(!dir.mkdirs()){
            throw new IOException("Failed to create [" + errName + "] at: [" + dir.getAbsolutePath() + "]!");
        }
    }

    public static void createFile(File file, String errName) throws IOException {
        if(!file.createNewFile()){
            throw new IOException("Failed to create [" + errName + "] at: [" + file.getAbsolutePath() + "]!");
        }
    }

    public static File getCreateDir(File parent, String path, String errName) throws IOException {
        File file = new File(parent, path);
        return getCreateDir(file, errName);
    }

    public static File getCreateDir(String path, String errName) throws IOException {
        File file = new File(path);
        return getCreateDir(file, errName);
    }

    public static File getCreateDir(File dir, String errName) throws IOException {
        if(!dir.exists()) createDir(dir, errName);
        return dir;
    }

    public static File getCreateFile(File parent, String path, String errName) throws IOException {
        File file = new File(parent, path);
        return getCreateFile(file, errName);
    }

    public static File getCreateFile(String path, String errName) throws IOException {
        File file = new File(path);
        return getCreateFile(file, errName);
    }

    public static File getCreateFile(File file, String errName) throws IOException {
        if(!file.exists()) createFile(file, errName);
        return file;
    }
}
