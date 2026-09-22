package com.javacorrige.controller;

import java.io.File;
import java.util.List;

import com.javacorrige.model.Student;
import com.javacorrige.service.csv.CSVService;

public class CSVController {
    private CSVController() {
    }

    public static void startCSVController(List<Student> students, File fileDirectory) {
        File csvFile = new File(fileDirectory, "correcao.csv");

        CSVService.generateCSV(csvFile, students);
    }
}
