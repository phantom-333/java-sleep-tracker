package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.*;

import java.time.LocalTime;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserClassification implements Function<SleepingLog, SleepAnalysisResult> {
    private static final LocalTime OWL_START_SLEEP_TIME = LocalTime.of(23, 0);
    private static final LocalTime OWL_END_SLEEP_TIME = LocalTime.of(9, 0);
    private static final LocalTime LARK_START_SLEEP_TIME = LocalTime.of(22, 0);
    private static final LocalTime LARK_END_SLEEP_TIME = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        int owl = sleepingLog.getLog().stream()
                .filter((sleepingSession) ->
                        LocalTime.from(sleepingSession.getStartSession()).isAfter(OWL_START_SLEEP_TIME) &&
                                LocalTime.from(sleepingSession.getEndSession()).isAfter(OWL_END_SLEEP_TIME))
                .toList()
                .size(); //количество ночей, характерных для "Совы"
        int lark = sleepingLog.getLog().stream()
                .filter((sleepingSession) ->
                        LocalTime.from(sleepingSession.getStartSession()).isBefore(LARK_START_SLEEP_TIME) &&
                                LocalTime.from(sleepingSession.getEndSession()).isBefore(LARK_END_SLEEP_TIME))
                .toList()
                .size(); //количество ночей, характерных для "Жаворонка"
        int pigeon = sleepingLog.getLog().size() - owl - lark; //количество ночей, характерных для "Голубя"
        if (owl > lark && owl > pigeon) {
            return new SleepAnalysisResult("Пользователь по характеру сна относится к ", "'совам'");
        }
        if (lark > owl && lark > pigeon) {
            return new SleepAnalysisResult("Пользователь по характеру сна относится к ", "'жаворонкам'");
        }
        return new SleepAnalysisResult("Пользователь по характеру сна относится к ", "'голубям'");
    }
}
