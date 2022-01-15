package pl.wojkacz.Data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import pl.wojkacz.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Account {
    String accountName;
    Long accountID;
    Double balancePLN;
    Double balanceEUR;
    Double balanceGBP;
    Double balanceUSD;
    List<Transfer> transfers = new ArrayList<>();

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

    public static Account getCurrentAccount() { return AccountHolder.account; }
    public static void setCurrentAccount(Account account) { AccountHolder.account = account; }

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

    public void setTransfers(List<Transfer> transfers) {
        this.transfers = transfers;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public static boolean getTransfersFromAPI(){
        Account.getCurrentAccount().getTransfers().clear();
        HttpClient httpClient = HttpClientBuilder.create().build();
        String getUrl = Main.api + "getTransfers?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&account_id=" + Account.getCurrentAccount().getAccountID();
        HttpGet request = new HttpGet(getUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200) { // Znaleziono konta
                List<Transfer> tr = Account.getCurrentAccount().getTransfers();
                JSONObject obj = new JSONObject(result);
                int amount = obj.getJSONObject("data").getInt("amount");
                for (int i = 1; i <= amount; i++) {
                    String transferName = "Transfer " + i;
                    Long senderAccountID = obj.getJSONObject("transfer_" + i).getLong("sender_account_id");
                    Long receiverAccountID = obj.getJSONObject("transfer_" + i).getLong("receiver_account_id");
                    Long currencyID = obj.getJSONObject("transfer_" + i).getLong("currency_id");
                    Double amountMoney = obj.getJSONObject("transfer_" + i).getDouble("amount");
                    LocalDate date = LocalDate.parse(obj.getJSONObject("transfer_" + i).getString("date"));
                    tr.add(new Transfer(transferName, senderAccountID, receiverAccountID, currencyID, amountMoney, date));
                }

                tr.sort(Comparator.comparing(Transfer::getDate).reversed());
            }
            else return false;
        } catch(Exception e) {
            System.out.println("[Get Transfers] " + e.getMessage());
            request.releaseConnection();
            return false;
        } finally {
            request.releaseConnection();
        }
        return true;
    }
}
