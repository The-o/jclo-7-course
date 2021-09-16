package ru.netology.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import ru.netology.model.Transaction;

@Repository
public class TransactionRepository {
    private Map<String, Transaction> transactions = new HashMap<>();

    public Transaction getTransaction(String id) {
        return transactions.get(id);
    }

    public Transaction putTransaction(String id, Transaction transaction) {
        return transactions.put(id, transaction);
    }
}
