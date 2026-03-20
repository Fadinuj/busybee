package com.securefromscratch.busybee.auth;

import com.securefromscratch.busybee.storage.Task;
import com.securefromscratch.busybee.storage.TaskComment;
import com.securefromscratch.busybee.storage.TaskNotFoundException;
import com.securefromscratch.busybee.storage.TasksStorage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component("taskAuthorization")
public class TaskAuthorization {

    private final TasksStorage tasksStorage;

    public TaskAuthorization(TasksStorage tasksStorage) {
        this.tasksStorage = tasksStorage;
    }

    public boolean canCloseTask(UUID taskid, Authentication authentication) {
        Task task = tasksStorage.getTaskById(taskid);
        if (task == null) {
            throw new TaskNotFoundException(taskid);
        }

        return canAccessTask(task, authentication);
    }

    public boolean canViewImage(String filename, Authentication authentication) {
        if (filename == null || filename.isBlank()) {
            return false;
        }

        Optional<Task> taskOpt = tasksStorage.findTaskByImage(filename);
        if (taskOpt.isEmpty()) {
            return false;
        }

        return canAccessTask(taskOpt.get(), authentication);
    }

    private boolean canAccessTask(Task task, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }

        String username = authentication.getName();

        if (username.equals(task.createdBy())) {
            return true;
        }

        String[] resp = task.responsibilityOf();
        return resp != null && Arrays.asList(resp).contains(username);
    }
}