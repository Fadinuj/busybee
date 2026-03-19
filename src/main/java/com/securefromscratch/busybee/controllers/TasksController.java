package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.safety.Description;
import com.securefromscratch.busybee.safety.FutureDate;
import com.securefromscratch.busybee.safety.Name;
import com.securefromscratch.busybee.safety.ResponsibilityOf;
import com.securefromscratch.busybee.safety.ValidTime;
import com.securefromscratch.busybee.storage.Task;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import com.securefromscratch.busybee.storage.TasksStorage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "null")
public class TasksController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TasksController.class);
    private static final Pattern SAFE_TEXT = Pattern.compile("^[a-zA-Z0-9 \\-_.!?#א-ת]*$");

    @Autowired
    private TasksStorage m_tasks;

    // Request: No arguments
    // Expected Response: [
    //    {
    //       "taskid": "<UUID>",
    //       "name": "<name>",
    //       "desc": "<desc>",
    //       "dueDate": "<date>",  // this is optional
    //       "dueTime": "<time>",  // this is optional
    //       "createdBy": "<name of user>",
    //       "responsibilityOf": [ "<user1">, "<user2>", ...],
    //       "creationDatetime": "<date+time>",
    //       "done": false/true,
    //       "comments": [ { comment1 }, { comment2 }, ... ] (see TaskCommentOut for fields)
    //    }, ...
    // ]
    @GetMapping("/tasks")
    public Collection<TaskOut> getTasks() {
        List<Task> allTasks = m_tasks.getAll();
        Transformer<Task, TaskOut> transformer = t-> TaskOut.fromTask((Task)t);
        return CollectionUtils.collect(allTasks, transformer);
    }

    public record DoneDTO(UUID taskid) {
    }
    // Request: { "taskid": "<uuid>" }
    // Expected Response: { "success": true/false }
    @PreAuthorize("@taskAuthorization.canCloseTask(#input.taskid(), #authentication)")
    @PostMapping("/done")
    public ResponseEntity<?> markTaskDone(@RequestBody DoneDTO input, Authentication authentication) throws IOException {
        boolean alreadyDone = m_tasks.markDone(input.taskid());
        return ResponseEntity.ok(Map.of("success", !alreadyDone));
    }







    public record CreateTaskRequest(
        Name name,
        Description desc,
        FutureDate dueDate,
        ValidTime dueTime,
        ResponsibilityOf responsibilityOf
    ) {}
    public record CreateTaskResponse(UUID taskid) {}


    // Request: {
    //     "name": "<task name>",
    //     "desc": "<description>",
    //     "dueDate": "<date>", // or null
    //     "dueTime": "<time>", // or null
    //     "responsibilityOf": [ "<name1>", "<name2>", ... ]
    // }
    // Expected Response: { "taskid": "<uuid>" }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTaskRequest request, Authentication authentication) throws IOException {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("CONTROLLER REACHED - ATTEMPTING TO SAVE TASK: " + request.name());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        String currentUser = authentication.getName();

        // 1. בדיקת הרשאות TRIAL
        boolean isTrial = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TRIAL"));
        if (isTrial && m_tasks.getAll().stream().anyMatch(t -> t.createdBy().equals(currentUser) && !t.done())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Trial limit reached");
        }

        // 2. ולידציית זמן משולבת
        if (request.dueTime() != null && request.dueDate() != null && request.dueTime().isPastWhen(request.dueDate().getDate())) {
            throw new IllegalArgumentException("The combined date and time is in the past");
        }

        // 3. בדיקת כפילות
        if (m_tasks.getAll().stream().anyMatch(t -> t.name().equalsIgnoreCase(request.name().toString()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Task name already exists");
        }

        // 4. יצירה
        UUID newTaskId = m_tasks.add(
            request.name().toString(),
            request.desc().get(),
            request.dueDate() != null ? request.dueDate().getDate() : null,
            request.dueTime() != null ? request.dueTime().getTime() : null,
            currentUser,
            request.responsibilityOf() != null ? request.responsibilityOf().toArray() : new String[0]
        );

        LOGGER.info("Task created successfully with ID: {}", newTaskId);
        return ResponseEntity.ok(Map.of("taskid", newTaskId));
    }
}
