package pl.wojkacz.Data;

public class Account {
    String accountName;
    Long accountID;
    Double balancePLN;
    Double balanceEUR;
    Double balanceGBP;
    Double balanceUSD;

    public Account() {
    }

    public Account(String accountName, Long accountID, Double balancePLN, Double balanceEUR, Double balanceGBP, Double balanceUSD) {
        this.accountName = accountName;
        this.accountID = accountID;
        this.balancePLN = balancePLN;
        this.balanceEUR = balanceEUR;
        this.balanceGBP = balanceGBP;
        this.balanceUSD = balanceUSD;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getAccountID() {
        return accountID;
    }

    public void setAccountID(Long accountID) {
        this.accountID = accountID;
    }

    public Double getBalancePLN() {
        return balancePLN;
    }

    public void setBalancePLN(Double balancePLN) {
        this.balancePLN = balancePLN;
    }

    public Double getBalanceEUR() {
        return balanceEUR;
    }

    public void setBalanceEUR(Double balanceEUR) {
        this.balanceEUR = balanceEUR;
    }

    public Double getBalanceGBP() {
        return balanceGBP;
    }

    public void setBalanceGBP(Double balanceGBP) {
        this.balanceGBP = balanceGBP;
    }

    public Double getBalanceUSD() {
        return balanceUSD;
    }

    public void setBalanceUSD(Double balanceUSD) {
        this.balanceUSD = balanceUSD;
    }
}
