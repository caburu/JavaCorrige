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
                Paragraph missingClasses = new Paragraph(
                        "Classes ausentes: " + String.join(", ", exercise.getMissingClasses()))
                        .setFontColor(ColorConstants.RED)
                        .setMarginBottom(10);
                document.add(missingClasses);
            }

            for (ClassCorrection classCorrection : exercise.getCorrectedClasses()) {
                if (classCorrection.getGrade() > 0) {
                    PdfClassCorrection.addClassCorrection(document, classCorrection);
                }
            }
        }
    }
}
