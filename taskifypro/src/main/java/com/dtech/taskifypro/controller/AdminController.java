package com.dtech.taskifypro.controller;

import com.dtech.taskifypro.dto.TaskRequest;
import com.dtech.taskifypro.dto.TaskResponse;
import com.dtech.taskifypro.dto.UserResponse;
import com.dtech.taskifypro.entity.TaskStatus;
import com.dtech.taskifypro.service.TaskService;
import com.dtech.taskifypro.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private final TaskService taskService;
    @Autowired
    private final UserService userService;

    public AdminController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        List<TaskResponse> tasks = taskService.getAllTasks();
        List<UserResponse> users = userService.getAllUsers();

        model.addAttribute("tasks", tasks);
        model.addAttribute("users", users);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("taskStatuses", TaskStatus.values());

        return "admin/dashboard";
    }

    @GetMapping("/tasks/create")
    public String createTaskForm(Model model) {
        List<UserResponse> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("taskRequest", new TaskRequest());
        return "admin/create-task";
    }

    @PostMapping("/tasks/create")
    public String createTask(@Valid @ModelAttribute TaskRequest taskRequest,
                             BindingResult bindingResult,
                             Authentication authentication,
                             Model model) {
        if (bindingResult.hasErrors()) {
            List<UserResponse> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "admin/create-task";
        }

        try {
            taskService.createTask(taskRequest, authentication.getName());
            return "redirect:/admin/dashboard?success=Task created successfully";
        } catch (Exception e) {
            List<UserResponse> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("error", e.getMessage());
            return "admin/create-task";
        }
    }

    @PostMapping("/tasks/{taskId}/reassign")
    public String reassignTask(@PathVariable Long taskId,
                               @RequestParam List<Long> userIds) {
        try {
            taskService.reassignTask(taskId, userIds);
            return "redirect:/admin/dashboard?success=Task reassigned successfully";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return "redirect:/admin/dashboard?success=Task deleted successfully";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=" + e.getMessage();
        }
    }
}