package com.javacorrige.util.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.javacorrige.model.Student;
import com.javacorrige.model.result.compilation.CompilationResult;
import com.javacorrige.service.compilation.CompilationService;
import com.javacorrige.service.file.ClassFileLoader;
import com.javacorrige.service.file.FileService;
public class StudentProcessor {

    public static List<Student> processStudentDirectory(File studentRootDirectory) {
        FileService.unpackAll(studentRootDirectory);

        List<Student> students = new ArrayList<>();
        File[] subDirs = FileService.getDirectSubdirectories(studentRootDirectory);

        for (File subDir : subDirs) {
            Student student = processSingleStudentDirectory(subDir);
            if (student != null) {
                students.add(student);
            }
        }

        return students;
    }

    private static Student processSingleStudentDirectory(File studentDirectory) {
        String studentName = getStudentName(studentDirectory.getName());
        List<File> javaFiles = FileService.getFilesWithExtension(studentDirectory, ".java");
        List<File> jarFiles = FileService.getFilesWithExtension(studentDirectory, ".jar");

        if (javaFiles.isEmpty()) {
            System.err.println("Nenhum arquivo Java encontrado no diretório: " + studentName);
            return new Student(studentName, Collections.emptyList(), null);
        }

        CompilationResult compilationResult = CompilationService.compileClasses(studentDirectory, javaFiles, jarFiles);

        List<Class<?>> loadedClasses = Collections.emptyList();
        if (compilationResult.getCompilationDirectory() != null && compilationResult.getCompiledFiles() != null) {
            loadedClasses = ClassFileLoader.loadClasses(compilationResult.getCompilationDirectory(), 
                                            compilationResult.getCompiledFiles(), compilationResult.getJarFiles());
        }
                                        
        return new Student(studentName, loadedClasses, compilationResult);
    }

    private static String getStudentName(String studentName){
        if (studentName.contains(" - ")) {
            studentName = studentName.split(" - ")[0].trim();
        }
        return studentName;
    }
}
