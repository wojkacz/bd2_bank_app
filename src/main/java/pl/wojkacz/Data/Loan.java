package pl.wojkacz.Data;

import java.time.LocalDate;

public class Loan {
    Double amount;
    Double toPayBackTotal;
    Double installement;
    int length;
    int paidInstallements;
    LocalDate date;

    Long accountID;
    Long userID;
    Long pendingID;
    int index;

    public Loan(Double amount, Double toPayBackTotal, int length, int paidInstallements, LocalDate date) {
        this.amount = amount;
        this.toPayBackTotal = toPayBackTotal;
        this.length = length;
        this.paidInstallements = paidInstallements;
        this.date = date;
        installement = toPayBackTotal / length;
    }

    public Long getAccountID() {
        return accountID;
    }

    public void setAccountID(Long accountID) {
        this.accountID = accountID;
    }

    public Long getPendingID() {
        return pendingID;
    }

    public void setPendingID(Long pendingID) {
        this.pendingID = pendingID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Double getAmount() {
        return amount;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getToPayBackTotal() {
        return toPayBackTotal;
    }

    public void setToPayBackTotal(Double toPayBackTotal) {
        this.toPayBackTotal = toPayBackTotal;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPaidInstallements() {
        return paidInstallements;
    }

    public void setPaidInstallements(int paidInstallements) {
        this.paidInstallements = paidInstallements;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getInstallement() {
        return installement;
    }

    public void setInstallement(Double installement) {
        this.installement = installement;
    }
}
