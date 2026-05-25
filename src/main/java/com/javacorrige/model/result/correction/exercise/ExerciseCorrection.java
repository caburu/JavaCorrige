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

    public ExerciseCorrection(Exercise exercise, List<Class<?>> studentClasses) {
        this(exercise, studentClasses, null);
    }

    public ExerciseCorrection(Exercise exercise, List<Class<?>> studentClasses, String targetClassName) {
        this.exerciseName = exercise.getExerciseName();
        this.classes = initializeClasses(exercise.getClasses(), studentClasses, targetClassName);
    }

    private List<ClassCorrection> initializeClasses(List<Class<?>> templateClasses, List<Class<?>> studentClasses,
            String targetClassName) {
        HashMap<Class<?>, Class<?>> mappedClasses = ClassMapper.mapClasses(templateClasses, studentClasses);
        List<ClassCorrection> corrections = new ArrayList<>();

        mappedClasses.forEach((template, student) -> {
            if (template != null) {
                // Se não houver filtro OU se o nome da classe bater com o filtro, adiciona
                // normalmente 
                if (targetClassName == null || template.getSimpleName().equalsIgnoreCase(targetClassName)
                        || template.getName().equalsIgnoreCase(targetClassName)) {
                    corrections.add(new ClassCorrection(template, student));
                }
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
        return classes.stream().mapToDouble(ClassCorrection::getGrade).sum();
    }

    public double getObtainedGrade() {
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
