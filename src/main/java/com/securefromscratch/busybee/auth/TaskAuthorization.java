package com.securefromscratch.busybee.auth;

import com.securefromscratch.busybee.storage.Task;
import com.securefromscratch.busybee.storage.TasksStorage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component("taskAuthorization")
public class TaskAuthorization {

    private final TasksStorage tasksStorage;

    public TaskAuthorization(TasksStorage tasksStorage) {
        this.tasksStorage = tasksStorage;
    }

    public boolean canCloseTask(UUID taskid, Authentication authentication) {
        Task task = tasksStorage.find(taskid).orElse(null);
        System.out.println("DONE found=" + (task != null));
        if (task != null) {
            System.out.println("DONE owner=" + task.createdBy()
                    + " resp=" + Arrays.toString(task.responsibilityOf()));
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // ADMIN תמיד מותר
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        // Owner או אחראי
        String username = authentication.getName();
        //Task task = tasksStorage.find(taskid).orElse(null);
        if (task == null) return false;

        if (username.equals(task.createdBy())) return true;

        // optional: responsibilityOf
        String[] resp = task.responsibilityOf();
    
        return resp != null && Arrays.asList(resp).contains(username);
    }
}