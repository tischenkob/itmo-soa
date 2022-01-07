package ru.ifmo.util;

import java.util.function.Function;

public class Nulls {

    public static <T, U> U map(T object, Function<T, U> function) {
        if (object == null) {return null;}
        return function.apply(object);
    }

}