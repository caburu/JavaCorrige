package com.javacorrige.model.result.correction;

import java.util.List;
import java.util.stream.Collectors;

import com.javacorrige.model.result.compilation.CompilationResult;
import com.javacorrige.model.result.correction.exercise.ExerciseCorrection;
import com.javacorrige.model.template.Template;

public class ReflectionResult implements Correction {
    private final String templateName;
    private final List<ExerciseCorrection> exercises;
    private final CompilationResult compilationResult;
    private Double obtainedGrade;

    public ReflectionResult(Template template, List<Class<?>> studentClasses, CompilationResult compilationResult) {
        this(template, studentClasses, compilationResult, null);
    }

    public ReflectionResult(Template template, List<Class<?>> studentClasses, CompilationResult compilationResult,
            String targetClassName) {
        // Lida com a possibilidade do template ser null
        this.templateName = template == null ? null : template.getTemplateName();
        this.compilationResult = compilationResult;
        this.exercises = template == null ? null : initializeExercises(template, studentClasses, targetClassName);
        this.obtainedGrade = null;
    }

    private List<ExerciseCorrection> initializeExercises(Template template, List<Class<?>> studentClasses,
            String targetClassName) {
        return template.getExercises().stream()
                .map(exercise -> new ExerciseCorrection(exercise, studentClasses, targetClassName))
                .collect(Collectors.toList());
    }

    public String getTemplateName() {
        return templateName;
    }

    public List<ExerciseCorrection> getExercises() {
        return exercises;
    }

    public CompilationResult getCompilationResult() {
        return compilationResult;
    }

    public double getGrade() {
        return exercises.stream().mapToDouble(ExerciseCorrection::getGrade).sum();
    }

    public double getObtainedGrade() {
        if (obtainedGrade == null) {
            obtainedGrade = calculeObtainedGrade();
        }
        return obtainedGrade;
    }

    private double calculeObtainedGrade() {
        return exercises.stream().mapToDouble(ExerciseCorrection::getObtainedGrade).sum();
    }
}
