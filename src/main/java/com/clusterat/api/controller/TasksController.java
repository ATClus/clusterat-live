package com.clusterat.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
class TasksController {
    @GetMapping
    public String getTasks() {
        return "Hello World!";
    }
}
