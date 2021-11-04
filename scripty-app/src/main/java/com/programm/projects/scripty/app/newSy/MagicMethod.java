package com.programm.projects.scripty.app.newSy;

import com.programm.projects.scripty.module.api.events.Get;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class MagicMethod {

    private final Method method;
    private final Object instance;
    private final BiFunction<Class<?>, Get, Object> getFunc;

    public Object run(Object... args){
        int goalLength = method.getParameterCount();
        Object[] actualArgs = new Object[goalLength];

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] types = method.getParameterTypes();

        int c = 0;
        for(int i=0;i<goalLength;i++){
            Annotation[] annotations = parameterAnnotations[i];
            Class<?> type = types[i];

            Get getAnnotation = null;

            for(Annotation annotation : annotations){
                if(annotation instanceof Get){
                    getAnnotation = (Get)annotation;
                    break;
                }
            }

            if(getAnnotation == null){
                if(c >= args.length) {
                    //TODO throw something
                    System.err.println("Not enough args!");
                }
                actualArgs[i] = type.cast(args[c]);
                c++;
            }
            else {
                Object obj = getFunc.apply(type, getAnnotation);
                actualArgs[i] = obj;
            }
        }

        try {
            return method.invoke(instance, actualArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

}
