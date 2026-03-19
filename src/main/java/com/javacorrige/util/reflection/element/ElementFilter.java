package com.javacorrige.util.reflection.element;

import java.util.ArrayList;
import java.util.List;

import com.javacorrige.model.result.correction.SpecificationElement;
import com.javacorrige.model.result.correction.exercise.clazz.specification.ConstructorCorrection;
import com.javacorrige.model.result.correction.exercise.clazz.specification.FieldCorrection;
import com.javacorrige.model.result.correction.exercise.clazz.specification.MethodCorrection;

public class ElementFilter {

    public enum ElementType {
        CONSTRUCTOR, FIELD, METHOD
    }

    public static List<SpecificationElement<?>> getElementsByType(List<SpecificationElement<?>> elements,
            ElementType type) {
        List<SpecificationElement<?>> selectedElements = new ArrayList<>();

        for (SpecificationElement<?> element : elements) {
            switch (type) {
                case CONSTRUCTOR:
                    if (element instanceof ConstructorCorrection) {
                        selectedElements.add(element);
                    }
                    break;
                case FIELD:
                    if (element instanceof FieldCorrection) {
                        selectedElements.add(element);
                    }
                    break;
                case METHOD:
                    if (element instanceof MethodCorrection) {
                        selectedElements.add(element);
                    }
                    break;
            }
        }

        return selectedElements;
    }
}
