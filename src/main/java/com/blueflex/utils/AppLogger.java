package com.blueflex.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppLogger {
    public enum Level {
        INFO,
        WARN,
        ERROR,
        DEBUG
    }

    private final String tag;
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AppLogger(String tag) {
        this.tag = tag;
    }

    private void log(Level level, String message) {
        String time = LocalDateTime.now().format(TIME_FORMAT);
        String formatted = String.format("[%s] [%s] [%s] %s",
                time, level, tag, message);

        switch (level) {
            case ERROR -> System.err.println(formatted);
            default -> System.out.println(formatted);
        }
    }

    // === Convenience methods ===
    public void info(String message) {
        log(Level.INFO, message);
    }

    public void warn(String message) {
        log(Level.WARN, message);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void debug(String message) {
        log(Level.DEBUG, message);
    }
}
