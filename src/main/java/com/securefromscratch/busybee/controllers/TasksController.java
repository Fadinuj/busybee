package com.securefromscratch.busybee.controllers;

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







    public record CreateTaskRequest(String name) {}
    public record CreateTaskResponse(UUID taskid) {}
    
    
    private void validate(CreateTaskRequest request) {
        if (request == null)
            throw new IllegalArgumentException("Request is null");

        if (request.name() == null || request.name().isBlank())
            throw new IllegalArgumentException("Task name is required");

        if (request.name().length() > 100)
            throw new IllegalArgumentException("Task name too long");

        if (!SAFE_TEXT.matcher(request.name()).matches())
            throw new IllegalArgumentException("Task name contains invalid characters");
    }


    // Request: {
    //     "name": "<task name>",
    //     "desc": "<description>",
    //     "dueDate": "<date>", // or null
    //     "dueTime": "<time>", // or null
    //     "responsibilityOf": [ "<name1>", "<name2>", ... ]
    // }
    // Expected Response: { "taskid": "<uuid>" }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateTaskRequest request) {
    try {
            validate(request);
            UUID newTaskId = m_tasks.add(request.name(),"no description, yet",new String[0]);
            return ResponseEntity.ok().body(new CreateTaskResponse(newTaskId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("Invalid input: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Failed creating a task with parameters {0}", request, e);
            return ResponseEntity.internalServerError().body("IO Exception");
        }
    }
}
