package com.javacorrige.util.reflection.element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementExtractor {

    private ElementExtractor() {

    }

    public static List<Object> extractElements(Class<?> clazz) {
        List<Object> elements = new ArrayList<>();
        if (clazz != null) {
            elements.addAll(List.of(clazz.getDeclaredFields()));
            elements.addAll(List.of(clazz.getDeclaredMethods()));
            elements.addAll(List.of(clazz.getDeclaredConstructors()));
        }
        return elements;
    }

    public static List<Field> extractFields(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    public static List<Method> extractMethods(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredMethods());
    }

    public static List<Constructor<?>> extractConstructors(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredConstructors());
    }
}
