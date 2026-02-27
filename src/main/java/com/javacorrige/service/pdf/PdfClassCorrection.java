package com.javacorrige.service.pdf;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.javacorrige.model.result.correction.SpecificationElement;
import com.javacorrige.model.result.correction.exercise.clazz.ClassCorrection;
import com.javacorrige.util.reflection.element.ElementFilter;
import com.javacorrige.util.reflection.element.ElementFilter.ElementType;

public class PdfClassCorrection {

    public static void addClassCorrection(Document document, ClassCorrection classCorrection) {

        // Adiciona o título da classe
        addTitle(document,
                "Classe `" + classCorrection.getTemplate().getSimpleName() + "`("
                        + String.format("%.2f", classCorrection.getObtainedGrade()) + "/"
                        + String.format("%.2f", classCorrection.getGrade()) + ")");

        // Adiciona os elementos ausentes
        addElementsByType(
                document,
                classCorrection.getMissingElements(),
                "Elementos ausentes:",
                ColorConstants.RED);

        // Adiciona os elementos extras
        addElementsByType(
                document,
                classCorrection.getExtraElements(),
                "Elementos extras:",
                ColorConstants.CYAN);

        // Adiciona os elementos corrigidos
        PdfElement.addElementSection(document, classCorrection.getCorrectedElements());
    }

    private static void addTitle(Document document, String titleText) {
        Paragraph classTitle = new Paragraph(titleText)
                .setBold()
                .setFontSize(14)
                .setMarginTop(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(classTitle);
    }

    private static void addElementsByType(
            Document document,
            List<SpecificationElement<?>> elements,
            String headerText,
            com.itextpdf.kernel.colors.Color color) {
        if (elements.isEmpty())
            return;

        // Agrupa os elementos por tipo usando ElementFilter
        Map<ElementType, List<SpecificationElement<?>>> groupedElements = new EnumMap<>(ElementType.class);
        for (ElementType type : ElementType.values()) {
            groupedElements.put(type, ElementFilter.getElementsByType(elements, type));
        }

        // Adiciona o cabeçalho
        Paragraph header = new Paragraph(headerText)
                .setFontColor(color)
                .setBold()
                .setMarginBottom(5);
        document.add(header);

        // Adiciona os elementos de cada tipo
        groupedElements.forEach((type, elementsOfType) -> {
            if (!elementsOfType.isEmpty()) {
                String typeTitle = getTypeTitle(type);
                String elementDescriptions = elementsOfType.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                Paragraph typeParagraph = new Paragraph(typeTitle + ": " + elementDescriptions)
                        .setFontColor(color)
                        .setMarginBottom(10);
                document.add(typeParagraph);
            }
        });
    }

    private static String getTypeTitle(ElementType type) {
        switch (type) {
            case CONSTRUCTOR:
                return "Construtores";
            case FIELD:
                return "Atributos";
            case METHOD:
                return "Métodos";
            default:
                return "Desconhecido";
        }
    }
}
