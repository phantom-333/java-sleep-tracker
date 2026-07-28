package ru.yandex.practicum.sleeptracker;

public class LogFileException extends Exception {
    public LogFileException(String message) {
        super(message);
    }

    public LogFileException(Throwable cause) { super(cause); }
}
