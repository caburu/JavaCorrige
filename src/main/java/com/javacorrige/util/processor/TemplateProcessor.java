package com.javacorrige.util.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.javacorrige.model.result.compilation.CompilationResult;
import com.javacorrige.model.template.Template;
import com.javacorrige.model.template.exercise.Exercise;
import com.javacorrige.service.compilation.CompilationService;
import com.javacorrige.service.file.ClassFileLoader;
import com.javacorrige.service.file.FileService;

public class TemplateProcessor {

    public static Template processTemplateDirectory(File templateDirectory, String stepCorrection) {
        File[] subDirs = FileService.organizeStepDirectories(templateDirectory, stepCorrection);

        String templateName = templateDirectory.getName();
        List<Exercise> exercises = new ArrayList<>();

        // File[] subDirs = FileService.getDirectSubdirectories(templateDirectory);

        for (File subDir : subDirs) {
            Exercise exercise = processSingleExerciseDirectory(subDir);
            exercises.add(exercise);
        }

        return new Template(templateName, exercises);
    }

    private static Exercise processSingleExerciseDirectory(File exerciseDirectory) {
        String exerciseName = exerciseDirectory.getName();
        List<File> javaFiles = FileService.getFilesWithExtension(exerciseDirectory, ".java");
        List<File> jarFiles = FileService.getFilesWithExtension(exerciseDirectory, ".jar");

        if (javaFiles.isEmpty()) {
            // TODO: adicionar logger
            return null;
        }

        CompilationResult compilationResult = CompilationService.compileClasses(exerciseDirectory, javaFiles, jarFiles);

        if (!compilationResult.isSuccess()) {
            StringBuilder errorMessage = new StringBuilder("Gabarito não pode conter erros de compilação: " + exerciseName + "\n");

            compilationResult.getDiagnostics().forEach(diagnostic -> {
                errorMessage.append("Código: ").append(diagnostic.getCode()).append("\n")
                            .append("Mensagem: ").append(diagnostic.getMessage(Locale.getDefault())).append("\n")
                            .append("Localização: ").append(diagnostic.getSource()).append(" linha: ")
                            .append(diagnostic.getLineNumber()).append("\n")
                            .append("----------------------------------------------------\n");
            });

            throw new IllegalStateException(errorMessage.toString());
        }

        List<Class<?>> loadedClasses = loadCompiledClasses(compilationResult, exerciseName);
        return new Exercise(exerciseName, loadedClasses);
    }

    private static List<Class<?>> loadCompiledClasses(CompilationResult compilationResult, String exerciseName) {
        try {
            return ClassFileLoader.loadClasses(compilationResult.getCompilationDirectory(), compilationResult.getCompiledFiles(), compilationResult.getJarFiles());
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao carregar classes compiladas para o gabarito -> exercício: " + exerciseName, e);
        }
    }
}
