package ru.yandex.practicum.sleeptracker.functions;

public class EmptyLogException extends RuntimeException {
    public EmptyLogException() {
        super();
    }

    public EmptyLogException(String message) {
        super(message);
    }
}
