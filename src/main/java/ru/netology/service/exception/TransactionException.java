package ru.netology.service.exception;

public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }

}
