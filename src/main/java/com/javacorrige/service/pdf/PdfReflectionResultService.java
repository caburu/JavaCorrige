package com.javacorrige.service.pdf;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.javacorrige.model.Student;
import com.javacorrige.model.result.correction.ReflectionResult;
import com.javacorrige.model.result.correction.exercise.ExerciseCorrection;
import com.javacorrige.model.result.correction.exercise.clazz.ClassCorrection;

public class PdfReflectionResultService {

    private PdfReflectionResultService() {
    }

    public static void addReflectionResult(Document document, ReflectionResult reflectionResult) {
        for (ExerciseCorrection exercise : reflectionResult.getExercises()) {
            if (exercise.getGrade() <= 0) {
                continue;
            }

            // Adiciona o nome do exercício
            String title = "Exercício `" + exercise.getExerciseName() + "`:  ("
                    + String.format("%.2f", exercise.getObtainedGrade()) + "/"
                    + String.format("%.2f", exercise.getGrade()) + ")";

            addTitle(title, document);

            if (!exercise.getMissingClasses().isEmpty()) {
                java.util.List<String> failedFileNames = reflectionResult
                        .getCompilationResult().getFailedFiles() != null
                                ? reflectionResult.getCompilationResult().getFailedFiles().stream()
                                        .map(f -> f.getName().replace(".java", ""))
                                        .toList()
                                : new java.util.ArrayList<>();

                java.util.List<String> trulyMissing = new java.util.ArrayList<>();
                java.util.List<String> failedCompilation = new java.util.ArrayList<>();

                for (String missingClass : exercise.getMissingClasses()) {
                    if (failedFileNames.contains(missingClass)) {
                        failedCompilation.add(missingClass);
                    } else {
                        trulyMissing.add(missingClass);
                    }
                }

                if (!failedCompilation.isEmpty()) {
                    Paragraph failedPara = new Paragraph(
                            "Classes com erro de compilação: " + String.join(", ", failedCompilation))
                            .setFontColor(ColorConstants.RED)
                            .setMarginBottom(5);
                    document.add(failedPara);
                }

                if (!trulyMissing.isEmpty()) {
                    Paragraph missingPara = new Paragraph(
                            "Classes ausentes: " + String.join(", ", trulyMissing))
                            .setFontColor(ColorConstants.RED)
                            .setMarginBottom(10);
                    document.add(missingPara);
                }
            }

            for (ClassCorrection classCorrection : exercise.getCorrectedClasses()) {
                if (classCorrection.getGrade() > 0) {
                    PdfClassCorrection.addClassCorrection(document, classCorrection);
                }
            }
        }
    }

    /**
     * Adiciona os dados dos estudantes ao relatório geral.
     * 
     * @param document Documento em que os dados devem ser inseridos.
     * @param students Lista de estudantes que devem ser adicionados ao relatório
     *                 geral.
     */
    public static void addReflectionResultGeneralReport(Document document,
            List<Student> students) {
        Table table = generateGeneralReportTable();

        for (Student student : students) {
            String correctedClasses = formatCorrectedClasses(student);
            addStudentToTable(table, correctedClasses, student);
        }

        String exercisesNamesHeader = extractExerciseNamesHeader(students);
        addTitle(exercisesNamesHeader, document);
        document.add(table);
    }

    /**
     * Retorna as classes de um estudante formatadas para o relatório geral.
     * 
     * @param student estudante que deseja obter as classes
     * @return classes formatadas
     */
    private static String formatCorrectedClasses(Student student) {
        if (student.getCompilationResult().getFailedFiles() != null
                && !student.getCompilationResult().getFailedFiles().isEmpty()) {
            return "Erro ao compilar execícios!";
        }

        if (student.getReflectionResult() == null || student.getReflectionResult().getExercises() == null) {
            return "Exercício não encontrado!";
        }

        // Percorre os exercícios dos estudantes e retorna a lista de classes corrigidas
        return student.getReflectionResult().getExercises().stream().filter(e -> e.getGrade() > 0)
                .flatMap(e -> e.getCorrectedClasses().stream())
                .filter(c -> c.getGrade() > 0).map(PdfClassCorrection::getSimpleClassCorrection)
                .collect(Collectors.joining(", "));
    }

    /**
     * Recebe uma lista de estudantes e retorna uma String contendo o nome dos
     * exercícios feitos por eles.
     * 
     * @param students estudantes que vão ser avaliados
     * @return String contendo o nome dos exercícios corrigidos
     */
    private static String extractExerciseNamesHeader(List<Student> students) {
        return students.stream().map(Student::getReflectionResult).filter(Objects::nonNull)
                .flatMap(r -> r.getExercises().stream())
                .filter(e -> e.getGrade() > 0).map(ExerciseCorrection::getExerciseName).distinct()
                .collect(Collectors.joining(", "));
    }

    /**
     * Cria uma célula na tabela
     * 
     * @param content conteúdo da célula
     * @return nova célula
     */
    private static Cell createCell(String content) {
        return new Cell()
                .add(new Paragraph(content))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(6)
                .setMarginBottom(5);
    }

    /**
     * Adiciona um novo título no documento
     * 
     * @param title    título do documento
     * @param document documento em que o título é adicionado
     */
    private static void addTitle(String title, Document document) {
        Paragraph stepTitle = new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.BLUE)
                .setMarginTop(10);

        document.add(stepTitle);
    }

    /**
     * Gera a tabela para o relatório geral
     * 
     * @return tabela formatada para o relatório
     */
    private static Table generateGeneralReportTable() {
        Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 2, 5 })).useAllAvailableWidth();

        // Define o espaçamento acima da tabela
        table.setMarginTop(20f);

        // Adiciona o cabeçalho da tabela (em negrito)
        table.addHeaderCell(new Cell().add(new Paragraph("Nome").setFontSize(8)).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Nota final").setFontSize(8)).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Notas por classe").setFontSize(8)).setBold());

        return table;
    }

    /**
     * Adiciona um estudante na tabela
     * 
     * @param table            tabela em que o estudante deve ser adicionado
     * @param correctedClasses classes do estudante (corrigidas)
     * @param student          estudante que deve ser adicionado na tabela
     */
    private static void addStudentToTable(Table table, String correctedClasses, Student student) {
        String finalGrade = student.getReflectionResult().getObtainedGrade() + "/"
                + student.getReflectionResult().getGrade();

        table.addCell(createCell(student.getName()));
        table.addCell(createCell(finalGrade));
        table.addCell(createCell(correctedClasses));
    }
}
