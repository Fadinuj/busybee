package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.storage.TaskComment;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCommentOut(UUID commentid, String text, String image, String attachment, 
                             String createdBy, LocalDateTime createdOn, int indent) {

    public static TaskCommentOut fromComment(TaskComment c) {
        return new TaskCommentOut(
                c.commentId(),
                c.text(),
                c.image().orElse(null),      // שימוש ב-Optional
                c.attachment().orElse(null), // שימוש ב-Optional
                c.createdBy(),
                c.createdOn(),               // וודא שב-TaskComment קיימת מתודה createdOn()
                c.indent()
        );
    }
}