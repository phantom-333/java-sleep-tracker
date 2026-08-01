package ru.yandex.practicum.sleeptracker;

import java.util.Objects;

public class SleepAnalysisResult {
    protected final String message;
    protected final String value;

    public SleepAnalysisResult(String message, String value) {
        this.message = message;
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SleepAnalysisResult that = (SleepAnalysisResult) o;
        return Objects.equals(message, that.message) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, value);
    }

    @Override
    public String toString() {
        return message + value;
    }
}
