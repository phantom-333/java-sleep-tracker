package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.*;
import java.util.function.Function;

public class CountBadSleepSessions implements Function<SleepingLog, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        return new SleepAnalysisResult("Количество сессий с плохим качеством сна: ",
                String.valueOf(sleepingLog.getLog().stream()
                        .filter(sleepingSession -> sleepingSession.getQuality() == SleepQuality.BAD)
                        .toList()
                        .size()
                ));
    }
}
