package ru.netology.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ru.netology.util.CardValidTill.check;

public class CardValidTillValidator implements ConstraintValidator<CardValidTill, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && check(value);
    }

}
