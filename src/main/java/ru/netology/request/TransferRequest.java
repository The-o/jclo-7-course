package ru.netology.request;

import javax.validation.GroupSequence;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;

import ru.netology.validation.CardNumber;
import ru.netology.validation.CardValidTill;
import ru.netology.validation.groups.FirstOrder;
import ru.netology.validation.groups.SecondOrder;
import ru.netology.validation.groups.ThirdOrder;

@Validated
@GroupSequence({TransferRequest.class, FirstOrder.class, SecondOrder.class, ThirdOrder.class})
public class TransferRequest {

    @Validated
    public class TransferRequestAmount {

        @Min(value = 1, message = "Неверная сумма перевода")
        private long value;

        @NotNull(message = "Не указана валюта перевода")
        private String currency;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

    }

    @NotNull(message = "Не указан номер карты отправителя", groups = FirstOrder.class)
    @Pattern(regexp = "^\\d{16}$", message = "Неверный номер карты отправителя", groups = SecondOrder.class)
    @CardNumber(message = "Неверный номер карты отправителя", groups = ThirdOrder.class)
    private String cardFromNumber;

    @NotNull(message = "Не указана дата действия карты отправителя", groups = FirstOrder.class)
    @Pattern(regexp = "^\\d{2}/\\d{2}$", message = "Неверная дата действия карты отправителя", groups = SecondOrder.class)
    @CardValidTill(message = "Срок действия карты отправителя истёк", groups = ThirdOrder.class)
    private String cardFromValidTill;

    @NotNull(message = "Не указан CVV2/CVC2 код карты отправителя", groups = FirstOrder.class)
    @Pattern(regexp = "^\\d{3}$", message = "Неверный CVV2/CVC2 код карты отправителя", groups = SecondOrder.class)
    private String cardFromCVV;

    @NotNull(message = "Не указан номер карты адресата", groups = FirstOrder.class)
    @Pattern(regexp = "^\\d{16}$", message = "Неверный номер карты адресата", groups = SecondOrder.class)
    @CardNumber(message = "Неверный номер карты адресата", groups = ThirdOrder.class)
    private String cardToNumber;

    @NotNull(message = "Не указана сумма перевода")
    @Valid
    private TransferRequestAmount amount;

    public String getCardFromNumber() {
        return cardFromNumber;
    }

    public void setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
    }

    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public void setCardFromValidTill(String cardFromValidTill) {
        this.cardFromValidTill = cardFromValidTill;
    }

    public String getCardFromCVV() {
        return cardFromCVV;
    }

    public void setCardFromCVV(String cardFromCVV) {
        this.cardFromCVV = cardFromCVV;
    }

    public String getCardToNumber() {
        return cardToNumber;
    }

    public void setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
    }

    public TransferRequestAmount getAmount() {
        return amount;
    }

    public void setAmount(TransferRequestAmount amount) {
        this.amount = amount;
    }

}
