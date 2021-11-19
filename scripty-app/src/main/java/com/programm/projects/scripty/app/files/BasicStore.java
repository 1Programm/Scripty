package com.programm.projects.scripty.app.files;

import com.programm.projects.scripty.module.api.IStore;
import com.programm.projects.scripty.module.api.StoreException;
import lombok.RequiredArgsConstructor;

import java.io.*;
import java.util.*;

@RequiredArgsConstructor
public class BasicStore implements IStore {

    private static class StoreEntry {
        Object value;
        Class<?> type;
    }

    private final File storeFile;
    private final Map<String, StoreEntry> entries = new HashMap<>();

    public void initialLoad() throws WorkspaceException{
        if(!storeFile.exists() || !storeFile.isFile()){
            try {
                SyWorkspace.ensureFolders(storeFile.getParentFile().getAbsolutePath());
            }
            catch (IOException e){
                throw new WorkspaceException("Could not create directories for store file at: [" + storeFile.getAbsolutePath() + "].", e);
            }

            try {
                if(!storeFile.createNewFile()){
                    throw new WorkspaceException("Failed to create new Store file at: [" + storeFile.getAbsolutePath() + "].");
                }
            } catch (IOException e) {
                throw new WorkspaceException("Could not create new Store file at: [" + storeFile.getAbsolutePath() + "].");
            }
        }
        else {
            try (BufferedReader br = new BufferedReader(new FileReader(storeFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    StoreEntry entry = new StoreEntry();
                    List<String> list = retrieveSplitDoubleColons(line);

                    if (list.size() != 3) {
                        throw new WorkspaceException("Invalid entry: [" + line + "] in store: [" + storeFile.getAbsolutePath() + "].");
                    }

                    String _key = list.get(0);
                    String _value = list.get(1);
                    String _type = list.get(2);

                    if (_type.equals("String")) {
                        entry.value = _value;
                        entry.type = String.class;
                    } else if (_type.equals("Integer")) {
                        entry.value = Integer.parseInt(_value);
                        entry.type = Integer.class;
                    } else if (_type.equals("Float")) {
                        entry.value = Float.parseFloat(_value);
                        entry.type = Float.class;
                    } else if (_type.equals("Double")) {
                        entry.value = Double.parseDouble(_value);
                        entry.type = Double.class;
                    } else if (_type.equals("Boolean")) {
                        entry.value = Boolean.parseBoolean(_value);
                        entry.type = Boolean.class;
                    } else {
                        throw new WorkspaceException("Unsupported type [" + _type + "] defined in Store file: [" + storeFile.getAbsolutePath() + "].");
                    }

                    entries.put(_key, entry);
                }
            } catch (IOException e) {
                throw new WorkspaceException("Could not read store file: [" + storeFile.getAbsolutePath() + "].", e);
            }
        }
    }

    @Override
    public void save(String key, Object value) throws StoreException {
        StoreEntry entry = new StoreEntry();

        Class<?> type = value.getClass();

        if(type == String.class || type == Integer.class || type == Float.class || type == Double.class || type == Boolean.class){
            entry.type = type;
        }
        else {
            entry.type = String.class;
        }

        entry.value = value.toString();
        entries.put(key, entry);

        saveToStoreFile();
    }

    @Override
    public <T> T load(String key, Class<T> cls) {
        StoreEntry entry = entries.get(key);

        if(entry == null) return null;
        if(cls == Object.class) return cls.cast(entry.value);
        if(cls == entry.type) return cls.cast(entry.value);

        if(entry.type == String.class){
            String _value = entry.value.toString();
            if(cls == Integer.class){
                return cls.cast(Integer.parseInt(_value));
            }
            else if(cls == Float.class){
                return cls.cast(Float.parseFloat(_value));
            }
            else if(cls == Double.class){
                return cls.cast(Double.parseDouble(_value));
            }
            else if(cls == Boolean.class){
                return cls.cast(Boolean.parseBoolean(_value));
            }
        }

        throw new IllegalArgumentException("Could not find propper mapping for: [" + entry.type.getSimpleName() + " -> " + cls.getSimpleName() + "].");
    }

    private void saveToStoreFile() throws StoreException {
        List<String> lines = new ArrayList<>();

        for(String key : entries.keySet()){
            StoreEntry entry = entries.get(key);
            String _entry = concatWithColons(Arrays.asList(key, entry.value.toString(), entry.type.getSimpleName()));
            lines.add(_entry);
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(storeFile))){
            for(String line : lines){
                bw.write(line);
                bw.newLine();
            }
        }
        catch (IOException e){
            throw new StoreException("Could not write to store file: [" + storeFile.getAbsolutePath() + "].", e);
        }
    }

    private String concatWithColons(List<String> list){
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<list.size();i++){
            if(i != 0){
                sb.append(";");
            }
            String _item = list.get(i).replaceAll(";", ";;");
            sb.append(_item);
        }

        return sb.toString();
    }

    private List<String> retrieveSplitDoubleColons(String s){
        List<String> list = new ArrayList<>();

        int last = 0;
        int i;
        StringBuilder sb = new StringBuilder();

        while((i = s.indexOf(';', last)) != -1){
            if(i + 1 < s.length() && s.charAt(i + 1) == ';'){
                sb.append(s, last, i + 1);
                last = i + 2;
            }
            else {
                sb.append(s, last, i);
                list.add(sb.toString());
                sb.setLength(0);
                last = i + 1;
            }
        }

        if(last < s.length()){
            sb.append(s.substring(last));
            list.add(sb.toString());
        }

        return list;
    }
}
