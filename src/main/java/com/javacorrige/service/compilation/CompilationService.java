package com.javacorrige.service.compilation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.javacorrige.model.result.compilation.CompilationResult;

public class CompilationService {
    private static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    public static CompilationResult compileClasses(File rootDirectory, List<File> javaFiles, List<File> jarFiles) {
        if (compiler == null) {
            String errorMessage = "Compilador Java não encontrado. Certifique-se de estar utilizando o JDK.";
            System.err.println(errorMessage);
            return new CompilationResult(errorMessage);
        }
        try {
            return compileJavaFiles(rootDirectory, javaFiles, jarFiles);
        } catch (IOException e) {
            String errorMessage = "Erro ao tentar compilar os arquivos: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            return new CompilationResult(errorMessage);
        }
    }

    private static CompilationResult compileJavaFiles(File rootDirectory, List<File> javaFiles, List<File> jarFiles)
            throws IOException {
        // Cria um diretório para os arquivos compilados.
        File compileDir = new File(rootDirectory, "bin");

        // "Escuta" para capturar cada erro de sintaxe, aviso ou falha que o compilador
        // encontrar.
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        // Lista para armazenar os arquivos não compilados
        List<File> failedFiles = new ArrayList<>();

        // Arquivos agrupados por exercícios
        Map<File, List<File>> groupedFiles = javaFiles.stream()
                .collect(Collectors.groupingBy(File::getParentFile));

        try (StandardJavaFileManager fileManager = createFileManager(rootDirectory, compileDir, diagnostics,
                jarFiles)) {
            // Processa cada arquivo individualmente
            for (Map.Entry<File, List<File>> entry : groupedFiles.entrySet()) {
                Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(entry.getValue());
                boolean success = executeCompilation(units, fileManager, diagnostics);

                if (!success) {
                    failedFiles.addAll(entry.getValue());
                }
            }

            // Busca todos os arquivos .class gerados no diretório bin, respeitando a
            // estrutura de pacotes.
            List<File> compiledFiles = com.javacorrige.service.file.FileService.getFilesWithExtension(compileDir,
                    ".class");

            return new CompilationResult(rootDirectory, compileDir, compiledFiles, failedFiles,
                    diagnostics.getDiagnostics(), jarFiles);
        }
    }

    private static StandardJavaFileManager createFileManager(File rootDirectory, File compileDir,
            DiagnosticCollector<JavaFileObject> diagnostics, List<File> jarFiles) throws IOException {
        if (!compileDir.exists() && !compileDir.mkdirs()) {
            String errorMessage = "Não foi possível criar o diretório de compilação: " + compileDir.getPath();
            System.err.println(errorMessage);
            throw new IOException(errorMessage);
        }

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.getDefault(), null);

        // Configurar o classpath no FileManager
        fileManager.setLocation(StandardLocation.CLASS_PATH, jarFiles);

        // Configurar o diretório de saída para os arquivos compilados
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(compileDir));

        return fileManager;
    }

    private static boolean executeCompilation(Iterable<? extends JavaFileObject> compilationUnits,
            StandardJavaFileManager fileManager, DiagnosticCollector<JavaFileObject> diagnostics) {
        try {
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null,
                    compilationUnits);
            return task.call();
        } catch (Exception e) {
            String errorMessage = "Erro durante a execução da compilação: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
            return false;
        }
    }
}
