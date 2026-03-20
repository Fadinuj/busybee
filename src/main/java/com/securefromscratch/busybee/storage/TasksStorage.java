package com.securefromscratch.busybee.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TasksStorage {
    private final List<Task> m_tasks = new ArrayList<>();
    private final ObjectMapper m_wrapper = new ObjectMapper();
    private final String storagePath = System.getProperty("user.home") + File.separator + "busybee_db.json";

    public TasksStorage() throws IOException {
        // 1. הגדרת ה-Wrapper בצורה אחידה לכל הפעולות
        m_wrapper.registerModule(new JavaTimeModule());
        // ביטול שמירת תאריכים כמערכי מספרים (הופך אותם ל-Strings)
        m_wrapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // מניעת קריסה אם יש שדות ב-JSON שלא קיימים ב-Java (חשוב לגרסאות קודמות)
        m_wrapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // מניעת שגיאה על רשימות ריקות
        m_wrapper.configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        File file = new File(storagePath);
        
        // 2. ניסיון טעינה מהדיסק אם הקובץ קיים
        if (file.exists() && file.length() > 0) {
            System.out.println(">>> DEBUG: Found database file at " + storagePath);
            loadFromDisk();
        }

        // 3. אם אחרי הטעינה הרשימה עדיין ריקה (או שאין קובץ), נשתמש בגנרטור
        if (m_tasks.isEmpty()) {
            System.out.println(">>> DEBUG: No tasks found. Generating initial data...");
            InitialDataGenerator.fillWithData(m_tasks);
            saveToDisk(); // שמירה מיידית של נתוני ברירת המחדל
        }
    }

    private void loadFromDisk() {
        try {
            File file = new File(storagePath);
            // שימוש ב-TypeReference כדי ש-Jackson ידע שמדובר ברשימת Tasks
            List<Task> loaded = m_wrapper.readValue(file, new TypeReference<List<Task>>() {});
            if (loaded != null) {
                m_tasks.clear();
                m_tasks.addAll(loaded);
                System.out.println(">>> DEBUG: Successfully loaded " + m_tasks.size() + " tasks from disk.");
            }
        } catch (IOException e) {
            System.err.println(">>> DEBUG: Failed to load from disk: " + e.getMessage());
            // במקרה של שגיאה קריטית ב-JSON, אנחנו לא מוחקים את הקובץ כדי שתוכל לתקן אותו
        }
    }

    public synchronized void saveToDisk() {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(storagePath), StandardCharsets.UTF_8))) {
            // כתיבת ה-JSON בצורה יפה (Pretty Print)
            String json = m_wrapper.writerWithDefaultPrettyPrinter().writeValueAsString(m_tasks);
            out.print(json);
            out.flush(); // וידוא שהכל נכתב פיזית לדיסק
            System.out.println(">>> DEBUG: Database updated on disk. Total tasks: " + m_tasks.size());
        } catch (IOException e) {
            System.err.println(">>> DEBUG: Failed to save to disk: " + e.getMessage());
        }
    }

    public List<Task> getAll() {
        return Collections.unmodifiableList(m_tasks);
    }

    public synchronized UUID add(String name, String desc, java.time.LocalDate date, java.time.LocalTime time, String owner, String[] resp) {
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
}