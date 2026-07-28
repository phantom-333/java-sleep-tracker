package ru.yandex.practicum.sleeptracker;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class SleepTrackerAppTest {

    @Test
    public void sleepengSessionIlleggalArgumentTest() {
        assertThrows(NullPointerException.class,
                () -> {new SleepingSession(null, LocalDateTime.now(), LocalDateTime.now());},
                "Тест на передачу в SleepingSession null-аргументов не пройден");

        assertThrows(NullPointerException.class,
                () -> {new SleepingSession(SleepQuality.GOOD, null, LocalDateTime.now());},
                "Тест на передачу в SleepingSession null-аргументов не пройден");

        assertThrows(NullPointerException.class,
                () -> {new SleepingSession(SleepQuality.GOOD, LocalDateTime.now(), null);},
                "Тест на передачу в SleepingSession null-аргументов не пройден");

        assertThrows(IllegalArgumentException.class,
                () -> {new SleepingSession(SleepQuality.GOOD, LocalDateTime.now(), LocalDateTime.now().minusHours(1));},
                "Тест на передачу в SleepingSession некорректных параметров временных интервалов не пройден");

        LocalDateTime dateTime = LocalDateTime.now();
        assertThrows(IllegalArgumentException.class,
                () -> {new SleepingSession(SleepQuality.GOOD, dateTime.minusDays(1), dateTime);},
                "Тест на передачу в SleepingSession некорректных параметров временных интервалов не пройден");

        assertDoesNotThrow(() -> {new SleepingSession(SleepQuality.GOOD, dateTime.minusHours(23).minusMinutes(59).minusSeconds(59), dateTime);},
                "Тест на передачу в SleepingSession корректных параметров временных интервалов на границе значений не пройден");
    }

    @Test
    public void sleepengSessionTest() {
        LocalDateTime dateTime = LocalDateTime.parse("2026-07-15T00:30:15");
        SleepingSession session = new SleepingSession(SleepQuality.GOOD, dateTime, dateTime.plusHours(9));

        assertEquals(SleepQuality.GOOD, session.getQuality(),
                "getQuality() в классе SleepQuality выдает некорректный результат");
        assertEquals(LocalDateTime.parse("2026-07-15T00:30:15"), session.getStartSession(),
                "getStartSession() в классе SleepQuality выдает некорректный результат");
        assertEquals(9, session.getDuration().toHours(),
                "Продолжительность сна в классе SleepQuality рассчитывается некорректно");
    }

    @Test
    public void sleepengSessionParseTest() {
        String sessionStr = "01.10.25 22:15;02.10.25 08:00;GOOD";
        SleepingSession session = null;
        try {
            session = SleepingSession.parse(sessionStr);
            assertTrue(sessionStr.equals(session.toString()),
                    "метод parse() в классе SleepQuality выдает некорректный результат");
        } catch (IllegalSleepingSessionFormat e) {
            System.out.println(e.getMessage());

        }

        assertNotNull(session,"метод parse() в классе SleepQuality завершен с ошибкой");
    }

}