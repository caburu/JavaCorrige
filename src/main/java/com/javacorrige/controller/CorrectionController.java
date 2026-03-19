package com.javacorrige.controller;

import java.util.List;

import com.javacorrige.model.Student;
import com.javacorrige.model.result.correction.ReflectionResult;
import com.javacorrige.model.template.Template;

public class CorrectionController {
    
    private Template template;
    private List<Student> students;

    public CorrectionController(Template template, List<Student> students){
        this.template = template;
        this.students = students;
    }

    public void start(){
        for (Student student : students) {
            student.setReflectionResult(new ReflectionResult(template, student.getClasses()));
        }
    }
}
