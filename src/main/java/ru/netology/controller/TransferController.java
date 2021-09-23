package ru.netology.controller;

import java.io.IOException;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.netology.request.ConfirmRequest;
import ru.netology.request.TransferRequest;
import ru.netology.response.ErrorResponse;
import ru.netology.response.TransferResponse;
import ru.netology.service.TransferService;
import ru.netology.service.exception.TransactionException;

@RestController
@RequestMapping("/")
public class TransferController {

    private static final String ERROR_INTERNAL = "Внутренняя ошибка сервера";
    private static final String ERROR_WRONG_FORMAT = "Неверный формат данных";
    
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    @CrossOrigin
    public TransferResponse transferHandler(@Valid @RequestBody TransferRequest request) {
        String operationId = transferService.prepareTransaction(
            request.getCardFromNumber(),
            request.getCardFromValidTill(),
            request.getCardFromCVV(),
            request.getCardToNumber(),
            request.getAmount().getValue(),
            request.getAmount().getCurrency()
        );
        return new TransferResponse(operationId);
    }

    @PostMapping("/confirmOperation")
    @CrossOrigin
    public TransferResponse confirmHandler(@Valid @RequestBody ConfirmRequest request) throws IOException {
        String operationId = transferService.performTransaction(request.getOperationId(), request.getCode());
        return new TransferResponse(operationId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(), 1);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(ERROR_WRONG_FORMAT, 2);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ErrorResponse> handleTransactionException(TransactionException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 3);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(ERROR_INTERNAL, 4);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
