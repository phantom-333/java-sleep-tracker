package ru.yandex.practicum.sleeptracker.functions;
import ru.yandex.practicum.sleeptracker.*;
import java.util.function.Function;

public class MinDurationSleepSession implements Function<SleepingLog, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        try {
            return new SleepAnalysisResult("Минимальная продолжительность сессии (в минутах): ",
                    String.valueOf(sleepingLog.getLog().stream()
                            .min(SleepingSession.durationComparator)
                            .orElseThrow(() -> {throw new EmptyLogException();})
                            .getDuration()
                            .toMinutes()
                    ));
        } catch (EmptyLogException e) {
            return new SleepAnalysisResult("Минимальная продолжительность сессии (в минутах): ", "-");
        }
    }
}
