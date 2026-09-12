package com.javacorrige.service.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.BiConsumer;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.javacorrige.model.result.correction.SpecificationElement;
import com.javacorrige.util.reflection.element.ElementFilter;
import com.javacorrige.util.reflection.element.ElementFilter.ElementType;

public class PdfElement {

    // Record interno para agrupar e transportar as somas das notas
    private record GradeSummary(double total, double obtained) {
    }

    /**
     * Método principal que orquestra a adição das seções de elementos no PDF
     */
    public static void addElementSection(Document document, List<SpecificationElement<?>> elements) {

        // Filtra os elementos por tipo usando o utilitário do sistema
        List<SpecificationElement<?>> constructors = ElementFilter.getElementsByType(elements, ElementType.CONSTRUCTOR);
        List<SpecificationElement<?>> fields = ElementFilter.getElementsByType(elements, ElementType.FIELD);
        List<SpecificationElement<?>> methods = ElementFilter.getElementsByType(elements, ElementType.METHOD);

     
        GradeSummary constrGrades = calculateGrades(constructors);
        GradeSummary fieldGrades = calculateGrades(fields);
        GradeSummary methodGrades = calculateGrades(methods);

        // Renderiza cada seção dinamicamente se houver elementos válidos
        if (!constructors.isEmpty() && constrGrades.total() > 0) {
            renderSection(document, "Construtores", constrGrades, constructors, PdfElement::addTableForConstructors);
        }
        if (!fields.isEmpty() && fieldGrades.total() > 0) {
            renderSection(document, "Atributos", fieldGrades, fields, PdfElement::addTableForFields);
        }
        if (!methods.isEmpty() && methodGrades.total() > 0) {
            renderSection(document, "Métodos", methodGrades, methods, PdfElement::addTableForMethods);
        }
    }

    /**
     * Auxiliar para calcular a soma das notas brutas e obtidas de uma lista de
     * elementos
     */
    private static GradeSummary calculateGrades(List<SpecificationElement<?>> elements) {
        double total = elements.stream().mapToDouble(SpecificationElement::getGrade).sum();
        double obtained = elements.stream().mapToDouble(SpecificationElement::getObtainedGrade).sum();
        return new GradeSummary(total, obtained);
    }

    /**
     * Auxiliar para renderizar o título da seção formatado e disparar a criação da
     * respectiva tabela
     */
    private static void renderSection(Document document, String sectionName, GradeSummary grades,
            List<SpecificationElement<?>> elements,
            BiConsumer<Document, List<SpecificationElement<?>>> tableRenderer) {

        String formattedTitle = String.format("%s (%.2f/%.2f):", sectionName, grades.obtained(), grades.total());
        addSectionTitle(document, formattedTitle);
        tableRenderer.accept(document, elements);
    }

    /**
     * Auxiliar para formatar a string de nota final nas tabelas (centraliza o
     * padrão "obtido / total")
     */
    private static String formatElementGrade(SpecificationElement<?> element) {
        return String.format("%.2f / %.2f", element.getObtainedGrade(), element.getGrade());
    }

    /**
     * Adiciona o título visual da seção no documento PDF
     */
    private static void addSectionTitle(Document document, String sectionTitle) {
        Paragraph sectionTitleParagraph = new Paragraph(sectionTitle)
                .setBold()
                .setFontSize(14)
                .setMarginTop(7);
        document.add(sectionTitleParagraph);
    }

    /**
     * Adiciona tabela para os construtores
     */
    private static void addTableForConstructors(Document document, List<SpecificationElement<?>> elements) {
        List<String> headers = List.of("Construtor", "Visibilidade", "Modificador", "Parâmetros", "Nota");

        List<SpecificationElement<?>> filteredElements = filterValuableElements(elements);
        if (filteredElements.isEmpty())
            return;

        List<Function<SpecificationElement<?>, String>> valueExtractors = List.of(
                SpecificationElement::templateString,
                element -> element.checkVisibility() ? "V" : "X",
                element -> element.checkModifiers() ? "V" : "X",
                element -> element.checkParameters() ? "V" : "X",
                PdfElement::formatElementGrade);

        PdfTableService.addTable(document, headers, filteredElements, valueExtractors);
    }

    /**
     * Adiciona tabela para os atributos (fields)
     */
    private static void addTableForFields(Document document, List<SpecificationElement<?>> elements) {
        List<String> headers = List.of("Atributo", "Visibilidade", "Modificador", "Tipo", "Nota");

        List<SpecificationElement<?>> filteredElements = filterValuableElements(elements);
        if (filteredElements.isEmpty())
            return;

        List<Function<SpecificationElement<?>, String>> valueExtractors = List.of(
                SpecificationElement::templateString,
                element -> element.checkVisibility() ? "V" : "X",
                element -> element.checkModifiers() ? "V" : "X",
                element -> element.checkType() ? "V" : "X",
                PdfElement::formatElementGrade);

        PdfTableService.addTable(document, headers, filteredElements, valueExtractors);
    }

    /**
     * Adiciona tabela para os métodos
     */
    private static void addTableForMethods(Document document, List<SpecificationElement<?>> elements) {
        List<String> headers = List.of("Metodo", "Visibilidade", "Modificador", "Retorno", "Parâmetros", "Teste",
                "Nota");

        List<SpecificationElement<?>> filteredElements = filterValuableElements(elements);
        if (filteredElements.isEmpty())
            return;

        List<Function<SpecificationElement<?>, String>> valueExtractors = List.of(
                SpecificationElement::templateString,
                element -> element.checkVisibility() ? "V" : "X",
                element -> element.checkModifiers() ? "V" : "X",
                element -> element.checkReturnType() ? "V" : "X",
                element -> element.checkParameters() ? "V" : "X",
                element -> element.checkTest() ? "V" : "X",
                PdfElement::formatElementGrade);

        PdfTableService.addTable(document, headers, filteredElements, valueExtractors);
    }

    /**
     * Método auxiliar para filtrar os elementos com nota configurada (grade > 0)
     */
    private static List<SpecificationElement<?>> filterValuableElements(List<SpecificationElement<?>> list) {
        return list.stream()
                .filter(e -> e.getGrade() > 0)
                .toList();
    }
}