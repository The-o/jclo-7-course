package ru.netology.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ru.netology.model.Transaction;
import ru.netology.model.Transaction.TransactionStatus;
import ru.netology.repository.TransactionRepository;
import ru.netology.service.PaymentSystemClient.TransactionResult;
import ru.netology.service.exception.TransactionException;

@Service
public class TransferService {

    private static final String ERROR_TRANSACTION_WRONG_CODE = "Неверный код подтверждения операции";
    private static final String ERROR_TRANSACTION_COMMITED = "Операция уже выполнена";
    private static final String ERROR_TRANSACTION_NOT_FOUND = "Операция не найдена";

    private final PaymentSystemClient paymentSystem;
    private final TransactionRepository repository;
    private final SmsCodeGenerator codeGenerator;
    private final Logger logger;

    public TransferService(PaymentSystemClient paymentSystem, TransactionRepository repository, SmsCodeGenerator codeGenerator, Logger logger) {
        this.paymentSystem = paymentSystem;
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.logger = logger;
    }

    public String prepareTransaction(String fromCard, String fromValidTill, String fromCvv, String toCard, long amount, String currency) {
        Transaction transaction = new Transaction(fromCard, fromValidTill, fromCvv, toCard, amount, currency, codeGenerator.generate());
        String operationId = UUID.randomUUID().toString();

        repository.putTransaction(operationId, transaction);

        return operationId;
    }

    public String performTransaction(String operationId, String code) throws IOException {
        Transaction transaction = repository.getTransaction(operationId);
        if (transaction == null) {
            throw new TransactionException(ERROR_TRANSACTION_NOT_FOUND);
        }
        if (!transaction.isNew()) {
            throw new TransactionException(ERROR_TRANSACTION_COMMITED);
        }
        if (!transaction.hasCode(code)) {
            throw new TransactionException(ERROR_TRANSACTION_WRONG_CODE);
        }

        TransactionResult status = paymentSystem.performTransaction(transaction);

        transaction.setStatus(status == TransactionResult.OK ? TransactionStatus.COMMITED : TransactionStatus.FAILED);

        repository.putTransaction(operationId, transaction);

        logger.log(String.format(
            "перевод с %s на %s, объём: %d, комиссия: %d",
            transaction.getFromCard(),
            transaction.getToCard(),
            transaction.getAmount(),
            transaction.getCommission()
        ));

        return operationId;
    }

}
