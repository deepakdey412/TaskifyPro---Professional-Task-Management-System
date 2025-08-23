package com.dtech.taskifypro.controller;

import com.dtech.taskifypro.dto.TaskResponse;
import com.dtech.taskifypro.entity.Task;
import com.dtech.taskifypro.entity.TaskStatus;
import com.dtech.taskifypro.entity.User;
import com.dtech.taskifypro.repository.TaskRepository;
import com.dtech.taskifypro.repository.UserRepository;
import com.dtech.taskifypro.service.TaskService;
import com.dtech.taskifypro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private final TaskService taskService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TaskRepository taskRepository;

    public UserController(TaskService taskService, UserService userService, UserRepository userRepository, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        List<TaskResponse> tasks = taskService.getTasksByUserId(currentUser.getId());

        model.addAttribute("tasks", tasks);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("taskStatuses", TaskStatus.values());

        return "user/dashboard";
    }

    @PostMapping("/tasks/{taskId}/status")
    public String updateTaskStatus(@PathVariable Long taskId,
                                   @RequestParam TaskStatus status,
                                   Authentication authentication) {
        try {
            // Verify user has access to this task
            User currentUser = userService.findByUsername(authentication.getName());
            TaskResponse task = taskService.getTaskById(taskId);

            boolean hasAccess = task.getAssignedUsers().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));

            if (!hasAccess) {
                return "redirect:/user/dashboard?error=Access denied";
            }

            taskService.updateTaskStatus(taskId, status);
            return "redirect:/user/dashboard?success=Task status updated";
        } catch (Exception e) {
            return "redirect:/user/dashboard?error=" + e.getMessage();
        }
    }

    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable Long taskId,
                           Model model,
                           Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            TaskResponse task = taskService.getTaskById(taskId);

            // Verify user has access to this task
            boolean hasAccess = task.getAssignedUsers().stream()
                    .anyMatch(user -> user.getId().equals(currentUser.getId()));

            if (!hasAccess) {
                return "redirect:/user/dashboard?error=Access denied";
            }

            model.addAttribute("task", task);
            model.addAttribute("taskStatuses", TaskStatus.values());
            return "user/task-detail";
        } catch (Exception e) {
            return "redirect:/user/dashboard?error=" + e.getMessage();
        }
    }


    @GetMapping("/user/dashboard")
    public String userDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login?error=User not found";
        User user = userOpt.get();

        List<Task> tasks = taskRepository.findByAssignedUsers(user);

        long total = tasks.size();
        long pending = tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDING).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();

        model.addAttribute("username", username);
        model.addAttribute("tasks", tasks);
        model.addAttribute("totalTasks", total);
        model.addAttribute("pendingTasks", pending);
        model.addAttribute("inProgressTasks", inProgress);
        model.addAttribute("completedTasks", completed);
        model.addAttribute("taskStatuses", TaskStatus.values());

        return "user/dashboard";
    }


}