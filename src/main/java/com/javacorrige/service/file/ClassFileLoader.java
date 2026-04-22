package com.javacorrige.service.file;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

public class ClassFileLoader {

    public static List<Class<?>> loadClasses(File rootDirectory, List<File> compiledClassFiles, List<File> dependencyFiles) {
        List<Class<?>> classes = new ArrayList<>();
        List<URL> urls = new ArrayList<>();

        try {
            // Adicionar o diretório com as classes compiladas
            urls.add(rootDirectory.toURI().toURL());

            // Adicionar os arquivos JAR de dependências
            for (File dependencyFile : dependencyFiles) {
                if (dependencyFile.exists() && dependencyFile.getName().endsWith(".jar")) {
                    urls.add(dependencyFile.toURI().toURL());
                } else {
                    System.err.println("Arquivo de dependência inválido: " + dependencyFile.getAbsolutePath());
                }
            }

            // Criar o ClassLoader com todas as URLs
            try (URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader())) {
                // Carregar as classes compiladas do diretório de saída
                loadClassesFromFiles(compiledClassFiles, rootDirectory, classLoader, classes);

                // Carregar as classes dos arquivos JAR de dependências
                loadClassesFromJars(dependencyFiles, classLoader);
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Erro ao criar o URLClassLoader. URL inválida para o diretório ou arquivos de dependência.", e);
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao manipular arquivos ou fechar o ClassLoader.", e);
        }

        return classes;
    }

    private static void loadClassesFromFiles(List<File> compiledClassFiles, File rootDirectory, URLClassLoader classLoader, List<Class<?>> classes) {
        for (File classFile : compiledClassFiles) {
            try {
                String className = extractClassName(classFile, rootDirectory);
                Class<?> clazz = classLoader.loadClass(className);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                System.err.println("Classe não encontrada no arquivo: " + classFile.getAbsolutePath());
            } catch (NoClassDefFoundError e) {
                System.err.println("Erro ao carregar definição da classe: " + classFile.getAbsolutePath() + " - " + e.getMessage());
            }
        }
    }

    private static void loadClassesFromJars(List<File> dependencyFiles, URLClassLoader classLoader) {
        for (File jarFile : dependencyFiles) {
            if (jarFile.getName().endsWith(".jar")) {
                try (JarFile jar = new JarFile(jarFile)) {
                    jar.stream()
                        .filter(entry -> entry.getName().endsWith(".class")) // Filtra apenas as classes
                        .forEach(entry -> loadClassFromJar(entry, classLoader));
                } catch (IOException e) {
                    throw new IllegalStateException("Erro ao ler o arquivo JAR: " + jarFile.getAbsolutePath());
                }
            }
        }
    }

    private static void loadClassFromJar(JarEntry entry, URLClassLoader classLoader) {
        try {
            String className = entry.getName().replace("/", ".").replace(".class", "");
            classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Classe não encontrada no JAR: " + entry.getName());
        }
    }

    private static String extractClassName(File classFile, File rootDirectory) {
        return classFile.getAbsolutePath()
                .replace(rootDirectory.getAbsolutePath() + File.separator, "")
                .replace(".class", "")
                .replace(File.separator, ".");
    }
}
