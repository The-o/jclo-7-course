package ru.netology.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sms.code.generator", havingValue = "numeric", matchIfMissing = true)
public class NumericCodeGenerator implements SmsCodeGenerator {

    private final int length;

    public NumericCodeGenerator(@Value("${sms.code.length:4}") int length) {
        this.length = length;
    }

    public String generate() {
        StringBuilder builder = new StringBuilder();
        Random rng = new Random();

        for (int i = 0; i < length; ++i) {
            builder.append(String.valueOf(rng.nextInt(10)));
        }

        return builder.toString();
    }

}
