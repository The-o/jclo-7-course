package ru.netology.unit.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;

import ru.netology.model.Transaction;
import ru.netology.model.Transaction.TransactionStatus;
import ru.netology.repository.TransactionRepository;
import ru.netology.service.Logger;
import ru.netology.service.PaymentSystemClient;
import ru.netology.service.SmsCodeGenerator;
import ru.netology.service.TransferService;
import ru.netology.service.PaymentSystemClient.TransactionResult;
import ru.netology.service.exception.TransactionException;

@SpringBootTest
public class TransferServiceTests {

    private PaymentSystemClient paymentSystemMock;
    private TransactionRepository repositoryMock;
    private SmsCodeGenerator generatorMock;
    private Logger loggerMock;
    private TransferService service;

    @BeforeEach
    public void initTests() {
        paymentSystemMock = mock(PaymentSystemClient.class);
        repositoryMock = mock(TransactionRepository.class);
        generatorMock = mock(SmsCodeGenerator.class);
        loggerMock = mock(Logger.class);

        service = new TransferService(paymentSystemMock, repositoryMock, generatorMock, loggerMock);
    }

    @Test
    public void prepareTransactionTest() {
        when(generatorMock.generate()).thenReturn("1234");

        String operationId = service.prepareTransaction("1234567890123456", "01/01", "123", "0987654321098765", 10000, "RUR");

        ArgumentCaptor<String> operationIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(repositoryMock).putTransaction(operationIdCaptor.capture(), transactionCaptor.capture());

        Transaction actualTransaction = transactionCaptor.getValue();
        assertAll(
            () -> assertEquals(operationId, operationIdCaptor.getValue()),
            () -> assertEquals("1234567890123456", actualTransaction.getFromCard()),
            () -> assertEquals("01/01", actualTransaction.getFromValidTill()),
            () -> assertEquals("123", actualTransaction.getFromCvv()),
            () -> assertEquals("0987654321098765", actualTransaction.getToCard()),
            () -> assertEquals(9900, actualTransaction.getAmount()),
            () -> assertEquals(100, actualTransaction.getCommission()),
            () -> assertEquals("RUR", actualTransaction.getCurrency()),
            () -> assertTrue(actualTransaction.hasCode("1234"))
        );
    }

    @Test
    public void performTransactionTestOk() throws IOException {
        Transaction transactionMock = mock(Transaction.class);

        when(transactionMock.isNew()).thenReturn(true);
        when(transactionMock.hasCode("1234")).thenReturn(true);
        when(repositoryMock.getTransaction("id321")).thenReturn(transactionMock);
        when(paymentSystemMock.performTransaction(transactionMock)).thenReturn(TransactionResult.OK);

        String operationId = service.performTransaction("id321", "1234");

        assertAll(
            () -> verify(transactionMock).setStatus(TransactionStatus.COMMITED),
            () -> verify(repositoryMock).putTransaction("id321", transactionMock),
            () -> assertEquals("id321", operationId)
        );
    }

    @Test
    public void performTransactionTestFail() throws IOException {
        Transaction transactionMock = mock(Transaction.class);

        when(transactionMock.isNew()).thenReturn(true);
        when(transactionMock.hasCode("1234")).thenReturn(true);
        when(repositoryMock.getTransaction("id321")).thenReturn(transactionMock);
        when(paymentSystemMock.performTransaction(transactionMock)).thenReturn(TransactionResult.FAIL);

        String operationId = service.performTransaction("id321", "1234");

        assertAll(
            () -> verify(transactionMock).setStatus(TransactionStatus.FAILED),
            () -> verify(repositoryMock).putTransaction("id321", transactionMock),
            () -> assertEquals("id321", operationId)
        );
    }

    @Test
    public void performTransactionTestNoTransaction() throws IOException {
        when(repositoryMock.getTransaction("id321")).thenReturn(null);
        TransactionException exception = assertThrows(TransactionException.class, () -> service.performTransaction("id321", "1234"));

        assertEquals("Операция не найдена", exception.getMessage());
    }

    @Test
    public void performTransactionTestTransactionPerformed() throws IOException {
        Transaction transactionMock = mock(Transaction.class);

        when(transactionMock.isNew()).thenReturn(false);
        when(repositoryMock.getTransaction("id321")).thenReturn(transactionMock);

        TransactionException exception = assertThrows(TransactionException.class, () -> service.performTransaction("id321", "1234"));

        assertEquals("Операция уже выполнена", exception.getMessage());
    }

    @Test
    public void performTransactionTestWrongCode() throws IOException {
        Transaction transactionMock = mock(Transaction.class);

        when(transactionMock.isNew()).thenReturn(true);
        when(transactionMock.hasCode(anyString())).thenReturn(false);
        when(repositoryMock.getTransaction("id321")).thenReturn(transactionMock);

        TransactionException exception = assertThrows(TransactionException.class, () -> service.performTransaction("id321", "1234"));

        assertEquals("Неверный код подтверждения операции", exception.getMessage());
    }

}
