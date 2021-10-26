package com.programm.projects.scripty.app.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;

public class ClassHelper {

    public static <T, E extends Throwable> T createInstanceFromEmptyConstructor(Class<T> cls, BiFunction<String, Throwable, E> exBuilder) throws E {
        try {
            return cls.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw exBuilder.apply("Class [" + cls.getName() + "] represents an abstract class and cannot be instantiated!", e);
        } catch (IllegalAccessException e) {
            throw exBuilder.apply("Empty constructor of class [" + cls.getName() + "] is private and cannot be accessed!", e);
        } catch (InvocationTargetException e) {
            throw exBuilder.apply("Exception in empty constructor of class [" + cls.getName() + "]!", e);
        } catch (NoSuchMethodException e) {
            throw exBuilder.apply("Cannot find empty constructor of class [" + cls.getName() + "]!", e);
        }
    }

}
