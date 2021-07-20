package com.programm.projects.scripty.core.json;

import java.util.Set;

class JNValue implements JNode {

    private final String value;

    public JNValue(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public JNode get(String name) {
        return null;
    }

    @Override
    public String toString() {
        return value();
    }
}
