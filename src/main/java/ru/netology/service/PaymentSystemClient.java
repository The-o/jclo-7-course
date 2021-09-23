package ru.netology.service;

import ru.netology.model.Transaction;

public interface PaymentSystemClient {

    enum TransactionResult {
        OK,
        FAIL
    }

    TransactionResult performTransaction(Transaction transaction);

}
