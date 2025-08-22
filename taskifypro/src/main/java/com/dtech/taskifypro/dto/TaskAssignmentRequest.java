package com.dtech.taskifypro.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class TaskAssignmentRequest {
    @NotEmpty(message = "User IDs cannot be empty")
    private List<Long> userIds;

    // Getters and Setters
    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
}