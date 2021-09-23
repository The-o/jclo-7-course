package ru.netology.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import ru.netology.model.Transaction;

@Repository
public class TransactionRepository {

    private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    public Transaction getTransaction(String id) {
        return transactions.get(id);
    }

    public Transaction putTransaction(String id, Transaction transaction) {
        return transactions.put(id, transaction);
    }

}
