package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost")
public class StudentController {
    @Autowired
    private StudentRepository repository;

    @GetMapping
    public List<Student> getAllStudents() {
        List<Student> students = repository.findAll();
        System.out.println("Fetched students: " + students);
        return students;
    }
}