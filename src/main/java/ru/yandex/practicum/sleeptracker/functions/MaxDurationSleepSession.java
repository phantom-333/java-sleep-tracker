package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.*;

import java.util.function.Function;

public class MaxDurationSleepSession implements Function<SleepingLog, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        try {
            return new SleepAnalysisResult("Максимальная продолжительность сессии (в минутах): ",
                    String.valueOf(sleepingLog.getLog().stream()
                            .max(SleepingSession.durationComparator)
                            .orElseThrow(() -> {
                                throw new EmptyLogException();
                            })
                            .getDuration()
                            .toMinutes()
                    ));
        } catch (EmptyLogException e) {
            return new SleepAnalysisResult("Максимальная продолжительность сессии (в минутах): ", "-");
        }
    }
}
