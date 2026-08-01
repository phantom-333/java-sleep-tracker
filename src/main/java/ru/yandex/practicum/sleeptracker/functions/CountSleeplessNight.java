package ru.yandex.practicum.sleeptracker.functions;

import ru.yandex.practicum.sleeptracker.*;

import java.time.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class CountSleeplessNight implements Function<SleepingLog, SleepAnalysisResult> {
    private static final LocalTime NORMAL_START_SLEEP_TIME = LocalTime.of(0, 0);
    private static final LocalTime NORMAL_END_SLEEP_TIME = LocalTime.of(6, 0);
    private static final int DAY_ITERATOR = 1;
    private static final int HOUR_DAY_DELIMETER = 12;   //граница суток (час), относительно которого первая сессия сна относится к предыдущей или к следующей ночи.

    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        try {
            LocalDateTime startPeriodDateTime = sleepingLog.getLog().stream()
                    .min(SleepingSession.startSleepSessionComparator)
                    .orElseThrow(EmptyLogException::new)
                    .getStartSession();
            if (startPeriodDateTime.getHour() > HOUR_DAY_DELIMETER) {
                startPeriodDateTime = startPeriodDateTime.plusDays(1);
            }
            startPeriodDateTime = LocalDateTime.of(startPeriodDateTime.toLocalDate(), NORMAL_START_SLEEP_TIME);
            LocalDate startDate = startPeriodDateTime.toLocalDate();
            LocalDate endDate = sleepingLog.getLog().stream()
                    .max(SleepingSession.endSleepSessionComparator)
                    .orElseThrow(EmptyLogException::new)
                    .getEndSession()
                    .toLocalDate();
            if (endDate.isBefore(startDate)) {
                endDate = startDate;
            }
            Period analiticsPeriod = Period.between(startDate, endDate);
            int totalNight = analiticsPeriod.getDays() + DAY_ITERATOR;
            long countNormalSession = Stream.iterate(startDate, date -> date.plusDays(DAY_ITERATOR))
                    .limit(totalNight + DAY_ITERATOR)
                    .filter(sleepNightDate -> sleepingLog.getLog().stream()
                            .anyMatch(session ->
                                    (session.getStartSession().toLocalDate().isBefore(session.getEndSession().toLocalDate()) &&
                                            session.getEndSession().toLocalDate().equals(sleepNightDate)) ||
                                            (session.getStartSession().toLocalDate().equals(sleepNightDate) &&
                                                    session.getStartSession().toLocalTime().isBefore(NORMAL_END_SLEEP_TIME))))
                    .count();
            return new SleepAnalysisResult("Количество бессонных ночей: ",
                    String.valueOf(totalNight - countNormalSession));
        } catch (EmptyLogException e) {
            return new SleepAnalysisResult("Количество бессонных ночей: ", "-");
        }
    }
    /*
    // Первый вариант решения, реализован согласно Подсказкам №1 и 2 Практикума,
    // но предложенный в Практикуме алгоритм решения неверный, так как отдельная сессия сна != одной ночи
    // сессий сна может быть несколько (как дневных, так и ночных), что дает ложный результат при подсчете.
    // Некорректный алгоритм подтверждается тестами - при некоторых входных условиях подсчет кол-ва бессонных
    // ночей выдает отрицательный результат
    @Override
    public SleepAnalysisResult apply(SleepingLog sleepingLog) {
        try {
            LocalDateTime startPeriodDateTime = sleepingLog.getLog().stream()
                    .min(SleepingSession.startSleepSessionComparator)
                    .orElseThrow(() -> {throw new EmptyLogException();})
                    .getStartSession();
            LocalDateTime endPeriodDateTime = sleepingLog.getLog().stream()
                    .max(SleepingSession.endSleepSessionComparator)
                    .orElseThrow(() -> {throw new EmptyLogException();})
                    .getEndSession();
            if (startPeriodDateTime.getHour() < 12) {
                startPeriodDateTime = startPeriodDateTime.minusDays(1);
            }
            Period analiticsPeriod = Period.between(LocalDate.from(startPeriodDateTime), LocalDate.from(endPeriodDateTime));
            Integer totalNight = analiticsPeriod.getDays();
            Integer countNormalSession = sleepingLog.getLog().stream()
                    .filter((sleepingSession) ->
                            (sleepingSession.getStartSession().getDayOfMonth() != sleepingSession.getEndSession().getDayOfMonth()) ||
                            (LocalTime.from(sleepingSession.getStartSession()).isAfter(NORMAL_START_SLEEP_TIME) &&
                             LocalTime.from(sleepingSession.getStartSession()).isBefore(NORMAL_END_SLEEP_TIME)) ||
                            (LocalTime.from(sleepingSession.getEndSession()).isAfter(NORMAL_START_SLEEP_TIME) &&
                             LocalTime.from(sleepingSession.getEndSession()).isBefore(NORMAL_END_SLEEP_TIME)))
                    .collect(Collectors.toList())
                    .size();
            return new SleepAnalysisResult("Количество бессонных ночей: ",
                    String.valueOf(totalNight - countNormalSession));
        } catch (EmptyLogException e) {
            return new SleepAnalysisResult("Количество бессонных ночей: ", "-");
        }
    }
    */
}
