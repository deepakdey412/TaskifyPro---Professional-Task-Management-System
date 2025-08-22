package com.dtech.taskifypro.dto;


import com.dtech.taskifypro.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private String createdByUsername;
    private LocalDateTime createdAt;
    private List<UserResponse> assignedUsers;
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public String getCreatedByUsername() { return createdByUsername; }
    public void setCreatedByUsername(String createdByUsername) { this.createdByUsername = createdByUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<UserResponse> getAssignedUsers() { return assignedUsers; }
    public void setAssignedUsers(List<UserResponse> assignedUsers) { this.assignedUsers = assignedUsers; }
}
