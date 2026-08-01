package ru.yandex.practicum.sleeptracker;

public class IllegalSleepingSessionFormat extends Exception {
    public IllegalSleepingSessionFormat(String message) {
        super(message);
    }
}
