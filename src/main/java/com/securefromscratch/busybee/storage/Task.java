package com.securefromscratch.busybee.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID m_taskid;
    private final String m_name;
    private final String m_desc;
    private final LocalDate m_dueDate;
    private final boolean m_hasDueTime;
    private final LocalTime dueTime;
    private final String m_createdBy;
    private final String[] m_responsibilityOf;
    private final LocalDateTime m_creationDatetime;
    private final boolean m_done;
    private final List<TaskComment> m_comments = new ArrayList<>();

    public Task() {
        this.m_taskid = UUID.randomUUID();
        this.m_name = "";
        this.m_desc = "";
        this.m_dueDate = LocalDate.MAX;
        this.m_hasDueTime = false;
        this.dueTime = LocalTime.MIN;
        this.m_createdBy = "";
        this.m_responsibilityOf = new String[0];
        this.m_creationDatetime = LocalDateTime.now();
        this.m_done = false;
    }

    @JsonCreator
    public Task(
            @JsonProperty("taskid") UUID taskid,
            @JsonProperty("name") String name,
            @JsonProperty("desc") String desc,
            @JsonProperty("dueDate") LocalDate dueDate,
            @JsonProperty("hasDueTime") boolean hasDueTime,
            @JsonProperty("dueTime") LocalTime dueTime,
            @JsonProperty("createdBy") String createdBy,
            @JsonProperty("responsibilityOf") String[] responsibilityOf,
            @JsonProperty("creationDatetime") LocalDateTime creationDatetime,
            @JsonProperty("done") boolean done,
            @JsonProperty("comments") List<TaskComment> comments
    ) {
        this.m_taskid = (taskid != null) ? taskid : UUID.randomUUID();
        this.m_name = (name != null) ? name : "";
        this.m_desc = (desc != null) ? desc : "";
        this.m_dueDate = (dueDate != null) ? dueDate : LocalDate.MAX;
        this.m_hasDueTime = hasDueTime;
        this.dueTime = (dueTime != null) ? dueTime : LocalTime.MIN;
        this.m_createdBy = (createdBy != null) ? createdBy : "";
        this.m_responsibilityOf = (responsibilityOf != null) ? responsibilityOf : new String[0];
        this.m_creationDatetime = (creationDatetime != null) ? creationDatetime : LocalDateTime.now();
        this.m_done = done;

        if (comments != null) {
            this.m_comments.addAll(comments);
        }
    }

    public Task(String name, String desc, LocalDate date, LocalTime time, String owner, String[] resp) {
        this(
                UUID.randomUUID(),
                name,
                desc,
                date != null ? date : LocalDate.MAX,
                time != null,
                time != null ? time : LocalTime.MIN,
                owner,
                resp,
                LocalDateTime.now(),
                false,
                new ArrayList<>()
        );
    }

    public Task(String name, String desc, LocalDate date, String owner, String[] resp, LocalDateTime createdOn) {
        this(
                UUID.randomUUID(),
                name,
                desc,
                date,
                false,
                LocalTime.MIN,
                owner,
                resp,
                createdOn,
                false,
                new ArrayList<>()
        );
    }

    @JsonProperty("taskid")
    public UUID getTaskid() {
        return m_taskid;
    }

    @JsonProperty("name")
    public String getName() {
        return m_name;
    }

    @JsonProperty("desc")
    public String getDesc() {
        return m_desc;
    }

    @JsonProperty("dueDate")
    public LocalDate getDueDateJson() {
        return (m_dueDate == null || m_dueDate.equals(LocalDate.MAX)) ? null : m_dueDate;
    }

    @JsonProperty("hasDueTime")
    public boolean isHasDueTime() {
        return m_hasDueTime;
    }

    @JsonProperty("dueTime")
    public LocalTime getDueTimeJson() {
        return m_hasDueTime ? dueTime : null;
    }

    @JsonProperty("createdBy")
    public String getCreatedBy() {
        return m_createdBy;
    }

    @JsonProperty("responsibilityOf")
    public String[] getResponsibilityOf() {
        return m_responsibilityOf;
    }

    @JsonProperty("creationDatetime")
    public LocalDateTime getCreationDatetimeJson() {
        return m_creationDatetime;
    }

    @JsonProperty("done")
    public boolean isDone() {
        return m_done;
    }

    @JsonProperty("comments")
    public List<TaskComment> getComments() {
        return m_comments;
    }

    public UUID taskid() {
        return m_taskid;
    }

    public String name() {
        return m_name;
    }

    public String desc() {
        return m_desc;
    }

    public String createdBy() {
        return m_createdBy;
    }

    public String[] responsibilityOf() {
        return m_responsibilityOf;
    }

    public LocalDateTime creationDatetime() {
        return m_creationDatetime;
    }

    public boolean done() {
        return m_done;
    }

    public List<TaskComment> comments() {
        return Collections.unmodifiableList(m_comments);
    }

    public Optional<LocalDate> dueDate() {
        return (m_dueDate == null || m_dueDate.equals(LocalDate.MAX))
                ? Optional.empty()
                : Optional.of(m_dueDate);
    }

    public Optional<LocalTime> dueTime() {
        return m_hasDueTime ? Optional.of(dueTime) : Optional.empty();
    }

    public static Task asDone(Task t) {
        return new Task(
                t.m_taskid,
                t.m_name,
                t.m_desc,
                t.m_dueDate,
                t.m_hasDueTime,
                t.dueTime,
                t.m_createdBy,
                t.m_responsibilityOf,
                t.m_creationDatetime,
                true,
                new ArrayList<>(t.m_comments)
        );
    }

    public UUID addComment(
            String text,
            Optional<String> image,
            Optional<Object> attachment,
            String createdBy,
            LocalDateTime createdOn,
            Optional<Object> after
    ) {
        int indent = 0;

        if (after.isPresent() && after.get() instanceof UUID parentId) {
            for (TaskComment existing : m_comments) {
                if (existing.commentId().equals(parentId)) {
                    indent = existing.indent() + 1;
                    break;
                }
            }
        }

        TaskComment c = new TaskComment(
                text,
                image,
                attachment,
                createdBy,
                createdOn,
                indent
        );

        m_comments.add(c);
        return c.commentId();
    }
}