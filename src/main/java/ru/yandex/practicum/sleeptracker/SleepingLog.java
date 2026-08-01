package ru.yandex.practicum.sleeptracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SleepingLog {
    protected PrintWriter pwLog;
    protected List<SleepingSession> log;

    public SleepingLog(PrintWriter pwLog, String fileLogPath) throws SleepingLogException {
        Path pathToFile;
        if (pwLog == null) {
            throw new SleepingLogException("лог фиксации ошибок не должен быть null");
        }
        this.pwLog = pwLog;
        try {
            pathToFile = Paths.get(fileLogPath);
        } catch (InvalidPathException | NullPointerException e) {
            throw new SleepingLogException("передан некорректный путь к файлу");
        }
        if (!Files.isRegularFile(pathToFile)) {
            throw new SleepingLogException("по указанному пути файл с логом сессий сна отсутствует");
        }
        log = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileLogPath))) {
            log = br.lines()
                    .filter(line -> !line.trim().isEmpty())
                    .map(line -> {
                        try {
                            return SleepingSession.parse(line);
                        } catch (IllegalSleepingSessionFormat e) {
                            pwLog.printf("%sошибка парсинга строки \"%s\": %s\n", GetTime.now(), line, e.getMessage());
                            return null;
                        }
                    })
                    .filter(sleepingSession -> sleepingSession != null)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new SleepingLogException(GetTime.now() + "ошибка чтения файла: " + e.getMessage());
        }
    }

    public List<SleepingSession> getLog() {
        return List.copyOf(log);
    }
}
