package com.javacorrige.service.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.util.List;
import java.util.function.Function;

public class PdfTableService {

    private PdfTableService() {
    }

    public static <T> void addTable(Document document, List<String> headers, List<T> data,
            List<Function<T, String>> valueExtractors) {
        if (headers.size() != valueExtractors.size()) {
            throw new IllegalArgumentException(
                    "O número de cabeçalhos deve corresponder ao número de funções de extração.");
        }

        // Cria a tabela com o número correto de colunas
        Table table = new Table(headers.size());
        table.setWidth(document.getPdfDocument().getDefaultPageSize().getWidth() - 40); // Define a largura com margem

        // Adiciona cabeçalhos com formatação
        addTableHeader(table, headers);

        // Adiciona dados
        for (T item : data) {
            for (Function<T, String> extractor : valueExtractors) {
                String value = extractor.apply(item);
                Cell cell = new Cell()
                        .add(new Paragraph(value != null ? value : ""))
                        .setPadding(5)
                        .setTextAlignment(TextAlignment.CENTER);

                // Aplica a cor dependendo do valor (V para verde, X para vermelho)
                if ("V".equals(value)) {
                    cell.setFontColor(ColorConstants.GREEN);
                } else if ("X".equals(value)) {
                    cell.setFontColor(ColorConstants.RED);
                }

                table.addCell(cell);
            }
        }

        table.setMarginBottom(10);

        // Adiciona a tabela ao documento
        document.add(table);
    }

    /**
     * Adiciona um cabeçalho em uma tabela. 
     * 
     * @param table tabela que o cabeçalho deve ser adicionado 
     * @param headers valores do cabeçalho 
     */
    private static void addTableHeader(Table table, List<String> headers) {
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header))
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(headerCell);
        }
    }
}
