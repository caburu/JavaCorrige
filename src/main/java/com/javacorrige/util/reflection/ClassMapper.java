package com.javacorrige.util.reflection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ClassMapper {

    public static Map<Class<?>, Class<?>> mapClasses(List<Class<?>> templateClasses, List<Class<?>> studentClasses) {
        Map<Class<?>, Class<?>> mappedClasses = new HashMap<>();

        // Faz uma cópia da lista de studentClasses para evitar modificações durante a
        // iteração
        List<Class<?>> remainingStudentClasses = new ArrayList<>(studentClasses);

        // Para cada classe do template, busca uma correspondente no estudante
        for (Class<?> templateClass : templateClasses) {
            boolean matched = false;

            // Tenta encontrar a classe correspondente no estudante
            for (Class<?> studentClass : remainingStudentClasses) {
                // Se houver compatibilidade (mesmo nome simples)
                if (templateClass.getSimpleName().equals(studentClass.getSimpleName())) {
                    mappedClasses.put(templateClass, studentClass); // Mapeia a correspondência
                    remainingStudentClasses.remove(studentClass); // Remove a classe do estudante que já foi mapeada
                    matched = true;
                    break; // Encerra o loop interno, pois já encontrou a correspondência
                }
            }

            // Se não houver correspondência, mapeia a classe do template para null
            if (!matched) {
                mappedClasses.put(templateClass, null);
            }
        }

        // Para as classes do estudante que não foram mapeadas, adiciona ao mapa com
        // chave null
        for (Class<?> studentClass : remainingStudentClasses) {
            mappedClasses.put(null, studentClass);
        }

        return mappedClasses;
    }
}
