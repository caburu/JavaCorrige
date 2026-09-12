package com.javacorrige.controller;

import java.io.File;
import java.util.List;

import com.javacorrige.model.Student;
import com.javacorrige.model.template.Template;
import com.javacorrige.util.processor.StudentProcessor;
import com.javacorrige.util.processor.TemplateProcessor;

public class AppController {
    private File templateDirectory;
    private File studentsDirectory;
    private File pdfDirectory;
    private String stepCorrection;

    public AppController(File templateDirectory, File studentsDirectory, File pdfDirectory, String stepCorrection) {
        this.templateDirectory = templateDirectory;
        this.studentsDirectory = studentsDirectory;
        this.pdfDirectory = pdfDirectory;
        this.stepCorrection = stepCorrection;
    }

    public void start() {
        start(null);
    }

    public void start(String targetClassName) {
        Template template = TemplateProcessor.processTemplateDirectory(templateDirectory, stepCorrection);
        List<Student> students = StudentProcessor.processStudentDirectory(studentsDirectory);

        CorrectionController correctionController = new CorrectionController(template, students);
        correctionController.start(targetClassName);

        PdfController pdfController = new PdfController(students, pdfDirectory);
        pdfController.start();
    }
}
