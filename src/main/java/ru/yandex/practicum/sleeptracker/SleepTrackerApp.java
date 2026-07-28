package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public class SleepTrackerApp {

    public static void main(String[] args) {
        SleepingSession session;
        try {
            session = SleepingSession.parse("01.10.25 22:15;02.10.25 08:00;good");
            System.out.println(session);
        } catch (IllegalSleepingSessionFormat e) {
            System.out.println(e.getMessage());
        }
    }
}