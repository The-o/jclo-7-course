package ru.netology.model;

public class Transaction {

    public enum TransactionStatus {
        NEW,
        COMMITED,
        FAILED
    };

    private final String fromCard;
    private final String fromValidTill;
    private final String fromCvv;
    private final String toCard;
    private final long amount;
    private final long commission;
    private final String currency;

    private TransactionStatus status;
    private String code;

    public Transaction(String fromCard, String fromValidTill, String fromCvv, String toCard, long amount,
            String currency, String code) {
        this.fromCard = fromCard;
        this.fromValidTill = fromValidTill;
        this.fromCvv = fromCvv;
        this.toCard = toCard;
        this.commission = amount / 100;
        this.amount = amount - this.commission;
        this.currency = currency;

        this.code = code;
        this.status = TransactionStatus.NEW;
    }

    public String getFromCard() {
        return fromCard;
    }

    public String getFromValidTill() {
        return fromValidTill;
    }

    public String getFromCvv() {
        return fromCvv;
    }

    public String getToCard() {
        return toCard;
    }

    public long getAmount() {
        return amount;
    }

    public long getCommission() {
        return commission;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isNew() {
        return status == TransactionStatus.NEW;
    }

    public boolean isCommited() {
        return status == TransactionStatus.COMMITED;
    }

    public boolean isFailed() {
        return status == TransactionStatus.FAILED;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public boolean hasCode(String code){
        return this.code.equals(code);
    }
}
