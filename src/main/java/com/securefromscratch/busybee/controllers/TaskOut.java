package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.storage.Task;
import com.securefromscratch.busybee.storage.TaskComment;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional; // וודא שהשורה הזו קיימת!
import java.util.UUID;

public record TaskOut(UUID taskid, String name, String desc, LocalDate dueDate, LocalTime dueTime, 
                      String createdBy, String[] responsibilityOf, LocalDateTime creationDatetime, 
                      boolean done, TaskCommentOut[] comments) {

    public static TaskOut fromTask(Task t) {
        Transformer<TaskComment, TaskCommentOut> transformer = TaskCommentOut::fromComment;
        return new TaskOut(
                t.taskid(), t.name(), t.desc(), 
                t.dueDate().orElse(null), 
                t.dueTime().orElse(null), 
                t.createdBy(), t.responsibilityOf(), t.creationDatetime(), t.done(),
                CollectionUtils.collect(t.comments(), transformer).toArray(new TaskCommentOut[0])
        );
    }
}