package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public class SleepTrackerApp {

    public static void main(String[] args) {
        try {
            SleepingLog sleepingLog = new SleepingLog("/../../sleep_log.txt");
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
    }
}