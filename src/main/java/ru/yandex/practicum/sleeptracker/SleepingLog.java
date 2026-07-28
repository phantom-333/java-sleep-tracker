package ru.yandex.practicum.sleeptracker;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SleepingLog {
    protected ArrayList<SleepingSession> log;

    public SleepingLog(String fileLogPath) throws SleepingLogException {
        Path pathToFile;
        try {
            pathToFile = Paths.get(fileLogPath);
        } catch (InvalidPathException | NullPointerException e) {
            throw new SleepingLogException("Передан некорректный путь к файлу");
        }
        if(!Files.isRegularFile(pathToFile)) {
            throw new SleepingLogException("По указанному пути файл с логом сессий сна отсутствует");
        }
        log = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileLogPath))) {
            while (br.ready()) {
                System.out.println(br.readLine());
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }
}
