package com.javacorrige;

import java.io.File;

import com.javacorrige.controller.AppController;
import com.javacorrige.util.validator.InputValidator;
import com.javacorrige.view.GraphicalInterface;

public class Main {
    public static void main(String[] args) {
        if (args.length == 4) {
            // Executa no modo linha de comando
            try {
                String templateDir = args[0];
                String studentsDir = args[1];
                String pdfDir = args[2];
                String stepCorrection = args[3];

                // Validação dos argumentos
                InputValidator.validateArguments(new String[]{templateDir, studentsDir, pdfDir, stepCorrection});

                File templateDirectory = new File(templateDir);
                File studentsDirectory = new File(studentsDir);
                File pdfDirectory = new File(pdfDir);

                // Inicializa o controlador principal
                AppController app = new AppController(templateDirectory, studentsDirectory, pdfDirectory, stepCorrection);
                app.start();

                System.out.println("Software executado com sucesso!");
            } catch (Exception ex) {
                System.out.println("Erro ao carregar programa via linha de comando.");
                GraphicalInterface graphicalInterface = new GraphicalInterface();
                graphicalInterface.show();
            }
        } else if (args.length == 0) {
            // Executa a interface gráfica
            GraphicalInterface graphicalInterface = new GraphicalInterface();
            graphicalInterface.show();
        } else {
            // Número de argumentos inválido
            System.err.println("Uso incorreto do programa.");
            System.err.println("Para executar via linha de comando:");
            System.err.println("java -cp target/javacorrige-1.0-SNAPSHOT-jar-with-dependencies.jar com.javacorrige.Main <diretorioGabarito> <diretorioAlunos> <diretorioPDFs> <passoCorrecao>");
            System.err.println("Ou execute sem argumentos para usar a interface gráfica.");
        }
    }
}

