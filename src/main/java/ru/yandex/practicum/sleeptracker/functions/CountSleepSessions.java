package ru.yandex.practicum.sleeptracker.functions;
import ru.yandex.practicum.sleeptracker.*;
import java.util.function.Function;

public class CountSleepSessions implements Function<SleepingLog, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        return new SleepAnalysisResult("Количество сессий сна: ", String.valueOf(sleepingLog.getLog().size()));
    }
}
