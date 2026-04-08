package com.javacorrige.service.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.javacorrige.model.Student;

import java.io.File;
import java.io.IOException;

public class PdfService {

    public static void generateCorrectionReport(Student student, File pdfFile) {
        try {
            PdfWriter writer = new PdfWriter(pdfFile.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            double grade = 0.0;
            double obtainedGrade = 0.0;

            if (student.getReflectionResult() != null) {
                grade = student.getReflectionResult().getGrade();
                obtainedGrade = student.getReflectionResult().getObtainedGrade();
                if (obtainedGrade < 0)
                    obtainedGrade = 0;
            }

            String gradeString = String.format("%.2f / %.2f", obtainedGrade, grade);

            PdfHeaderService.addHeader(document, student.getName(), gradeString);

            if (student.getCompilationResult() == null) {
                document.add(new Paragraph("Nenhum arquivo Java encontrado"));
                document.close();
                return;
            }

            // Exibir erros de compilação se existirem
            if (student.getCompilationResult().getFailedFiles() != null && !student.getCompilationResult().getFailedFiles().isEmpty()) {
                
                Paragraph compErrorHeader = new Paragraph("Atenção: Arquivos com Erro de Compilação")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(ColorConstants.RED)
                        .setMarginTop(10);
                document.add(compErrorHeader);

                // lista o nome de cada arquivo/exercício que falhou
                for (File failedFile : student.getCompilationResult().getFailedFiles()) {
                    String fileName = failedFile.getName().replace(".java", "");
                    Paragraph fileError = new Paragraph("• Falha ao compilar: " + fileName)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.RED)
                            .setMarginLeft(10);
                    document.add(fileError);
                }
            }
            if (student.getReflectionResult() != null) {
                PdfReflectionResultService.addReflectionResult(document, student.getReflectionResult());
            }

            document.close();
        } catch (IOException e) {
            System.err.println("Erro ao gerar o relatório para o aluno " + student.getName() + ": " + e.getMessage());
        }
    }
}
