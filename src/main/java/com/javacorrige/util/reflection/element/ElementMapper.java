package com.javacorrige.util.reflection.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ElementMapper {

    // Record interno para representar um candidato a match com sua pontuação
    private record MatchCandidate(Object templateElement, Object studentElement, double score) {
    }

    /**
     * Mapeia os elementos entre duas classes priorizando globalmente os maiores
     * scores de similaridade.
     */
    public static Map<Object, Object> mapElements(Class<?> templateClass, Class<?> studentClass) {
        List<Object> templateElements = ElementExtractor.extractElements(templateClass);
        List<Object> studentElements = ElementExtractor.extractElements(studentClass);

        double similarityThreshold = ElementUtils.getSimilarityThreshold(templateClass);

        // Gera o produto cartesiano de todos os pares possíveis com score >= threshold
        List<MatchCandidate> candidates = new ArrayList<>();
        for (Object template : templateElements) {
            for (Object student : studentElements) {
                double score = ElementComparer.calculateSimilarity(template, student);

                if (score >= similarityThreshold) {
                    candidates.add(new MatchCandidate(template, student, score));
                }
            }
        }

        // Ordena todos os pares de forma decrescente pelo score (maiores scores
        // primeiro)
        candidates.sort((c1, c2) -> Double.compare(c2.score(), c1.score()));

        // Aloca os elementos garantindo que nenhum seja reusado
        Map<Object, Object> elementMap = new HashMap<>();
        Set<Object> matchedTemplates = new HashSet<>();
        Set<Object> matchedStudents = new HashSet<>();

        for (MatchCandidate candidate : candidates) {
            boolean templateAvailable = !matchedTemplates.contains(candidate.templateElement());
            boolean studentAvailable = !matchedStudents.contains(candidate.studentElement());

            if (templateAvailable && studentAvailable) {
                elementMap.put(candidate.templateElement(), candidate.studentElement());
                matchedTemplates.add(candidate.templateElement());
                matchedStudents.add(candidate.studentElement());
            }
        }

        // Preenche os elementos do gabarito que não atingiram similaridade mínima com
        // ninguém
        for (Object template : templateElements) {
            elementMap.putIfAbsent(template, null);
        }

        // Isola os elementos do aluno que sobraram (não mapeados)
        List<Object> unmatchedStudents = studentElements.stream()
                .filter(student -> !matchedStudents.contains(student))
                .toList();

        elementMap.put(null, unmatchedStudents);

        return elementMap;
    }
}