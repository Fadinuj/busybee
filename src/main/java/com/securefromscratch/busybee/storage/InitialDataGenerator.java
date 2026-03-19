package com.securefromscratch.busybee.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class InitialDataGenerator {
    public static void fillWithData(List<Task> tasks) {
        List<LocalDateTime> randomPastDates = generateRandomDateTimes(15, 5);
        List<LocalDateTime> randomFutureDates = generateRandomDateTimes(10, -5);

        // שים לב לשימוש ב-new String[]{"Yariv"} עבור אחריות
        tasks.add(new Task(
                "Buy ingredients for Caprese Sandwich",
                "<ul><li>1 fresh baguette...</li></ul>",
                randomFutureDates.remove(0).toLocalDate(),
                "Yariv", new String[]{"Yariv"}, randomPastDates.remove(0)
        ));

        tasks.add(new Task(
                "Get sticker for car",
                "Security office",
                LocalDate.now().plusDays(10),
                "Or", new String[]{"Yariv"}, randomPastDates.remove(0)
        ));
    }

    public static List<LocalDateTime> generateRandomDateTimes(int numberOfDates, int daysAgo) {
        List<LocalDateTime> randomDateTimes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime pastDate = now.minusDays(daysAgo);

        long startEpoch = pastDate.atZone(ZoneId.systemDefault()).toEpochSecond();
        long endEpoch = now.atZone(ZoneId.systemDefault()).toEpochSecond();
        
        // תיקון השגיאה: מוודא שהטווח תמיד חיובי
        long min = Math.min(startEpoch, endEpoch);
        long max = Math.max(startEpoch, endEpoch);

        for (int i = 0; i < numberOfDates; i++) {
            long randomEpoch = ThreadLocalRandom.current().nextLong(min, max + 1);
            LocalDateTime randomDateTime = LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneId.systemDefault().getRules().getOffset(now));
            randomDateTimes.add(randomDateTime);
        }
        randomDateTimes.sort(LocalDateTime::compareTo);
        return randomDateTimes;
    }
}