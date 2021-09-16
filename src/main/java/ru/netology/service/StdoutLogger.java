package ru.netology.service;

import java.io.IOException;
import java.util.Date;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "logs.engine", havingValue = "stdout", matchIfMissing = true)
public class StdoutLogger implements Logger {

    @Override
    public void log(String message) throws IOException {
        Date now = new Date();
        System.out.format("[%s] %s%n", now.toString(), message);
    }

}
