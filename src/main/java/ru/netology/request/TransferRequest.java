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

    private static final String PATTERN_CARD_NUMBER = "^\\d{16}$";
    private static final String PATTERN_VALID_TILL = "^\\d{2}/\\d{2}$";
    private static final String PATTERN_CVV = "^\\d{3}$";

    private static final String ERROR_NO_CARD_FROM_NUMBER = "Не указан номер карты отправителя";
    private static final String ERROR_WRONG_CARD_FROM_NUMBER = "Неверный номер карты отправителя";
    private static final String ERROR_NO_FROM_VALID_TILL = "Не указана дата действия карты отправителя";
    private static final String ERROR_WRONG_FROM_VALID_TILL = "Неверная дата действия карты отправителя";
    private static final String ERROR_EXPIRED_FROM_VALID_TILL = "Срок действия карты отправителя истёк";
    private static final String ERROR_NO_FROM_CVV = "Не указан CVV2/CVC2 код карты отправителя";
    private static final String ERROR_WRONG_FROM_CVV = "Неверный CVV2/CVC2 код карты отправителя";
    private static final String ERROR_NO_CARD_TO_NUMBER = "Не указан номер карты адресата";
    private static final String ERROR_WRONG_CARD_TO_NUMBER = "Неверный номер карты адресата";
    private static final String ERROR_NO_TRANSFER_AMOUNT = "Не указана сумма перевода";

    @Validated
    public class TransferRequestAmount {

        private static final String ERROR_NO_TRANSFER_VALUE = "Неверная сумма перевода";
        private static final String ERROR_NO_TRANSFER_CURRENCY = "Не указана валюта перевода";

        @Min(value = 1, message = ERROR_NO_TRANSFER_VALUE)
        private long value;

        @NotNull(message = ERROR_NO_TRANSFER_CURRENCY)
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

    @NotNull(message = ERROR_NO_CARD_FROM_NUMBER, groups = FirstOrder.class)
    @Pattern(regexp = PATTERN_CARD_NUMBER, message = ERROR_WRONG_CARD_FROM_NUMBER, groups = SecondOrder.class)
    @CardNumber(message = ERROR_WRONG_CARD_FROM_NUMBER, groups = ThirdOrder.class)
    private String cardFromNumber;

    @NotNull(message = ERROR_NO_FROM_VALID_TILL, groups = FirstOrder.class)
    @Pattern(regexp = PATTERN_VALID_TILL, message = ERROR_WRONG_FROM_VALID_TILL, groups = SecondOrder.class)
    @CardValidTill(message = ERROR_EXPIRED_FROM_VALID_TILL, groups = ThirdOrder.class)
    private String cardFromValidTill;

    @NotNull(message = ERROR_NO_FROM_CVV, groups = FirstOrder.class)
    @Pattern(regexp = PATTERN_CVV, message = ERROR_WRONG_FROM_CVV, groups = SecondOrder.class)
    private String cardFromCVV;

    @NotNull(message = ERROR_NO_CARD_TO_NUMBER, groups = FirstOrder.class)
    @Pattern(regexp = PATTERN_CARD_NUMBER, message = ERROR_WRONG_CARD_TO_NUMBER, groups = SecondOrder.class)
    @CardNumber(message = ERROR_WRONG_CARD_TO_NUMBER, groups = ThirdOrder.class)
    private String cardToNumber;

    @NotNull(message = ERROR_NO_TRANSFER_AMOUNT)
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
