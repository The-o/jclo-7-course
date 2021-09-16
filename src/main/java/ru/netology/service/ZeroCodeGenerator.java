package ru.netology.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "sms.code.generator", havingValue = "zero")
public class ZeroCodeGenerator implements SmsCodeGenerator {

    private int length;

    public ZeroCodeGenerator(@Value("${sms.code.length:4}") int length) {
        this.length = length;
    }

    public String generate() {
        return "0".repeat(length);
    }

}
