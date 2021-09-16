package ru.netology.unit.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.netology.service.SmsCodeGenerator;
import ru.netology.service.ZeroCodeGenerator;

@SpringBootTest
public class NumericCodeGeneratorTests {

    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5,6})
    public void testGenerate(int length) {
        SmsCodeGenerator generator = new ZeroCodeGenerator(length);
        String actual = generator.generate();

        assertTrue(actual.matches("^\\d{" + length + "}$"));
    }
}
