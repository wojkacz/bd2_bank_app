package pl.wojkacz.Data;

import java.util.ArrayList;
import java.util.List;

public class UserData {

    String name;
    String surname;
    String login;
    int permission;
    String token;
    List<Account> accounts = new ArrayList<>();

    public UserData() {
    }

    public UserData(String name, String surname, String login, int permission, String token) {
        this.name = name;
        this.surname = surname;
        this.login = login;
        this.permission = permission;
        this.token = token;
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

