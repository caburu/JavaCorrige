package com.javacorrige.model.result.compilation;

import java.io.File;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class CompilationResult {
    private final File rootDirectory;
    private final File compilationDirectory;
    private final List<File> compiledFiles;
    private final List<File> failedFiles;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;
    private final List<File> jarFiles;
    private final String errorDetails;

    public CompilationResult(File rootDirectory, File compilationDirectory, List<File> compiledFiles, List<File> failedFiles, List<Diagnostic<? extends JavaFileObject>> diagnostics, List<File> jarFiles) {
        this.rootDirectory = rootDirectory;
        this.compilationDirectory = compilationDirectory;
        this.diagnostics = diagnostics;
        this.compiledFiles = compiledFiles;
        this.failedFiles = failedFiles;
        this.jarFiles = jarFiles;
        errorDetails = null;
    }

    public CompilationResult(String errorDetails){
        this.errorDetails = errorDetails;
        rootDirectory = null;
        compilationDirectory = null;
        diagnostics = null;
        compiledFiles = null;
        failedFiles = null;
        jarFiles = null;
    }

    public File getRootDirectory(){ return rootDirectory; }
    public File getCompilationDirectory(){ return compilationDirectory; }
    // Retorna falso caso tenha ocorrido um erro na compilação
    public boolean isSuccess() { return compiledFiles != null && !compiledFiles.isEmpty(); }
    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {  return diagnostics; }
    public List<File> getCompiledFiles() { return compiledFiles; }
    public List<File> getFailedFiles() { return failedFiles; }
    public List<File> getJarFiles(){ return jarFiles; }
    public String getErrorDetails(){ return errorDetails; }
}