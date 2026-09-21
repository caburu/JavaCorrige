package com.javacorrige.service.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.javacorrige.model.Student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.logging.Logger;

public class PdfService {

    private static final Logger LOGGER = Logger.getLogger(PdfService.class.getName());

    private PdfService() {
    }

    /**
     * Gera o relatório para um estudante.
     * 
     * @param student Estudante para qual o relatório deve ser gerado.
     * @param pdfFile Arquivo em que o relatório deve ser gerado.
     */
    public static void generateCorrectionReport(Student student, File pdfFile) {
        try {
            Document document = setupDocument(pdfFile);

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

            if (student.getCompilationResult().getFailedFiles() != null
                    && !student.getCompilationResult().getFailedFiles().isEmpty()) {

                Paragraph compErrorHeader = new Paragraph("arquivos com erro de compilação:")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(ColorConstants.RED)
                        .setMarginTop(10);
                document.add(compErrorHeader);

                for (File failedFile : student.getCompilationResult().getFailedFiles()) {
                    String fileName = failedFile.getName().replace(".java", "");

                    // busca o primeiro erro associado a este arquivo específico
                    String firstErrorMessage = student.getCompilationResult().getDiagnostics().stream()
                            .filter(d -> d.getSource() != null
                                    && d.getSource().getName().contains(failedFile.getName()))
                            .map(d -> d.getMessage(java.util.Locale.getDefault()))
                            .findFirst()
                            .orElse("erro de sintaxe desconhecido");

                    // exibe: nome da classe - causa do erro
                    Paragraph fileError = new Paragraph("• " + fileName + ": " + firstErrorMessage)
                            .setFontSize(11)
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
            LOGGER.info("Erro ao gerar o relatório para o aluno " + student.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Gera um relatório geral contendo as notas de todos os estudantes.
     * 
     * @param pdfFile  Arquivo do relatório geral
     * @param students Lista contendo os estudantes que devem ser incluidos no
     *                 relatório geral
     */
    public static void generateGeneralReport(File pdfFile, List<Student> students, String templateDirectory) {
        try {
            // Formatação inicial do documento
            Document document = setupDocument(pdfFile);

            // Header setup
            PdfHeaderService.addGeneralReportHeader(document, templateDirectory);

            // Adiciona a correção dos exercícios no relatório
            PdfReflectionResultService.addReflectionResultGeneralReport(document, students);

            document.close();
        } catch (IOException e) {
            LOGGER.info("Erro ao gerar o relatório geral: " + e.getMessage());
        }
    }

    /**
     * Faz a configuração inicial do documento.
     * 
     * @param pdfFile Arquivo do documento.
     * @return Documento formatado.
     * @throws FileNotFoundException
     */
    private static Document setupDocument(File pdfFile) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(pdfFile.getAbsolutePath());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        return document;
    }

}
