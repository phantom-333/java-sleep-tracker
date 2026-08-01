package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.sleeptracker.functions.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class SleepTrackerAppTest {
    protected static final String TEST_SLEEPING_LOG_FILE = "testFileSleeping.log";
    private SleepingLog log;
    private FileWriter fileWriter;
    private PrintWriter testLogFile;
    private String testFilePath;
    private Function<SleepingLog, SleepAnalysisResult> function;
    private final PrintWriter pwLog = new PrintWriter(System.out);

    @TempDir
    Path tempDir;

    @Test
    public void sleepingSessionIllegalArgumentTest() {
        assertThrows(NullPointerException.class, () -> new SleepingSession(null, LocalDateTime.now(), LocalDateTime.now()),
                "Тест на передачу в SleepingSession null-аргументов не пройден");

        assertThrows(NullPointerException.class, () -> new SleepingSession(SleepQuality.GOOD, null, LocalDateTime.now()),
                "Тест на передачу в SleepingSession null-аргументов не пройден");

        assertThrows(NullPointerException.class, () -> new SleepingSession(SleepQuality.GOOD, LocalDateTime.now(), null),
                "Тест на передачу в SleepingSession null-аргументов не пройден");

        assertThrows(IllegalArgumentException.class, () -> new SleepingSession(SleepQuality.GOOD, LocalDateTime.now(), LocalDateTime.now().minusHours(1)),
                "Тест на передачу в SleepingSession некорректных параметров временных интервалов не пройден");

        LocalDateTime dateTime = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class, () -> new SleepingSession(SleepQuality.GOOD, dateTime.minusDays(1), dateTime),
                "Тест на передачу в SleepingSession некорректных параметров временных интервалов не пройден");

        assertDoesNotThrow(() -> {
            new SleepingSession(SleepQuality.GOOD, dateTime.minusHours(23).minusMinutes(59).minusSeconds(59), dateTime);
        }, "Тест на передачу в SleepingSession корректных параметров временных интервалов на границе значений не пройден");
    }

    @Test
    public void sleepingSessionTest() {
        LocalDateTime dateTime = LocalDateTime.parse("2026-07-15T00:30:15");
        SleepingSession session = new SleepingSession(SleepQuality.GOOD, dateTime, dateTime.plusHours(9));
        assertEquals(SleepQuality.GOOD, session.getQuality(), "getQuality() в классе SleepQuality выдает некорректный результат");
        assertEquals(LocalDateTime.parse("2026-07-15T00:30:15"), session.getStartSession(), "getStartSession() в классе SleepQuality выдает некорректный результат");
        assertEquals(9, session.getDuration().toHours(), "Продолжительность сна в классе SleepQuality рассчитывается некорректно");
    }

    @Test
    public void sleepingSessionParseTest() {
        String sessionStr = "01.10.25 22:15;02.10.25 08:00;GOOD";
        SleepingSession session = null;
        try {
            session = SleepingSession.parse(sessionStr);
            assertEquals(sessionStr, session.toString(), "метод parse() в классе SleepQuality выдает некорректный результат");
        } catch (IllegalSleepingSessionFormat e) {
            System.out.println(e.getMessage());
        }
        assertNotNull(session, "метод parse() в классе SleepQuality завершен с ошибкой");
    }

    @BeforeEach
    public void preparingLogSessionForTest() {
        Path tempFile = tempDir.resolve(TEST_SLEEPING_LOG_FILE);
        try {
            testFilePath = tempFile.toString();
            fileWriter = new FileWriter(testFilePath);
            testLogFile = new PrintWriter(fileWriter);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    public void closeResources() {
        try {
            testLogFile.close();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void countSleepSessionsEmptyTest() {
        function = new CountSleepSessions();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество сессий сна: 0", function.apply(log).toString(), "Функция CountSleepSessions выдает неверный результат");
    }

    @Test
    public void countSleepSessionsNoEmptyTest() {
        function = new CountSleepSessions();
        testLogFile.println("01.10.25 23:15;02.10.25 07:30;GOOD");
        testLogFile.println("02.10.25 23:15;03.10.25 07:30;GOOD");
        testLogFile.println("03.10.25 23:15;04.10.25 07:30;GOOD");
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество сессий сна: 3", function.apply(log).toString(), "Функция CountSleepSessions выдает неверный результат");
    }

    @Test
    public void countBadSleepSessions_Test_0() {
        function = new CountBadSleepSessions();
        testLogFile.println("01.10.25 23:15;02.10.25 07:30;GOOD");
        testLogFile.println("02.10.25 23:15;03.10.25 07:30;NORMAL");
        testLogFile.println("03.10.25 23:15;04.10.25 07:30;GOOD");
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество сессий с плохим качеством сна: 0", function.apply(log).toString(), "Функция CountBadSleepSessions выдает неверный результат");
    }

    @Test
    public void countBadSleepSessions_Test_3() {
        function = new CountBadSleepSessions();
        testLogFile.println("01.10.25 23:15;02.10.25 07:30;BAD");
        testLogFile.println("02.10.25 23:15;03.10.25 07:30;BAD");
        testLogFile.println("03.10.25 23:15;04.10.25 07:30;GOOD");
        testLogFile.println("20.10.25 23:15;21.10.25 07:30;BAD");
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество сессий с плохим качеством сна: 3", function.apply(log).toString(), "Функция CountBadSleepSessions выдает неверный результат");
    }

    @Test
    public void minDurationSleepSession_Empty_Test() {
        function = new MinDurationSleepSession();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Минимальная продолжительность сессии (в минутах): -", function.apply(log).toString(), "Функция MinDurationSleepSession выдает неверный результат");
    }

    @Test
    public void minDurationSleepSession_Test_90() {
        function = new MinDurationSleepSession();
        testLogFile.println("01.10.25 21:00;02.10.25 07:30;GOOD");
        testLogFile.println("02.10.25 23:00;03.10.25 08:45;GOOD");
        testLogFile.println("03.10.25 22:00;03.10.25 23:30;GOOD");
        testLogFile.println("04.10.25 21:00;05.10.25 09:00;GOOD");
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Минимальная продолжительность сессии (в минутах): 90", function.apply(log).toString(), "Функция MinDurationSleepSession выдает неверный результат");
    }

    @Test
    public void maxDurationSleepSession_Empty_Test() {
        function = new MaxDurationSleepSession();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Максимальная продолжительность сессии (в минутах): -", function.apply(log).toString(), "Функция MaxDurationSleepSession выдает неверный результат");
    }

    @Test
    public void maxDurationSleepSession_Test_600() {
        function = new MaxDurationSleepSession();
        testLogFile.println("01.10.25 23:15;02.10.25 07:30;GOOD");
        testLogFile.println("02.10.25 21:00;03.10.25 07:00;GOOD");
        testLogFile.println("03.10.25 23:15;04.10.25 07:30;GOOD");
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Максимальная продолжительность сессии (в минутах): 600", function.apply(log).toString(), "Функция MaxDurationSleepSession выдает неверный результат");
    }

    @Test
    public void averageSessionLength_Empty_Test() {
        function = new AverageSessionLength();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Средняя продолжительность сессии (в минутах): 0", function.apply(log).toString(), "Функция AverageSessionLength выдает неверный результат");
    }

    @Test
    public void averageSessionLength_Test() {
        function = new AverageSessionLength();
        testLogFile.println("01.10.25 23:00;02.10.25 04:00;GOOD");  //300
        testLogFile.println("02.10.25 21:00;03.10.25 07:00;GOOD");  //600
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Средняя продолжительность сессии (в минутах): 450", function.apply(log).toString(), "Функция AverageSessionLength выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Empty_Test() {
        function = new CountSleeplessNight();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: -", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_at06_00() {
        function = new CountSleeplessNight();
        testLogFile.println("02.10.25 06:00;02.10.25 07:00;GOOD");  //01-02.10 - бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 1", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_at05_59() {
        function = new CountSleeplessNight();
        testLogFile.println("02.10.25 05:59;02.10.25 07:00;GOOD");  //01-02.10 - не бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 0", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_at23_59() {
        function = new CountSleeplessNight();
        testLogFile.println("02.10.25 22:00;02.10.25 23:59;GOOD");  //02-03.10 - бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 1", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_at13_00() {
        function = new CountSleeplessNight();
        testLogFile.println("02.10.25 13:00;02.10.25 15:00;GOOD");  //02-03.10 - бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 1", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }


    @Test
    public void countSleeplessNight_Test_noSleepless() {
        function = new CountSleeplessNight();
        testLogFile.println("01.10.25 23:15;02.10.25 07:30;GOOD");  //все ночи не бессонные
        testLogFile.println("02.10.25 21:00;03.10.25 07:00;GOOD");
        testLogFile.println("03.10.25 23:15;04.10.25 07:30;GOOD");
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 0", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_allSleepless() {
        function = new CountSleeplessNight();
        testLogFile.println("01.10.25 21:00;01.10.25 23:59;GOOD");  //01-02.10 - бессонная
        testLogFile.println("02.10.25 13:00;02.10.25 15:00;GOOD");  //02-03.10 - бессонная
        testLogFile.println("02.10.25 17:00;02.10.25 19:00;GOOD");  //02-03.10 - бессонная
        testLogFile.println("03.10.25 13:00;03.10.25 14:00;GOOD");  //02-03.10 - бессонная
        testLogFile.println("03.10.25 14:00;03.10.25 15:00;GOOD");  //02-03.10 - бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 2", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_manyNormalSessions_And_OneSleepless() {
        function = new CountSleeplessNight();
        testLogFile.println("01.10.25 23:00;02.10.25 00:30;GOOD");  //01-02.10 - не бессонная
        testLogFile.println("02.10.25 01:00;02.10.25 02:00;GOOD");  //01-02.10 - не бессонная
        testLogFile.println("02.10.25 03:00;02.10.25 04:00;GOOD");  //01-02.10 - не бессонная
        testLogFile.println("03.10.25 07:00;03.10.25 09:00;GOOD");  //02-03.10 - бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 1", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }

    @Test
    public void countSleeplessNight_Test_manySleepless_And_OneNormalSessions() {
        function = new CountSleeplessNight();
        testLogFile.println("01.10.25 07:00;01.10.25 10:00;GOOD");  //01-02.10 - бессонная
        testLogFile.println("02.10.25 01:00;02.10.25 02:00;GOOD");  //01-02.10 - не бессонная
        testLogFile.println("02.10.25 09:00;02.10.25 11:00;GOOD");  //01-02.10 - интервал сна относится к "бессонным", но сама ночь — нет.
        testLogFile.println("03.10.25 07:00;03.10.25 09:00;GOOD");  //02-03.10 - бессонная
        testLogFile.flush();
        try {
            log = new SleepingLog(pwLog, testFilePath);
        } catch (SleepingLogException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("Количество бессонных ночей: 2", function.apply(log).toString(), "Функция CountSleeplessNight выдает неверный результат");
    }
}