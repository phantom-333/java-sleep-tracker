package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import static ru.yandex.practicum.sleeptracker.SleepQuality.*;


public class SleepingSession {
    protected SleepQuality quality;
    protected LocalDateTime startSession;
    protected LocalDateTime endSession;
    protected Duration duration;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public SleepingSession(SleepQuality quality, LocalDateTime startSession, LocalDateTime endSession)
                                                    throws NullPointerException, IllegalArgumentException {
        if (quality == null || startSession == null || endSession == null) {
            throw new NullPointerException("Входные данные сессии сна не могут быть null");
        }
        if (endSession.isBefore(startSession)) {
            throw new IllegalArgumentException("Время завершения сессии сна не может предшествовать его началу");
        }
        duration = Duration.between(startSession, endSession);
        if (duration.toDays() > 0) {
            throw new IllegalArgumentException("Длительность сна слишком продолжительная (> 24 часов)");
        }
        this.quality = quality;
        this.startSession = startSession;
        this.endSession = endSession;
    }

    public SleepQuality getQuality() {
        return quality;
    }

    public LocalDateTime getStartSession() {
        return startSession;
    }

    public LocalDateTime getEndSession() {
        return endSession;
    }

    public Duration getDuration() {
        return duration;
    }

    public static SleepingSession parse(String sessionStr) throws IllegalSleepingSessionFormat {
        String[] sessionParts;
        LocalDateTime startSession;
        LocalDateTime endSession;
        SleepQuality quality;
        try {
            sessionParts = sessionStr.split(";");
            if (sessionParts.length != 3) {
                throw new IllegalSleepingSessionFormat("Некорректный формат: количество частей в строке сессии не равно 3");
            }
        } catch (NullPointerException e) {
            throw new IllegalSleepingSessionFormat("Некорректный формат - аргумент равен null");
        }
        try {
            startSession = LocalDateTime.parse(sessionParts[0], DATE_TIME_FORMATTER);
            endSession = LocalDateTime.parse(sessionParts[1], DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalSleepingSessionFormat("Некорректный формат времени [dd.MM.yy HH:mm] в строке");
        }
        try {
            quality = SleepQuality.valueOf(sessionParts[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalSleepingSessionFormat("Некорректный формат качества сна (ожидалось: "
                    + SleepQuality.enumToString() + "), передано: " + sessionParts[2]);
        }
        try {
            return new SleepingSession(quality, startSession, endSession);
        } catch (Exception e) {
            throw new IllegalSleepingSessionFormat("Некорректный формат сессии сна " + e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SleepingSession session = (SleepingSession) o;
        return quality == session.quality && Objects.equals(startSession, session.startSession) && Objects.equals(endSession, session.endSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality, startSession, endSession);
    }

    @Override
    public String toString() {
        return startSession.format(DATE_TIME_FORMATTER) +
                ";" + endSession.format(DATE_TIME_FORMATTER) +
                ";" + quality;
    }
}
