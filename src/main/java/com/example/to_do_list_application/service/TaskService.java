package com.example.to_do_list_application.service;

import com.example.to_do_list_application.exception.CircularDependencyException;
import com.example.to_do_list_application.model.Task;
import com.example.to_do_list_application.repository.TaskRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        if (task.getDependencies() != null && !task.getDependencies().isEmpty()) {
            validateCircularDependency(task, new HashSet<>(), task.getId());
        }

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setPriority(taskDetails.getPriority());
        task.setStatus(taskDetails.getStatus());
        if (taskDetails.getDependencies() != null) {
            Set<Task> newDependencies = new HashSet<>();
            for (Task dep : taskDetails.getDependencies()) {
                Task dependencyTask = taskRepository.findById(dep.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Dependency not found: " + dep.getId()));
                newDependencies.add(dependencyTask);
            }
            for (Task dependency : newDependencies) {
                validateCircularDependency(dependency, new HashSet<>(), task.getId());
            }

            task.setDependencies(newDependencies);
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public void addDependency(Long taskId, Long dependencyId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        Task dependency = taskRepository.findById(dependencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Dependency task not found with id: " + dependencyId));

        try {
            validateCircularDependency(dependency, new HashSet<>(), taskId);
            task.getDependencies().add(dependency);
            taskRepository.save(task);
        } catch (CircularDependencyException e) {
            throw new CircularDependencyException("Circular dependency detected: Task " + taskId + " cannot depend on Task " + dependencyId);
        }
    }

    public void removeDependency(Long taskId, Long dependencyId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.getDependencies().removeIf(t -> t.getId().equals(dependencyId));
        taskRepository.save(task);
    }
    public Set<Task> getAllDependencies(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        Set<Task> allDependencies = new HashSet<>();
        collectDependencies(task, allDependencies);
        return allDependencies;
    }

    private void collectDependencies(Task task, Set<Task> allDependencies) {
        for (Task dependency : task.getDependencies()) {
            if (allDependencies.add(dependency)) {
                collectDependencies(dependency, allDependencies);
            }
        }
    }
    private void validateCircularDependency(Task task, Set<Long> visited, Long originalTaskId) {
        if (!visited.add(task.getId())) {
            throw new CircularDependencyException("Circular dependency detected: Task " + originalTaskId + " creates a loop!");
        }
        if (task.getId().equals(originalTaskId)) {
            throw new CircularDependencyException("Circular dependency detected: Task " + task.getId());
        }
        for (Task dependency : task.getDependencies()) {
            validateCircularDependency(dependency, visited, originalTaskId);
        }
    }
}
