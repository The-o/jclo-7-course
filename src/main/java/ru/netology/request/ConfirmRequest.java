package ru.netology.request;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public class ConfirmRequest {

    @NotBlank(message = "Не указан ID операции")
    private String operationId;

    @NotBlank(message = "Не указан код подтверждения операции")
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
