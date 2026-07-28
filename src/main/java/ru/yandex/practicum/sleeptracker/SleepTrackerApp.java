package ru.yandex.practicum.sleeptracker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class SleepTrackerApp {

    protected static final String ERROR_LOG_FILE_NAME = "error.log";
    protected static final String SLEEPING_SESSIONS_LOG_FILE_NAME = "src/main/resources/sleep_log.txt";
    protected static PrintWriter pwLog;

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
            try (FileWriter fileWriter = new FileWriter(ERROR_LOG_FILE_NAME)) {
                pwLog = new PrintWriter(fileWriter);
                pwLog.println(GetTime.now() + "запуск программы");
                try {
                    try {
                        SleepingLog sleepingLog = new SleepingLog(SLEEPING_SESSIONS_LOG_FILE_NAME);
                    } catch (SleepingLogException e) {
                        System.out.println(e.getMessage());
                        pwLog.printf("%sошибка: %s\n", GetTime.now(), e.getMessage());
                    }
                } catch (Exception e) {
                    pwLog.println(GetTime.now() + e.getMessage());
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