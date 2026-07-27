package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDateTime;

public class SleepingSession {
    protected static SleepQuality quality;
    protected LocalDateTime startSession;
    protected LocalDateTime endSession;
    protected Duration duration;

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

    public static SleepQuality getQuality() {
        return quality;
    }

    public LocalDateTime getStartSession() {
        return startSession;
    }

    public LocalDateTime getEndSession() {
        return endSession;
    }

}
