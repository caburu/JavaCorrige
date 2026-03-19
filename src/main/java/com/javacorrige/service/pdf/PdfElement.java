package com.javacorrige.service.pdf;

import java.util.List;
import java.util.function.Function;

import com.itextpdf.layout.Document;

import com.itextpdf.layout.element.Paragraph;
import com.javacorrige.model.result.correction.SpecificationElement;
import com.javacorrige.util.reflection.element.ElementFilter;
import com.javacorrige.util.reflection.element.ElementFilter.ElementType;

public class PdfElement {

    public static void addElementSection(Document document, List<SpecificationElement<?>> elements) {

        // Filtra elementos do tipo Construtor, Campo e Método
        List<SpecificationElement<?>> constructors = ElementFilter.getElementsByType(elements, ElementType.CONSTRUCTOR);
        double constructorGrade = 0;
        double constructorObtainedGrade = 0;
        for (SpecificationElement<?> c : constructors) {
            constructorGrade += c.getGrade();
            constructorObtainedGrade += c.getObtainedGrade();
        }

        List<SpecificationElement<?>> fields = ElementFilter.getElementsByType(elements, ElementType.FIELD);
        double fieldGrade = 0;
        double fieldObtainedGrade = 0;
        for (SpecificationElement<?> f : fields) {
            fieldGrade += f.getGrade();
            fieldObtainedGrade += f.getObtainedGrade();
        }

        List<SpecificationElement<?>> methods = ElementFilter.getElementsByType(elements, ElementType.METHOD);
        double methodGrade = 0;
        double methodObtainedGrade = 0;
        for (SpecificationElement<?> f : methods) {
            methodGrade += f.getGrade();
            methodObtainedGrade += f.getObtainedGrade();
        }

        // Adiciona o título da seção (diferente para cada tipo de elemento)
        if (!constructors.isEmpty() && constructorGrade > 0) {
            addSectionTitle(document, "Construtores " + "(" + String.format("%.2f", constructorObtainedGrade) + "/"
                    + String.format("%.2f", constructorGrade) + "):", constructors);
            addTableForConstructors(document, constructors);
        }
        if (!fields.isEmpty() && fieldGrade > 0) {
            addSectionTitle(document, "Atributos " + "(" + String.format("%.2f", fieldObtainedGrade) + "/"
                    + String.format("%.2f", fieldGrade) + "):", fields);
            addTableForFields(document, fields);
        }
        if (!methods.isEmpty() && methodGrade > 0) {
            addSectionTitle(document, "Métodos " + "(" + String.format("%.2f", methodObtainedGrade) + "/"
                    + String.format("%.2f", methodGrade) + "):", methods);
            addTableForMethods(document, methods);
        }
    }

    // Método auxiliar para adicionar o título da seção
    private static void addSectionTitle(Document document, String sectionTitle,
            List<SpecificationElement<?>> elements) {
        Paragraph sectionTitleParagraph = new Paragraph(sectionTitle)
                .setBold()
                .setFontSize(14)
                .setMarginTop(7);
        document.add(sectionTitleParagraph);
    }

    // Adiciona tabela para os construtores
    private static void addTableForConstructors(Document document, List<SpecificationElement<?>> elements) {
        List<String> headers = List.of("Construtor", "Visibilidade", "Modificador", "Parâmetros", "Nota");

        // Cada um dos elementos faz uma filtragem adicional para pegar apenas os
        // elementos que possuem nota (obtainedGrade > 0).
        List<SpecificationElement<?>> filteredElements = filterValuableElements(elements);

        if (filteredElements.isEmpty())
            return;

        // Funções que extraem os valores para a tabela
        List<Function<SpecificationElement<?>, String>> valueExtractors = List.of(
                element -> element.templateString(),
                element -> element.checkVisibility() ? "V" : "X", // Visibilidade
                element -> element.checkModifiers() ? "V" : "X",
                element -> element.checkParameters() ? "V" : "X", // Parâmetros
                element -> String.format("%.2f", element.getObtainedGrade()) + " / "
                        + String.format("%.2f", element.getGrade()));

        // Chama o método da classe PdfTableService para gerar a tabela
        PdfTableService.addTable(document, headers, filteredElements, valueExtractors);
    }

    // Adiciona tabela para os atributos (fields)
    private static void addTableForFields(Document document, List<SpecificationElement<?>> elements) {
        List<String> headers = List.of("Atributo", "Visibilidade", "Modificador", "Tipo", "Nota");

        List<SpecificationElement<?>> filteredElements = filterValuableElements(elements);

        if (filteredElements.isEmpty())
            return;

        // Funções que extraem os valores para a tabela
        List<Function<SpecificationElement<?>, String>> valueExtractors = List.of(
                element -> element.templateString(),
                element -> element.checkVisibility() ? "V" : "X", // Visibilidade
                element -> element.checkModifiers() ? "V" : "X", // Modificador
                element -> element.checkType() ? "V" : "X", // Tipo
                element -> String.format("%.2f", element.getObtainedGrade()) + " / "
                        + String.format("%.2f", element.getGrade()));

        // Chama o método da classe PdfTableService para gerar a tabela
        PdfTableService.addTable(document, headers, filteredElements, valueExtractors);
    }

    // Adiciona tabela para os métodos
    private static void addTableForMethods(Document document, List<SpecificationElement<?>> elements) {
        List<String> headers = List.of("Metodo", "Visibilidade", "Modificador", "Retorno", "Parâmetros", "Teste",
                "Nota");
        // List<String> headers = List.of("Metodo", "Visibilidade", "Modificador",
        // "Retorno", "Parâmetros", "Nota");

        List<SpecificationElement<?>> filteredElements = filterValuableElements(elements);

        if (filteredElements.isEmpty())
            return;

        // Funções que extraem os valores para a tabela
        List<Function<SpecificationElement<?>, String>> valueExtractors = List.of(
                element -> element.templateString(),
                element -> element.checkVisibility() ? "V" : "X", // Visibilidade
                element -> element.checkModifiers() ? "V" : "X", // Modificador
                element -> element.checkReturnType() ? "V" : "X", // Retorno
                element -> element.checkParameters() ? "V" : "X", // Parâmetros
                element -> element.checkTest() ? "V" : "X",
                element -> String.format("%.2f", element.getObtainedGrade()) + " / "
                        + String.format("%.2f", element.getGrade()));

        // Chama o método da classe PdfTableService para gerar a tabela
        PdfTableService.addTable(document, headers, filteredElements, valueExtractors);
    }

    /**
     * Método auxiliar para filtrar os elementos com nota (obtainedGrade > 0)
     */
    private static List<SpecificationElement<?>> filterValuableElements(List<SpecificationElement<?>> list) {
        return list.stream()
                .filter(e -> e.getGrade() > 0)
                .toList();
    }
}
