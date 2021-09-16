package ru.netology.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static ru.netology.util.Luhn.check;

public class CardNumberValidator implements ConstraintValidator<CardNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && check(value);
    }

}
