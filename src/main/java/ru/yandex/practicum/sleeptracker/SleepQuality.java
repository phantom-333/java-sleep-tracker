package ru.yandex.practicum.sleeptracker;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum SleepQuality {
    GOOD,
    NORMAL,
    BAD;

    public static String enumToString() {
        return Arrays.stream(SleepQuality.values())
                .map(SleepQuality::name)
                .collect(Collectors.joining(", "));
    }
}
