package ru.netology.unit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import ru.netology.util.CardValidTill;

@SpringBootTest
public class CardValidTillTests {

    @ParameterizedTest
    @MethodSource("cardValidTillTestDataSource")
    public void testCardValidTill(String validTill, boolean expected) {
        boolean actual = CardValidTill.check(validTill);

        assertEquals(expected, actual);
    }

    public static Stream<Arguments> cardValidTillTestDataSource() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) - 2000;
        int month = now.get(Calendar.MONTH) + 1;

        return Stream.of(
            Arguments.of(String.format("%02d/%02d", month, year + 1), true),
            Arguments.of(String.format("%02d/%02d", month + 1, year), true),
            Arguments.of(String.format("%02d/%02d", month, year), false),
            Arguments.of(String.format("%02d/%02d", month, year - 1), false)
        );
    }
}
