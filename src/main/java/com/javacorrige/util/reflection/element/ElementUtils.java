package com.javacorrige.util.reflection.element;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import com.javacorrige.util.reflection.annotation.AnnotationExtractor;

public class ElementUtils {

    public static boolean checkVisibility(Object template, Object student) {
        if (!canValidateVisibility(template, student))
            return false;

        int templateModifiers = getModifiers(template);
        int studentModifiers = getModifiers(student);

        return (Modifier.isPublic(templateModifiers) == Modifier.isPublic(studentModifiers)) &&
                (Modifier.isProtected(templateModifiers) == Modifier.isProtected(studentModifiers)) &&
                (Modifier.isPrivate(templateModifiers) == Modifier.isPrivate(studentModifiers));
    }

    public static boolean checkModifiers(Object template, Object student) {
        if (!canValidateModifiers(template, student))
            return false;

        int templateModifiers = getModifiers(template);
        int studentModifiers = getModifiers(student);

        boolean areStatic = Modifier.isStatic(templateModifiers) == Modifier.isStatic(studentModifiers);
        boolean areFinal = Modifier.isFinal(templateModifiers) == Modifier.isFinal(studentModifiers);

        return areStatic && areFinal;
    }

    public static boolean checkParameters(Object template, Object student) {
        if (!canValidateParameters(template, student))
            return false;

        Class<?>[] templateParams = getParameterTypes(template);
        Class<?>[] studentParams = getParameterTypes(student);

        // Verifica se ambos têm o mesmo número de parâmetros
        if (templateParams.length != studentParams.length) {
            return false;
        }

        // Compara os nomes completos dos parâmetros para verificar equivalência
        for (int i = 0; i < templateParams.length; i++) {
            String templateParamName = templateParams[i].getName();
            String studentParamName = studentParams[i].getName();

            if (!templateParamName.equals(studentParamName)) {
                return false; // Diferença encontrada
            }
        }

        return true; // Todos os parâmetros correspondem
    }

    public static boolean checkReturnType(Object template, Object student) {
        if (!canValidateReturnType(template, student))
            return false;

        Method templateMethod = (Method) template;
        Method studentMethod = (Method) student;

        return templateMethod.getReturnType().getSimpleName().equals(studentMethod.getReturnType().getSimpleName());
    }

    public static boolean checkType(Object template, Object student) {
        if (!canValidateType(template, student))
            return false;

        Field templateField = (Field) template;
        Field studentField = (Field) student;

        return templateField.getType().getSimpleName().equals(studentField.getType().getSimpleName());
    }

    public static boolean checkTest(Object template, Object student) {
        if (template == null || student == null) {
            return false;
        }
        if (AnnotationExtractor.getTest(template) == null) {
            return true;
        }

        if (!canValidateTest(template, student) || !checkParameters(template, student)
                || !checkReturnType(template, student))
            return false;

        Method templateMethod = (Method) template;
        Method studentMethod = (Method) student;

        String[] methodParameters = getMethodParametersTest(templateMethod);
        String[] constructorParameters = getConstructorParametersTest(templateMethod);

        // Guarda a referência do console original
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream templateStream = new java.io.ByteArrayOutputStream();
        java.io.ByteArrayOutputStream studentStream = new java.io.ByteArrayOutputStream();

        try {
            Object[] convertedMethodParameters = convertParameters(templateMethod, methodParameters);

            Constructor<?> matchingTemplateConstructor = findConstructorWithParameters(
                    templateMethod.getDeclaringClass(), constructorParameters.length);
            Constructor<?> matchingStudentConstructor = findConstructorWithParameters(
                    studentMethod.getDeclaringClass(), constructorParameters.length);

            Class<?>[] constructorParameterTypes = matchingTemplateConstructor.getParameterTypes();

            Object[] convertedConstructorParameters = convertConstructorParameters(constructorParameters,
                    constructorParameterTypes);

            Object templateInstance = Modifier.isStatic(templateMethod.getModifiers()) ? null
                    : matchingTemplateConstructor.newInstance(convertedConstructorParameters);
            Object studentInstance = Modifier.isStatic(studentMethod.getModifiers()) ? null
                    : matchingStudentConstructor.newInstance(convertedConstructorParameters);

            System.setOut(new java.io.PrintStream(templateStream));
            Object templateResult = templateMethod.invoke(templateInstance, convertedMethodParameters);

            System.setOut(new java.io.PrintStream(studentStream));
            Object studentResult = studentMethod.invoke(studentInstance, convertedMethodParameters);

            System.setOut(originalOut);

            String expectedConsole = normalizeText(templateStream.toString());
            String actualConsole = normalizeText(studentStream.toString());

            if (!expectedConsole.isEmpty()) {
                boolean consoleMatched = expectedConsole.equals(actualConsole);
                if (!consoleMatched)
                    return false; // Se a impressão falhar, o teste reprova
            }

            boolean isVoid = templateMethod.getReturnType().equals(Void.TYPE);
            if (!isVoid) {
                boolean resultMatched = compareResults(templateResult, studentResult);
                if (!resultMatched)
                    return false;
            }

            return compareStates(templateInstance, studentInstance);

        } catch (Exception e) {
            return false;
        } finally {
            // Garantia de restauração do console e fechamento de janelas
            System.setOut(originalOut);

            for (java.awt.Window window : java.awt.Window.getWindows()) {
                window.setVisible(false);
                window.dispose();
            }
        }
    }

    private static Constructor<?> findConstructorWithParameters(Class<?> declaringClass, int parametersLength) {
        Constructor<?>[] constructors = declaringClass.getConstructors();

        Constructor<?> matchingConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == parametersLength) {
                matchingConstructor = constructor;
                return matchingConstructor;
            }
        }

        return null;
    }

    private static boolean compareResults(Object result1, Object result2) {
        if (result1 == null && result2 == null)
            return true;
        if (result1 == null || result2 == null)
            return false;

        // Se for um tipo básico (Primitivo, String, Number), usa equals comum
        if (isSimpleType(result1)) {
            return result1.equals(result2);
        }

        // Se for um objeto complexo, compara recursivamente
        return compareStates(result1, result2);
    }

    /**
     * Checa se o tipo do obj é um tipo básico
     * 
     * @param obj objeto que deve ser checado
     * @return true se é positivo ou false caso contrário
     */
    private static boolean isSimpleType(Object obj) {
        return obj instanceof String ||
                obj instanceof Number ||
                obj instanceof Boolean ||
                obj.getClass().isPrimitive();
    }

    // TODO: Garantir que os mesmos atributos, mesmo fora de ordem sejam corrigidos
    // de maneira adequada.
    /**
     * Itera por duas instâncias comparando o estado dos atributos.
     * Os itens são comparados em ordem, isso implica que atributos com estados
     * similares,
     * mas declarados em ordens diferentes, retornarão false.
     * 
     * @param result1
     * @param result2
     * @return true se os estados são iguais ou false se são diferentes
     */
    private static boolean compareStates(Object result1, Object result2) {
        Field[] templateFields = result1.getClass().getDeclaredFields();
        Field[] studentFields = result2.getClass().getDeclaredFields();

        if (templateFields.length != studentFields.length)
            return false;

        // Percorre cada um dos campos e checa se os estados são iguais
        try {
            for (int i = 0; i < templateFields.length; i++) {
                templateFields[i].setAccessible(true);
                studentFields[i].setAccessible(true);

                Object valTemplate = templateFields[i].get(result1);
                Object valStudent = studentFields[i].get(result2);

                if (!compareResults(valTemplate, valStudent)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // --- ANOTAÇÕES ---

    public static boolean allFieldsPrivate(Object element) {
        try {
            Annotation annotation = AnnotationExtractor.getSpecification(element);
            if (annotation == null)
                return true;

            Method valueMethod = annotation.annotationType().getMethod("todosAtributosPrivados");

            return (boolean) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isAttributesExact(Object element) {
        try {
            Annotation annotation = AnnotationExtractor.getSpecification(element);
            if (annotation == null)
                return false;

            Method valueMethod = annotation.annotationType().getMethod("atributosExatos");

            return (boolean) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConstructorsExact(Object element) {
        try {
            Annotation annotation = AnnotationExtractor.getSpecification(element);
            if (annotation == null)
                return false;

            Method valueMethod = annotation.annotationType().getMethod("construtoresExatos");

            return (boolean) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMethodsExact(Object element) {
        try {
            Annotation annotation = AnnotationExtractor.getSpecification(element);
            if (annotation == null)
                return false;

            Method valueMethod = annotation.annotationType().getMethod("metodosExatos");

            return (boolean) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return false;
        }
    }

    public static double getGrade(Object element) {
        if (element == null) {
            return 0.0;
        }

        try {
            Annotation annotation = AnnotationExtractor.getGrade(element);
            if (annotation == null)
                return 0.0;

            Method valueMethod = annotation.annotationType().getMethod("value");

            return (double) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static double getPenalty(Object element) {
        try {
            Annotation annotation = AnnotationExtractor.getSpecification(element);
            if (annotation == null)
                return 0.0;

            Method valueMethod = annotation.annotationType().getMethod("penalidade");

            return (double) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static double getSimilarityThreshold(Object element) {
        try {
            Annotation annotation = AnnotationExtractor.getSpecification(element);
            if (annotation == null)
                return 0.0;

            Method valueMethod = annotation.annotationType().getMethod("similaridade");

            return (double) valueMethod.invoke(annotation);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static String[] getMethodParametersTest(Method method) {
        try {
            Annotation annotation = AnnotationExtractor.getTest(method);
            if (annotation == null) {
                return new String[0];
            }

            Method valueMethod = annotation.annotationType().getMethod("parametros");
            return (String[]) valueMethod.invoke(annotation);

        } catch (Exception e) {
            return new String[0];
        }
    }

    private static String[] getConstructorParametersTest(Method method) {
        try {
            Annotation annotation = AnnotationExtractor.getTest(method);
            if (annotation == null) {
                return new String[0];
            }

            Method valueMethod = annotation.annotationType().getMethod("construtor");
            return (String[]) valueMethod.invoke(annotation);

        } catch (Exception e) {
            return new String[0];
        }
    }

    // --- Métodos Auxiliares ---

    /**
     * Normaliza o texto impresso no console para evitar divergências por causa
     * de quebras de linha entre sistemas operacionais (\r\n vs \n) ou espaços
     * extras.
     */
    private static String normalizeText(String input) {
        if (input == null)
            return "";
        return input.replace("\r\n", "\n")
                .trim();
    }

    private static int getModifiers(Object element) {
        if (element instanceof Field)
            return ((Field) element).getModifiers();
        if (element instanceof Method)
            return ((Method) element).getModifiers();
        if (element instanceof Constructor)
            return ((Constructor<?>) element).getModifiers();
        return 0;
    }

    private static Class<?>[] getParameterTypes(Object element) {
        if (element instanceof Method)
            return ((Method) element).getParameterTypes();
        if (element instanceof Constructor)
            return ((Constructor<?>) element).getParameterTypes();
        return new Class<?>[0];
    }

    // --- VALIDADORES ---

    private static boolean canValidateVisibility(Object element1, Object element2) {
        if (element1 == null || element2 == null) {
            return false;
        }

        return (element1 instanceof Constructor<?> && element2 instanceof Constructor<?>) ||
                (element1 instanceof Field && element2 instanceof Field) ||
                (element1 instanceof Method && element2 instanceof Method);
    }

    private static boolean canValidateModifiers(Object element1, Object element2) {
        if (element1 == null || element2 == null) {
            return false;
        }

        return (element1 instanceof Constructor<?> && element2 instanceof Constructor<?>) ||
                (element1 instanceof Field && element2 instanceof Field) ||
                (element1 instanceof Method && element2 instanceof Method);
    }

    private static boolean canValidateReturnType(Object element1, Object element2) {
        if (element1 == null || element2 == null) {
            return false;
        }

        return (element1 instanceof Method && element2 instanceof Method);
    }

    private static boolean canValidateParameters(Object element1, Object element2) {
        if (element1 == null || element2 == null) {
            return false;
        }

        return (element1 instanceof Method && element2 instanceof Method) ||
                (element1 instanceof Constructor<?> && element2 instanceof Constructor<?>);
    }

    private static boolean canValidateType(Object element1, Object element2) {
        if (element1 == null || element2 == null) {
            return false;
        }

        return (element1 instanceof Field && element2 instanceof Field);
    }

    private static boolean canValidateTest(Object element1, Object element2) {
        if (element1 == null || element2 == null) {
            return false;
        }

        return (element1 instanceof Method && element2 instanceof Method);
    }

    private static Object[] convertParameters(Method method, String[] parameters) throws IllegalArgumentException {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameters.length != parameterTypes.length) {
            throw new IllegalArgumentException("O número de parâmetros não corresponde ao esperado.");
        }

        Object[] convertedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            convertedParameters[i] = convertToType(parameters[i], parameterTypes[i]);
        }

        return convertedParameters;
    }

    private static Object[] convertConstructorParameters(String[] parameters, Class<?>[] parameterTypes)
            throws IllegalArgumentException {
        if (parameters.length != parameterTypes.length) {
            throw new IllegalArgumentException("O número de parâmetros do construtor não corresponde ao esperado.");
        }

        Object[] convertedParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            convertedParameters[i] = convertToType(parameters[i], parameterTypes[i]);
        }

        return convertedParameters;
    }

    private static Object convertToType(String parameter, Class<?> type) throws IllegalArgumentException {
        try {
            if (type == int.class || type == Integer.class) {
                return Integer.parseInt(parameter);
            } else if (type == double.class || type == Double.class) {
                return Double.parseDouble(parameter);
            } else if (type == float.class || type == Float.class) {
                return Float.parseFloat(parameter);
            } else if (type == boolean.class || type == Boolean.class) {
                return Boolean.parseBoolean(parameter);
            } else if (type == long.class || type == Long.class) {
                return Long.parseLong(parameter);
            } else if (type == String.class) {
                return parameter;
            } else {
                throw new IllegalArgumentException("Tipo não suportado: " + type.getName());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Falha ao converter parâmetro '" + parameter + "' para o tipo " + type.getName());
        }
    }

}
