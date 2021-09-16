package ru.netology.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.netology.service.SmsCodeGenerator;
import ru.netology.service.ZeroCodeGenerator;

@SpringBootTest
public class ZeroCodeGeneratorTests {

    @ParameterizedTest
    @MethodSource("generateTestDataSource")
    public void testGenerate(int length, String expected) {
        SmsCodeGenerator generator = new ZeroCodeGenerator(length);
        String actual = generator.generate();

        assertEquals(expected, actual);
    }

    public static Stream<Arguments> generateTestDataSource() {
        return Stream.of(
            Arguments.of(1, "0"),
            Arguments.of(2, "00"),
            Arguments.of(3, "000"),
            Arguments.of(4, "0000"),
            Arguments.of(5, "00000"),
            Arguments.of(6, "000000")
        );
    }
}
