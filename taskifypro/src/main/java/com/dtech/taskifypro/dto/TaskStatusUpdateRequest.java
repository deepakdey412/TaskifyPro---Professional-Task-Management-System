package com.dtech.taskifypro.dto;

import com.dtech.taskifypro.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private TaskStatus status;

    // Getters and Setters
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
}