package com.programm.projects.scripty.module.api;

public interface IStore {

    void save(String key, Object value) throws StoreException;

    <T> T load(String key, Class<T> cls);

    default String load(String key){
        Object obj = load(key, Object.class);
        if(obj == null) return null;
        return obj.toString();
    }
}
