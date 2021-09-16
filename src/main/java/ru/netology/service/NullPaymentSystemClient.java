package ru.netology.service;

import org.springframework.stereotype.Component;

import ru.netology.model.Transaction;

@Component
public class NullPaymentSystemClient implements PaymentSystemClient {

    @Override
    public TransactionResult performTransaction(Transaction transaction) {
        return TransactionResult.OK;
    }

}
