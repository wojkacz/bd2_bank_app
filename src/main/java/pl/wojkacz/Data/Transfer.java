package pl.wojkacz.Data;

import java.io.Serializable;
import java.time.LocalDate;

public class Transfer implements Serializable {
    String name;
    int index;
    Long senderAccountID;
    Long receiverAccountID;
    Long currencyID;
    Double amount;
    LocalDate date;

    Long pendingID;

    public Transfer(String name, Long senderAccountID, Long receiverAccountID, Long currencyID, Double amount, LocalDate date) {
        this.name = name;
        this.senderAccountID = senderAccountID;
        this.receiverAccountID = receiverAccountID;
        this.currencyID = currencyID;
        this.amount = amount;
        this.date = date;
    }

    public Transfer() {
    }

    public Long getPendingID() {
        return pendingID;
    }

    public void setPendingID(Long pendingID) {
        this.pendingID = pendingID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSenderAccountID() {
        return senderAccountID;
    }

    public void setSenderAccountID(Long senderAccountID) {
        this.senderAccountID = senderAccountID;
    }

    public Long getReceiverAccountID() {
        return receiverAccountID;
    }

    public void setReceiverAccountID(Long receiverAccountID) {
        this.receiverAccountID = receiverAccountID;
    }

    public Long getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(Long currencyID) {
        this.currencyID = currencyID;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
