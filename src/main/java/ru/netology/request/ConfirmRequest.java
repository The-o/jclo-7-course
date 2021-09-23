package ru.netology.request;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public class ConfirmRequest {

    private static final String ERROR_NO_CONFIRMATION_CODE = "Не указан код подтверждения операции";
    private static final String ERROR_NO_OPERATION_ID = "Не указан ID операции";

    @NotBlank(message = ERROR_NO_OPERATION_ID)
    private String operationId;

    @NotBlank(message = ERROR_NO_CONFIRMATION_CODE)
    private String code;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
