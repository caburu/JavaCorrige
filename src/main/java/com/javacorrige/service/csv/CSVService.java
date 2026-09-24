package com.javacorrige.service.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.javacorrige.model.Student;
import com.opencsv.CSVWriter;

public class CSVService {

    private static final Logger LOGGER = Logger.getLogger(CSVService.class.getName());
    private static final String[] HEADER = { "Nome", "Nome Exercício", "Nota" };

    private CSVService() {
    }

    /**
     * Cuida da lógica responsável por gerar o arquivo csv contendo as notas dos
     * alunos.
     * 
     * @param csvFile  Arquivo csv onde os dados devem ser inseridos
     * @param students Lista de estudantes que devem ser inclusos no csv
     */
    public static void generateCSV(File csvFile, List<Student> students) {
        try (FileWriter outputFile = new FileWriter(csvFile);
                CSVWriter writer = new CSVWriter(outputFile)) {

            List<String[]> csvLines = new ArrayList<>();
            csvLines.add(HEADER);

            for (Student student : students) {
                // Adiciona uma linha para cada exercício do estudante
                csvLines.addAll(buildStudentExerciseLines(student));
            }

            writer.writeAll(csvLines);
        } catch (IOException e) {
            LOGGER.info("Erro ao gerar arquivo csv! " + e.getMessage());
        }
    }

    /**
     * Gera uma linha no CSV com os dados de um estudante
     * 
     * @param student
     * @return Lista contendo uma array de strings
     */
    private static List<String[]> buildStudentExerciseLines(Student student) {
        if (student == null || student.getReflectionResult() == null
                || student.getReflectionResult().getExercises() == null) {
            return List.of();
        }

        return student.getReflectionResult().getExercises().stream()
                .filter(exercise -> exercise.getGrade() > 0)
                .map(exercise -> new String[] {
                        student.getName(),
                        exercise.getExerciseName(),
                        String.valueOf(exercise.getObtainedGrade())
                })
                .toList();
    }
}