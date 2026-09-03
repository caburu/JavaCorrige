package com.javacorrige.model.result.correction.exercise.clazz;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.javacorrige.model.result.correction.SpecificationElement;
import com.javacorrige.model.result.correction.exercise.clazz.specification.ConstructorCorrection;
import com.javacorrige.model.result.correction.exercise.clazz.specification.FieldCorrection;
import com.javacorrige.model.result.correction.exercise.clazz.specification.MethodCorrection;
import com.javacorrige.util.reflection.element.ElementMapper;
import com.javacorrige.util.reflection.element.ElementUtils;

public class ClassCorrection {
    private final Class<?> template;
    private final Class<?> student;

    private final List<SpecificationElement<?>> elements;

    public ClassCorrection(Class<?> template, Class<?> student) {
        this.template = template;
        this.student = student;

        this.elements = initializeElements(template, student);
    }

    private List<SpecificationElement<?>> initializeElements(Class<?> template, Class<?> student) {
        List<SpecificationElement<?>> elementList = new ArrayList<>();

        if (template == null || student == null) {
            return elementList;
        }

        HashMap<Object, Object> mappedElements = ElementMapper.mapElements(template, student);

        mappedElements.forEach((templateElement, studentElement) -> {
            if (templateElement instanceof Field || studentElement instanceof Field) {
                elementList.add(new FieldCorrection(
                        (Field) templateElement,
                        (Field) studentElement,
                        allFieldsPrivate()));
            } else if (templateElement instanceof Method || studentElement instanceof Method) {
                elementList.add(new MethodCorrection(
                        (Method) templateElement,
                        (Method) studentElement));
            } else if (templateElement instanceof Constructor<?> || studentElement instanceof Constructor<?>) {
                elementList.add(new ConstructorCorrection(
                        (Constructor<?>) templateElement,
                        (Constructor<?>) studentElement));
            } else {
                if (studentElement instanceof Iterable<?>) {
                    for (Object element : (Iterable<?>) studentElement) {
                        if (element instanceof Field) {
                            elementList.add(new FieldCorrection(
                                    null,
                                    (Field) element,
                                    allFieldsPrivate()));
                        } else if (templateElement instanceof Method || element instanceof Method) {
                            elementList.add(new MethodCorrection(
                                    null,
                                    (Method) element));
                        } else if (templateElement instanceof Constructor<?> || element instanceof Constructor<?>) {
                            elementList.add(new ConstructorCorrection(
                                    null,
                                    (Constructor<?>) element));
                        }
                    }
                }
            }
        });

        return elementList;
    }

    public Class<?> getTemplate() {
        return template;
    }

    public Class<?> getStudent() {
        return student;
    }

    public List<SpecificationElement<?>> getElements() {
        return elements;
    }

    public List<SpecificationElement<?>> getCorrectedElements() {
        List<SpecificationElement<?>> corrected = new ArrayList<>();
        for (SpecificationElement<?> e : elements) {
            if (!e.hasTemplate() || !e.hasStudent())
                continue;
            corrected.add(e);
        }
        return corrected;
    }

    public List<SpecificationElement<?>> getMissingElements() {
        List<SpecificationElement<?>> missing = new ArrayList<>();

        for (SpecificationElement<?> element : elements) {
            if (!element.hasStudent() && element.hasTemplate()) {
                missing.add(element);
            }
        }

        return missing;
    }

    public List<SpecificationElement<?>> getExtraElements() {
        List<SpecificationElement<?>> extra = new ArrayList<>();

        // Verifica se a correspondência exata é necessária para atributos, construtores
        // e métodos
        boolean isAttributesExact = ElementUtils.isAttributesExact(template);
        boolean isConstructorsExact = ElementUtils.isConstructorsExact(template);
        boolean isMethodsExact = ElementUtils.isMethodsExact(template);

        // Itera sobre os elementos e verifica se o tipo e a condição exata coincidem
        for (SpecificationElement<?> element : elements) {
            boolean isValidElement = false;

            // Verifica se o elemento é do tipo correto e se a condição exata se aplica
            if (element.getClass().equals(FieldCorrection.class) && isAttributesExact) {
                isValidElement = true;
            } else if (element.getClass().equals(ConstructorCorrection.class) && isConstructorsExact) {
                isValidElement = true;
            } else if (element.getClass().equals(MethodCorrection.class) && isMethodsExact) {
                isValidElement = true;
            }

            // Se for um elemento válido e o estudante tem mas o template não, é considerado
            // extra
            if (isValidElement && element.hasStudent() && !element.hasTemplate()) {
                extra.add(element);
            }
        }

        return extra;
    }

    /**
     * Retorna a nota máxima.
     * 
     * @return nota total do exercício
     */
    public double getGrade() {
        return elements.stream()
                .mapToDouble(SpecificationElement::getGrade)
                .sum();
    }

    /**
     * Retorna a nota obtida pelo aluno.
     * @return
     */
    public double getObtainedGrade() {
        double grade = getCorrectedElements().stream()
                .mapToDouble(SpecificationElement::getObtainedGrade)
                .sum();
        double penalty = ElementUtils.getPenalty(template) * getExtraElements().size();

        return grade - penalty;
    }

    public boolean allFieldsPrivate() {
        return ElementUtils.allFieldsPrivate(template);
    }
}
