package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.functions.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    protected static final String ERROR_LOG_FILE_NAME = "error.log";
    protected static final String SLEEPING_SESSIONS_LOG_FILE_NAME = "src/main/resources/sleep_log.txt";
    protected static List<Function<SleepingLog, SleepAnalysisResult>> analyticalFunctions;

    public static List<Function<SleepingLog, SleepAnalysisResult>> loadFunctions() {
        ArrayList<Function<SleepingLog, SleepAnalysisResult>> analyticalFunctions = new ArrayList<>();
        analyticalFunctions.add(new CountSleepSessions());
        analyticalFunctions.add(new MinDurationSleepSession());
        analyticalFunctions.add(new MaxDurationSleepSession());
        analyticalFunctions.add(new AverageSessionLength());
        analyticalFunctions.add(new CountBadSleepSessions());
        analyticalFunctions.add(new CountSleeplessNight());
        analyticalFunctions.add(new UserClassification());
        return analyticalFunctions;
    }

    public static void main(String[] args) {
        try {
            Path logFile = Paths.get(ERROR_LOG_FILE_NAME);
            if (!Files.exists(logFile)) {
                try {
                    Files.createFile(logFile);
                } catch (IOException e) {
                    throw new LogFileException(e);
                }
            }
            try (FileWriter fileWriter = new FileWriter(ERROR_LOG_FILE_NAME); PrintWriter pwLog = new PrintWriter(fileWriter)) {
                try {
                    SleepingLog sleepingLog = new SleepingLog(pwLog, SLEEPING_SESSIONS_LOG_FILE_NAME);
                    System.out.println("------- Статистика сна -------");
                    sleepingLog.getLog().forEach(System.out::println);
                    System.out.println("------- Анализ статистики сна -------");
                    analyticalFunctions = loadFunctions();
                    analyticalFunctions.stream()
                            .map(function -> function.apply(sleepingLog).toString())
                            .peek(System.out::println)
                            .collect(Collectors.toList());
                } catch (SleepingLogException e) {
                    pwLog.printf("%sошибка: %s\n", GetTime.now(), e.getMessage());
                } catch (Exception e) {
                    pwLog.println(GetTime.now() + " : " + e.getMessage());
                }
            } catch (IOException e) {
                throw new LogFileException(e);
            }
        } catch (LogFileException e) {
            System.out.println("Ошибка работы с лог-файлом: " + e.getMessage());
            System.out.println("Дальнейшая работа программы невозможна");
            System.exit(0);
        }
    }
}