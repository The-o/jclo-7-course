package ru.netology.util;

import java.util.Calendar;

public class CardValidTill {

    public static boolean check(String cardValidTill) {
        String[] monthYear = cardValidTill.split("/");

        int month = Integer.parseInt(monthYear[0]);
        int year = Integer.parseInt(monthYear[1]);

        Calendar now = Calendar.getInstance();
        Calendar validTill = Calendar.getInstance();
        validTill.set(2000 + year, month - 1, 1);

        return now.compareTo(validTill) < 0;
    }
}
