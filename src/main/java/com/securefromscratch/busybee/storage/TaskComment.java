package com.securefromscratch.busybee.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID m_commentId;
    private final String m_text;
    private final Optional<String> m_image;
    private final Optional<String> m_attachment;
    private final String m_createdBy;
    private final LocalDateTime m_createdOn;
    private final int m_indent;

    public TaskComment() {
        this.m_commentId = UUID.randomUUID();
        this.m_text = "";
        this.m_image = Optional.empty();
        this.m_attachment = Optional.empty();
        this.m_createdBy = "";
        this.m_createdOn = LocalDateTime.now();
        this.m_indent = 0;
    }

    @JsonCreator
    public TaskComment(
            @JsonProperty("commentId") UUID commentId,
            @JsonProperty("text") String text,
            @JsonProperty("image") String image,
            @JsonProperty("attachment") String attachment,
            @JsonProperty("createdBy") String createdBy,
            @JsonProperty("createdOn") LocalDateTime createdOn,
            @JsonProperty("indent") int indent
    ) {
        this.m_commentId = (commentId != null) ? commentId : UUID.randomUUID();
        this.m_text = (text != null) ? text : "";
        this.m_image = Optional.ofNullable(image);
        this.m_attachment = Optional.ofNullable(attachment);
        this.m_createdBy = (createdBy != null) ? createdBy : "";
        this.m_createdOn = (createdOn != null) ? createdOn : LocalDateTime.now();
        this.m_indent = indent;
    }

    public TaskComment(String text, String createdBy, int indent) {
        this(text, Optional.empty(), Optional.empty(), createdBy, LocalDateTime.now(), indent);
    }

    public TaskComment(String text, String createdBy, LocalDateTime createdOn, int indent) {
        this(text, Optional.empty(), Optional.empty(), createdBy, createdOn, indent);
    }

    public TaskComment(String text,
                       Optional<String> image,
                       Optional<Object> attachment,
                       String createdBy,
                       LocalDateTime createdOn,
                       int indent) {
        this.m_commentId = UUID.randomUUID();
        this.m_text = (text != null) ? text : "";
        this.m_image = (image != null) ? image : Optional.empty();
        this.m_attachment = (attachment != null && attachment.isPresent())
                ? Optional.of(attachment.get().toString())
                : Optional.empty();
        this.m_createdBy = (createdBy != null) ? createdBy : "";
        this.m_createdOn = (createdOn != null) ? createdOn : LocalDateTime.now();
        this.m_indent = indent;
    }

    @JsonProperty("commentId")
    public UUID getCommentId() {
        return m_commentId;
    }

    @JsonProperty("text")
    public String getText() {
        return m_text;
    }

    @JsonProperty("image")
    public String getImageJson() {
        return m_image.orElse(null);
    }

    @JsonProperty("attachment")
    public String getAttachmentJson() {
        return m_attachment.orElse(null);
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return m_createdBy;
    }

    @JsonProperty("createdOn")
    public LocalDateTime getCreatedOn() {
        return m_createdOn;
    }

    @JsonProperty("indent")
    public int getIndent() {
        return m_indent;
    }

    public UUID commentId() {
        return m_commentId;
    }

    public String text() {
        return m_text;
    }

    public Optional<String> image() {
        return m_image;
    }

    public Optional<String> attachment() {
        return m_attachment;
    }

    public String createdBy() {
        return m_createdBy;
    }

    public LocalDateTime createdOn() {
        return m_createdOn;
    }

    public int indent() {
        return m_indent;
    }
}