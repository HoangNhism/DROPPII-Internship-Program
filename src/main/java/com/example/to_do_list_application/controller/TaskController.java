package com.example.to_do_list_application.controller;

import com.example.to_do_list_application.model.Task;
import com.example.to_do_list_application.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public Task createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskService.updateTask(id, taskDetails);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/dependencies/{dependencyId}")
    public ResponseEntity<?> addDependency(@PathVariable Long taskId, @PathVariable Long dependencyId) {
        taskService.addDependency(taskId, dependencyId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/dependencies/{dependencyId}")
    public ResponseEntity<?> removeDependency(@PathVariable Long taskId, @PathVariable Long dependencyId) {
        taskService.removeDependency(taskId, dependencyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/dependencies")
    public ResponseEntity<Set<Task>> getAllTaskDependencies(@PathVariable Long id) {
        Set<Task> dependencies = taskService.getAllDependencies(id);
        return ResponseEntity.ok(dependencies);
    }
}
