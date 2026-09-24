package com.javacorrige.controller;

import java.io.File;
import java.util.List;

import com.javacorrige.model.Student;
import com.javacorrige.service.csv.CSVService;

public class CSVController {
    private CSVController() {
    }

    /**
     * Gera um arquivo csv contendo as notas finais dos estudantes no exercício corrigido.
     * 
     * @param students lista de estudantes que devem ser inclusos no csv 
     * @param fileDirectory diretório onde o csv deve ser armazenado
     */
    public static void startCSVController(List<Student> students, File fileDirectory) {
        File csvFile = new File(fileDirectory, "correcao.csv");

        CSVService.generateCSV(csvFile, students);
    }
}
