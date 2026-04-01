package com.javacorrige.model.result.correction;

import java.util.List;
import java.util.stream.Collectors;

import com.javacorrige.model.result.compilation.CompilationResult;
import com.javacorrige.model.result.correction.exercise.ExerciseCorrection;
import com.javacorrige.model.template.Template;

public class ReflectionResult implements Correction{
    private final String templateName;
    private final List<ExerciseCorrection> exercises;
    private final CompilationResult compilationResult;

    public ReflectionResult(Template template, List<Class<?>> studentClasses, CompilationResult compilationResult) {
        this.templateName = template.getTemplateName();
        this.compilationResult = compilationResult;
        this.exercises = initializeExercises(template, studentClasses);
    }

    private List<ExerciseCorrection> initializeExercises(Template template, List<Class<?>> studentClasses) {
        return template.getExercises().stream()
                .map(exercise -> new ExerciseCorrection(exercise, studentClasses))
                .collect(Collectors.toList());
    }

    public String getTemplateName() { return templateName; }
    public List<ExerciseCorrection> getExercises() { return exercises; }
    public CompilationResult getCompilationResult() { return compilationResult; }
    public double getGrade() { return exercises.stream().mapToDouble(ExerciseCorrection::getGrade).sum(); }
    public double getObtainedGrade() { return exercises.stream().mapToDouble(ExerciseCorrection::getObtainedGrade).sum(); }
}
