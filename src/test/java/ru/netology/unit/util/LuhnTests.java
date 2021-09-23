package ru.netology.unit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.netology.util.Luhn;

@SpringBootTest
public class LuhnTests {

    @ParameterizedTest
    @MethodSource("luhnTestDataSource")
    public void testLuhn(String cardNumber, boolean expected) {
        boolean actual = Luhn.check(cardNumber);

        assertEquals(expected, actual);
    }

    public static Stream<Arguments> luhnTestDataSource() {
        return Stream.of(
            Arguments.of("0000000000000000", true),
            Arguments.of("0000000000000001", false),
            Arguments.of("2345678987654321", false),
            Arguments.of("2345678987654327", true)
        );
    }

}
