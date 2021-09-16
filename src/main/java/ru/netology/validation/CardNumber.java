package ru.netology.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardNumberValidator.class)
public @interface CardNumber {

  String message() default "{CardNumber.invalid}";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

}