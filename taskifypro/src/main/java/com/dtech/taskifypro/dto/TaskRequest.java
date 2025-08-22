package com.dtech.taskifypro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotEmpty(message = "At least one user must be assigned")
    private List<Long> assignedUserIds;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getAssignedUserIds() { return assignedUserIds; }
    public void setAssignedUserIds(List<Long> assignedUserIds) { this.assignedUserIds = assignedUserIds; }
}

