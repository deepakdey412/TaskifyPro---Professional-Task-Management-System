package com.dtech.taskifypro.service;

import com.dtech.taskifypro.dto.TaskRequest;
import com.dtech.taskifypro.dto.TaskResponse;
import com.dtech.taskifypro.dto.UserResponse;
import com.dtech.taskifypro.entity.Task;
import com.dtech.taskifypro.entity.TaskStatus;
import com.dtech.taskifypro.entity.User;
import com.dtech.taskifypro.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, String createdByUsername) {
        User createdBy = userService.findByUsername(createdByUsername);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCreatedBy(createdBy.getId());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // Assign users
        Set<User> assignedUsers = new HashSet<>();
        for (Long userId : request.getAssignedUserIds()) {
            User user = userService.findById(userId);
            assignedUsers.add(user);
        }
        task.setAssignedUsers(assignedUsers);

        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAllTasksWithUsers()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUserId(Long userId) {
        return taskRepository.findTasksByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return convertToResponse(task);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    @Transactional
    public TaskResponse reassignTask(Long taskId, List<Long> userIds) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        // Clear existing assignments and add new ones
        Set<User> newAssignedUsers = new HashSet<>();
        for (Long userId : userIds) {
            User user = userService.findById(userId);
            newAssignedUsers.add(user);
        }

        task.setAssignedUsers(newAssignedUsers);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());

        // Get creator username
        User creator = userService.findById(task.getCreatedBy());
        response.setCreatedByUsername(creator.getUsername());

        // Convert assigned users
        List<UserResponse> assignedUsers = task.getAssignedUsers()
                .stream()
                .map(this::convertUserToResponse)
                .collect(Collectors.toList());
        response.setAssignedUsers(assignedUsers);

        return response;
    }

    private UserResponse convertUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }
}