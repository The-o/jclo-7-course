package ru.netology.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "logs.engine", havingValue = "file")
public class FileLogger implements Logger {

    private final String logFile;

    public FileLogger(@Value("${logs.file:logs.txt}") String logFile) {
        this.logFile = logFile;
    }

    @Override
    public void log(String message) throws IOException {
        Date now = new Date();
        String logData = String.format("[%s] %s%n", now.toString(), message);

        Files.write(Paths.get(logFile), logData.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

}
