package ru.yandex.practicum.sleeptracker.functions;
import ru.yandex.practicum.sleeptracker.*;
import java.util.function.Function;

public class AverageSessionLength implements Function<SleepingLog, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        try {
            return new SleepAnalysisResult("Средняя продолжительность сессии (в минутах): ",
                    String.format("%.2f", sleepingLog.getLog().stream()
                            .mapToLong(sleepingSession -> sleepingSession.getDuration().toMinutes())
                            .average()
                            .orElseThrow(() -> {
                                throw new EmptyLogException();
                            }))
            );
        } catch (EmptyLogException e) {
            return new SleepAnalysisResult("Средняя продолжительность сессии (в минутах): ", "0");
        }

    }
}
