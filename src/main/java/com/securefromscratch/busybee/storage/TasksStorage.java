package com.securefromscratch.busybee.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TasksStorage {
    private final List<Task> m_tasks = new ArrayList<>();
    private final ObjectMapper m_wrapper = new ObjectMapper();
    private final String storagePath = System.getProperty("user.home") + File.separator + "busybee_db.json";

    public TasksStorage() throws IOException {
        m_wrapper.registerModule(new JavaTimeModule());
        m_wrapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        m_wrapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        m_wrapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        File file = new File(storagePath);

        if (file.exists() && file.length() > 0) {
            System.out.println(">>> DEBUG: Found database file at " + storagePath);
            loadFromDisk();
        }

        if (m_tasks.isEmpty()) {
            System.out.println(">>> DEBUG: No tasks found. Generating initial data...");
            InitialDataGenerator.fillWithData(m_tasks);
            saveToDisk();
        }
    }

    private void loadFromDisk() {
        try {
            File file = new File(storagePath);
            List<Task> loaded = m_wrapper.readValue(file, new TypeReference<List<Task>>() {});
            if (loaded != null) {
                m_tasks.clear();
                m_tasks.addAll(loaded);
                System.out.println(">>> DEBUG: Successfully loaded " + m_tasks.size() + " tasks from disk.");
            }
        } catch (IOException e) {
            System.err.println(">>> DEBUG: Failed to load from disk: " + e.getMessage());
        }
    }

    public synchronized void saveToDisk() {
        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(storagePath), StandardCharsets.UTF_8))) {

            String json = m_wrapper.writerWithDefaultPrettyPrinter().writeValueAsString(m_tasks);
            out.print(json);
            out.flush();

            System.out.println(">>> DEBUG: Database updated on disk. Total tasks: " + m_tasks.size());
        } catch (IOException e) {
            System.err.println(">>> DEBUG: Failed to save to disk: " + e.getMessage());
        }
    }

    public List<Task> getAll() {
        return Collections.unmodifiableList(m_tasks);
    }

    public synchronized UUID add(String name,
                                 String desc,
                                 java.time.LocalDate date,
                                 java.time.LocalTime time,
                                 String owner,
                                 String[] resp) {
        Task newTask = new Task(name, desc, date, time, owner, resp);
        m_tasks.add(newTask);
        saveToDisk();
        return newTask.taskid();
    }

    public synchronized boolean markDone(UUID taskid) {
        for (int i = 0; i < m_tasks.size(); i++) {
            if (m_tasks.get(i).taskid().equals(taskid)) {
                if (m_tasks.get(i).done()) {
                    return false;
                }

                m_tasks.set(i, Task.asDone(m_tasks.get(i)));
                saveToDisk();
                return true;
            }
        }

        throw new TaskNotFoundException(taskid);
    }

    public Task getTaskById(UUID id) {
        return m_tasks.stream()
                .filter(t -> t.taskid().equals(id))
                .findFirst()
                .orElse(null);
    }
    public Optional<Task> findTaskByImage(String filename) {
        return m_tasks.stream()
                .filter(task -> task.comments().stream().anyMatch(comment ->
                        comment.image().isPresent() && filename.equals(comment.image().get())
                ))
                .findFirst();
    }

    public Optional<Task> find(UUID id) {
        return m_tasks.stream()
                .filter(t -> t.taskid().equals(id))
                .findFirst();
    }

    public synchronized UUID addComment(Task task,
                                        String text,
                                        String createdBy,
                                        Optional<UUID> parentCommentId) {
        UUID newCommentId = task.addComment(
                text,
                Optional.empty(),
                Optional.empty(),
                createdBy,
                LocalDateTime.now(),
                parentCommentId.map(x -> (Object) x)
        );
        saveToDisk();
        return newCommentId;
    }

    public synchronized UUID addComment(Task task,
                                        String text,
                                        Optional<String> imageFilename,
                                        Optional<String> attachmentFilename,
                                        String createdBy,
                                        Optional<UUID> parentCommentId) {
        UUID newCommentId = task.addComment(
                text,
                imageFilename,
                attachmentFilename.map(x -> (Object) x),
                createdBy,
                LocalDateTime.now(),
                parentCommentId.map(x -> (Object) x)
        );
        saveToDisk();
        return newCommentId;
    }
}