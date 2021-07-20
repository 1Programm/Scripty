package com.programm.projects.scripty.core.json;

import java.util.Map;

class JNMap implements JNode {

    private String value;
    private final Map<String, JNode> map;

    public JNMap(Map<String, JNode> map) {
        this.map = map;
    }

    @Override
    public String value() {
        if(value == null){
            StringBuilder sb = new StringBuilder();

            for(String name : map.keySet()){
                String cValue = map.get(name).value();
                sb.append("\"").append(name).append("\": ").append(cValue);
            }

            value = sb.toString();
        }

        return value;
    }

    @Override
    public JNode get(String name) {
        return map.get(name);
    }

    @Override
    public String toString() {
        return value();
    }
}
