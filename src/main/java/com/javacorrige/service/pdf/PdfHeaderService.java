package com.javacorrige.service.pdf;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PdfHeaderService {

    private PdfHeaderService() {
    }

    public static void addHeader(Document document, String studentName, String grade) {
        // Título do relatório
        addTitle(document, "Relatório de Correção de Exercícios");

        // Tabela com duas colunas, onde a primeira tem menor largura
        Table table = addDefaultHeaderTable();

        // Adiciona a célula para "Aluno"
        table.addCell(createCell("Aluno:"));
        table.addCell(createCell(studentName));

        // Adiciona a célula para "Data"
        addLocalDateCell(table);

        // Adiciona a célula para "Nota"
        table.addCell(createCell("Nota:"));
        table.addCell(createCell(grade));

        // Adiciona a tabela ao documento
        document.add(table);
    }

    public static void addGeneralReportHeader(Document document, String templateDirectory) {

        addTitle(document, "Relatório geral de Correção de Exercícios");

        // Tabela principal
        Table table = addDefaultHeaderTable();

        // Célula para data
        addLocalDateCell(table);

        // Célula para caminho do gabarito
        table.addCell(createCell("Diretório Gabarito:"));
        table.addCell(createCell(templateDirectory));

        document.add(table);
    }

    // Método auxiliar para criar as células da tabela
    private static Cell createCell(String content) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(8)
                .setMarginBottom(5);
    }

    private static void addTitle(Document document, String text) {
        Paragraph title = new Paragraph(text)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);
    }

    private static Table addDefaultHeaderTable() {
        return new Table(UnitValue.createPercentArray(new float[] { 1, 7 })).useAllAvailableWidth();
    }

    private static void addLocalDateCell(Table table) {
        // Adiciona a célula para "Data"
        table.addCell(createCell("Data:"));
        table.addCell(createCell(getLocaldate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
    }

    private static LocalDate getLocaldate() {
        return LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    }
}
