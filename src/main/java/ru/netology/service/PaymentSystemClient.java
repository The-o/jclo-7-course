package ru.netology.service;

import ru.netology.model.Transaction;

public interface PaymentSystemClient {

    public enum TransactionResult {
        OK,
        FAIL
    }

    public TransactionResult performTransaction(Transaction transaction);
}
