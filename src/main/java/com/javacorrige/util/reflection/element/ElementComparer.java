package com.javacorrige.util.reflection.element;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.javacorrige.util.reflection.NameSimilarityCalculator;

public class ElementComparer {

    private ElementComparer() {

    }

    /**
     * Calcula um índice numérico de similaridade entre 0.0 e 1.0.
     */
    public static double calculateSimilarity(Object templateElement, Object studentElement) {
        if (templateElement == null || studentElement == null) {
            return 0.0;
        }

        // Elementos de categorias diferentes (ex: Field vs Method) nunca casam
        if (templateElement.getClass() != studentElement.getClass()) {
            return 0.0;
        }

        if (templateElement instanceof Method templateMethod && studentElement instanceof Method studentMethod) {
            return calculateMethodSimilarity(templateMethod, studentMethod);
        }

        if (templateElement instanceof Constructor<?> templateConstructor
                && studentElement instanceof Constructor<?> studentConstructor) {
            return calculateConstructorSimilarity(templateConstructor, studentConstructor);
        }

        if (templateElement instanceof Field templateField && studentElement instanceof Field studentField) {
            return calculateFieldSimilarity(templateField, studentField);
        }

        return 0.0;
    }

    private static double calculateMethodSimilarity(Method templateMethod, Method studentMethod) {
        // Similaridade do nome (peso 50%)
        double nameScore = NameSimilarityCalculator.calculate(templateMethod.getName(), studentMethod.getName());

        // Similaridade do tipo de retorno (peso 20%)
        double returnTypeScore = templateMethod.getReturnType().equals(studentMethod.getReturnType()) ? 1.0 : 0.0;

        // Similaridade dos parâmetros (peso 30%)
        double paramsScore = calculateParameterSimilarity(templateMethod.getParameterTypes(),
                studentMethod.getParameterTypes());

        return (nameScore * 0.50) + (returnTypeScore * 0.20) + (paramsScore * 0.30);
    }

    private static double calculateConstructorSimilarity(Constructor<?> templateConstructor,
            Constructor<?> studentConstructor) {
        // Construtores têm o mesmo nome da classe, então a diferenciação é dada 100%
        // pelos parâmetros
        return calculateParameterSimilarity(templateConstructor.getParameterTypes(),
                studentConstructor.getParameterTypes());
    }

    private static double calculateFieldSimilarity(Field templateField, Field studentField) {
        // Similaridade do nome (peso 70%)
        double nameScore = NameSimilarityCalculator.calculate(templateField.getName(), studentField.getName());

        // Tipo do atributo compatível (peso 30%)
        double typeScore = templateField.getType().equals(studentField.getType()) ? 1.0 : 0.0;

        return (nameScore * 0.70) + (typeScore * 0.30);
    }

    private static double calculateParameterSimilarity(Class<?>[] params1, Class<?>[] params2) {
        if (params1.length == 0 && params2.length == 0) {
            return 1.0;
        }

        int maxLength = Math.max(params1.length, params2.length);
        if (maxLength == 0)
            return 1.0;

        int matches = 0;
        int minLength = Math.min(params1.length, params2.length);

        for (int i = 0; i < minLength; i++) {
            if (params1[i].equals(params2[i])) {
                matches++;
            }
        }

        // Penaliza se a quantidade de parâmetros for diferente dividindo pelo tamanho
        // máximo
        return (double) matches / maxLength;
    }
}
