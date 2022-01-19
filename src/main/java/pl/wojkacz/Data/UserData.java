package pl.wojkacz.Data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import pl.wojkacz.Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserData {

    String name;
    String surname;
    String login;
    int permission;
    String token;
    List<Account> accounts = new ArrayList<>();

    boolean logout = false;
    public UserData() {
    }

    public UserData(String name, String surname, String login, int permission, String token) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.permission = permission;
        this.token = token;
    }

    public static boolean refreshAccountsData() {
        UserData.getUserData().getAccounts().clear();

        HttpClient httpClient = HttpClientBuilder.create().build();
        String getUrl = Main.api + "getAccounts?" +
                "tokenStr=" + UserData.getUserData().getToken();
        HttpGet request = new HttpGet(getUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            switch(response.getStatusLine().getStatusCode()){
                case 404:
                    // Nie znaleziono kont
                    break;
                case 200:
                    // Znaleziono konta
                    List<Account> accs = UserData.getUserData().getAccounts();
                    JSONObject obj = new JSONObject(result);
                    int amount = obj.getJSONObject("data").getInt("amount");
                    for(int i = 1; i <= amount; i++){
                        String accName = "Account " + i;
                        Long accID = obj.getJSONObject("account_" + i).getLong("account_id");
                        Double balPLN = obj.getJSONObject("account_" + i).getDouble("balance_pln");
                        Double balEUR = obj.getJSONObject("account_" + i).getDouble("balance_euro");
                        Double balGBP = obj.getJSONObject("account_" + i).getDouble("balance_pound");
                        Double balUSD = obj.getJSONObject("account_" + i).getDouble("balance_usd");
                        accs.add(new Account(accName, accID, balPLN, balEUR, balGBP, balUSD));
                    }
                    accs.sort(Comparator.comparing(Account::getAccountID));
                    break;
                default:
                    return false;
            }
        } catch(Exception e) {
            System.out.println("[Get Accounts] " + e.getMessage());
            request.releaseConnection();
            return false;
        } finally {
            request.releaseConnection();
        }



        if(Account.getCurrentAccount() != null){
            for(Account acc : UserData.getUserData().getAccounts()){
                if(acc.getAccountID().equals(Account.getCurrentAccount().getAccountID())) {
                    Account.setCurrentAccount(acc);
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean isLogout() {
        return logout;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }

    public static UserData getUserData() {
        return UserDataHolder.userData;
    }

    public static void setUserData(UserData userData) {
        UserDataHolder.userData = userData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}

