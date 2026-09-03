package com.javacorrige.model.result.correction.exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.javacorrige.model.result.correction.Correction;
import com.javacorrige.model.result.correction.exercise.clazz.ClassCorrection;
import com.javacorrige.model.template.exercise.Exercise;
import com.javacorrige.util.reflection.ClassMapper;

public class ExerciseCorrection implements Correction {
    private final String exerciseName;
    private final List<ClassCorrection> classes;
    private Double grade;
    private Double obtainedGrade;

    public ExerciseCorrection(Exercise exercise, List<Class<?>> studentClasses) {
        this(exercise, studentClasses, null);
    }

    public ExerciseCorrection(Exercise exercise, List<Class<?>> studentClasses, String targetClassName) {
        this.exerciseName = exercise == null ? null : exercise.getExerciseName();
        this.grade = null;
        this.obtainedGrade = null;

        this.classes = exercise == null ? new ArrayList<>()
                : initializeClasses(exercise.getClasses(), studentClasses, targetClassName);
    }

    private List<ClassCorrection> initializeClasses(List<Class<?>> templateClasses, List<Class<?>> studentClasses,
            String targetClassName) {
        HashMap<Class<?>, Class<?>> mappedClasses = ClassMapper.mapClasses(templateClasses, studentClasses);
        List<ClassCorrection> corrections = new ArrayList<>();

        mappedClasses.forEach((template, student) -> {
            if (template != null && (targetClassName == null || template.getSimpleName().equalsIgnoreCase(targetClassName)
                        || template.getName().equalsIgnoreCase(targetClassName))) {
                // Se não houver filtro OU se o nome da classe bater com o filtro, adiciona normalmente
                corrections.add(new ClassCorrection(template, student));
            }
        });

        return corrections;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public List<ClassCorrection> getAllClasses() {
        return classes;
    }

    public List<ClassCorrection> getCorrectedClasses() {
        List<ClassCorrection> classesList = new ArrayList<>();

        for (ClassCorrection c : classes) {
            if (c.getTemplate() == null || c.getStudent() == null)
                continue;
            classesList.add(c);
        }

        return classesList;
    }

    public double getGrade() {
        // Evita recalcular a nota toda vez que o método é chamado
        if (grade == null) {
            grade = calculetaGrade();
        }
        return grade;
    }

    private double calculetaGrade() {
        return classes.stream().mapToDouble(ClassCorrection::getGrade).sum();
    }

    public double getObtainedGrade() {
        if (obtainedGrade == null) {
            obtainedGrade = calculateObtainedGrade();
        }
        return obtainedGrade;
    }

    private double calculateObtainedGrade() {
        return classes.stream().mapToDouble(ClassCorrection::getObtainedGrade).sum();
    }

    public List<String> getMissingClasses() {
        List<String> missing = new ArrayList<>();

        for (ClassCorrection c : classes) {
            if (c.getStudent() == null) {
                missing.add(c.getTemplate().getSimpleName());
            }
        }

        return missing;
    }
}
