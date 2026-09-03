package com.javacorrige.service.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.javacorrige.model.result.correction.ReflectionResult;
import com.javacorrige.model.result.correction.exercise.ExerciseCorrection;
import com.javacorrige.model.result.correction.exercise.clazz.ClassCorrection;

public class PdfReflectionResultService {
    public static void addReflectionResult(Document document, ReflectionResult reflectionResult) {
        for (ExerciseCorrection exercise : reflectionResult.getExercises()) {

            if (exercise.getGrade() <= 0) {
                continue;
            }

            Paragraph stepTitle = new Paragraph("Exercício `" + exercise.getExerciseName() + "`:  ("
                    + String.format("%.2f", exercise.getObtainedGrade()) + "/"
                    + String.format("%.2f", exercise.getGrade()) + ")")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE)
                    .setMarginTop(10);
            document.add(stepTitle);

            if (!exercise.getMissingClasses().isEmpty()) {
                java.util.List<String> failedFileNames = reflectionResult
                        .getCompilationResult().getFailedFiles() != null
                                ? reflectionResult.getCompilationResult().getFailedFiles().stream()
                                        .map(f -> f.getName().replace(".java", ""))
                                        .collect(java.util.stream.Collectors.toList())
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
}
