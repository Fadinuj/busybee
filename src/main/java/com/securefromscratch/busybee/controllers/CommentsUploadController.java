package com.securefromscratch.busybee.controllers;

import com.securefromscratch.busybee.safety.CommentText;
import com.securefromscratch.busybee.storage.FileStorage;
import com.securefromscratch.busybee.storage.Task;
import com.securefromscratch.busybee.storage.TaskNotFoundException;
import com.securefromscratch.busybee.storage.TasksStorage;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@RestController
public class CommentsUploadController {

    @Autowired
    private TasksStorage m_tasks;

    public record AddCommentFields(
            @NotNull UUID taskid,
            Optional<UUID> commentid,
            @NotNull CommentText text
    ) {}

    public record CreatedCommentId(UUID commentid) {}

    @PostMapping(value = "/comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CreatedCommentId> addComment(
            @RequestPart("commentFields") AddCommentFields commentFields,
            @RequestPart(value = "file", required = false) Optional<MultipartFile> optFile,
            Authentication authentication
    ) throws IOException {

        Optional<Task> t = m_tasks.find(commentFields.taskid());
        if (t.isEmpty()) {
            throw new TaskNotFoundException(commentFields.taskid());
        }

        String currentUser = authentication.getName();

        if (optFile.isEmpty() || optFile.get().isEmpty()) {
            UUID newComment = m_tasks.addComment(
                    t.get(),
                    commentFields.text().get(),
                    currentUser,
                    commentFields.commentid()
            );
            return ResponseEntity.ok(new CreatedCommentId(newComment));
        }

        FileStorage fileStorage = new FileStorage(Paths.get("uploads"));

        String storedFilename;
        try {
            storedFilename = fileStorage.store(optFile.get());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        FileStorage.FileType filetype = FileStorage.identifyType(optFile.get());

        Optional<String> imageFilename =
                (filetype == FileStorage.FileType.IMAGE)
                        ? Optional.of(storedFilename)
                        : Optional.empty();

        Optional<String> attachFilename =
                (filetype != FileStorage.FileType.IMAGE)
                        ? Optional.of(storedFilename)
                        : Optional.empty();

        UUID newComment = m_tasks.addComment(
                t.get(),
                commentFields.text().get(),
                imageFilename,
                attachFilename,
                currentUser,
                commentFields.commentid()
        );

        return ResponseEntity.ok(new CreatedCommentId(newComment));
    }
}