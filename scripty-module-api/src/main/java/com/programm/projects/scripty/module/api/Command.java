package com.programm.projects.scripty.module.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String CMD_CLASS_NAME = "CMD_NAME";

    //name
    String value() default CMD_CLASS_NAME;

}
