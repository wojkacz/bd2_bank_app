package pl.wojkacz.MainScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import pl.wojkacz.CustomObjects.AccountTile;
import pl.wojkacz.Data.Account;
import pl.wojkacz.Data.UserData;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    final static String api = "http://localhost:8080/";
    HttpClient httpClient;
    MessageDigest md;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private VBox accountsVBox;

    @FXML
    private AnchorPane notificationPane;

    @FXML
    private Button createAccButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button settingsButton;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private Label set_errorLabel;

    @FXML
    private TextField set_nameTextField;

    @FXML
    private TextField set_surnameTextField;

    @FXML
    private TextField set_emailTextField;

    @FXML
    private TextField set_newPasswordTextField;

    @FXML
    private PasswordField set_passwordTextField;

    @FXML
    private CheckBox set_nameCheckBox;

    @FXML
    private CheckBox set_surnameCheckBox;

    @FXML
    private CheckBox set_emailCheckBox;

    @FXML
    private CheckBox set_passwordCheckBox;

    @FXML
    private void changeNameEditable(){
        set_nameTextField.setEditable(set_nameCheckBox.isSelected());
    }

    @FXML
    private void changeSurnameEditable(){
        set_surnameTextField.setEditable(set_surnameCheckBox.isSelected());
    }

    @FXML
    private void changeEmailEditable(){
        set_emailTextField.setEditable(set_emailCheckBox.isSelected());
    }

    @FXML
    private void changePasswordEditable(){
        set_newPasswordTextField.setEditable(set_passwordCheckBox.isSelected());
    }

    @FXML
    private void createAccount(){
        for(Node n : accountsVBox.getChildren()){
            AccountTile at = (AccountTile) n;
            at.getManageButton().setDisable(!at.getManageButton().isDisabled());
        }
        Label l = (Label) notificationPane.getChildren().get(1);
        l.setText("Are you sure you want to create account?\nThis action can not be undone!");

        Button cancelButton = (Button) notificationPane.getChildren().get(2);
        Button submitButton = (Button) notificationPane.getChildren().get(3);

        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                notificationPane.setVisible(false);
                createAccButton.setDisable(false);
                logoutButton.setDisable(false);
                settingsButton.setDisable(false);
                for(Node n : accountsVBox.getChildren()){
                    AccountTile at = (AccountTile) n;
                    at.getManageButton().setDisable(!at.getManageButton().isDisabled());
                }
            }
        });

        submitButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String postUrl = api + "createAccount?" +
                        "tokenStr=" + UserData.getUserData().getToken();
                HttpPost request = new HttpPost(postUrl);

                try {
                    HttpResponse response = httpClient.execute(request);
                    switch(response.getStatusLine().getStatusCode()){
                        case 417:
                            logout();
                            break;
                        case 401:
                            // Admin nie moze
                            break;
                        case 200:
                            refreshAccounts();
                            break;
                        default:
                            // Nieznany blad
                            break;
                    }
                } catch(Exception e) {
                    System.out.println("[Create Account] " + e.getMessage());
                    logout();
                } finally {
                    request.releaseConnection();
                }

                notificationPane.setVisible(false);
                createAccButton.setDisable(false);
                logoutButton.setDisable(false);
                settingsButton.setDisable(false);
            }
        });

        notificationPane.setVisible(true);
        createAccButton.setDisable(true);
        logoutButton.setDisable(true);
        settingsButton.setDisable(true);
    }

    @FXML
    private void applyChanges(){
        set_errorLabel.setVisible(false);
        set_errorLabel.setTextFill(Color.RED);

        String name = set_nameTextField.getText();
        String surname = set_surnameTextField.getText();
        String email = set_emailTextField.getText();

        if(set_passwordTextField.getText().length() <= 1){
            set_errorLabel.setText("Password too short!");
            set_errorLabel.setVisible(true);
            return;
        }

        if(set_newPasswordTextField.getText().length() <= 1 && set_passwordCheckBox.isSelected()){
            set_errorLabel.setText("New Password too short!");
            set_errorLabel.setVisible(true);
            return;
        }

        httpClient = HttpClientBuilder.create().build();
        String getUrl = api + "updateUser?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&password_hash=" + getHash(set_passwordTextField.getText());

        if(set_nameCheckBox.isSelected()){
            if(name.length() > 1)
                getUrl = getUrl.concat("&name=").concat(name);
            else {
                set_errorLabel.setText("Name too short!");
                set_errorLabel.setVisible(true);
                return;
            }
        }

        if(set_surnameCheckBox.isSelected()) {
            if(surname.length() > 1)
                getUrl = getUrl.concat("&surname=").concat(surname);
            else {
                set_errorLabel.setText("Surname too short!");
                set_errorLabel.setVisible(true);
                return;
            }
        }

        if(set_emailCheckBox.isSelected()) {
            if(email.indexOf('@') == -1) {
                set_errorLabel.setText("Incorrect email!");
                set_errorLabel.setVisible(true);
                return;
            }
            else if(email.length() <= 1){
                set_errorLabel.setText("Email too short!");
                set_errorLabel.setVisible(true);
                return;
            }
            else getUrl = getUrl.concat("&login=").concat(email);
        }

        if(set_passwordCheckBox.isSelected())
            getUrl = getUrl.concat("&new_password_hash=").concat(getHash(set_newPasswordTextField.getText()));

        set_nameCheckBox.setDisable(true);
        set_surnameCheckBox.setDisable(true);
        set_emailCheckBox.setDisable(true);
        set_passwordCheckBox.setDisable(true);
        System.out.println(getUrl);
        HttpPost request = new HttpPost(getUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            switch(response.getStatusLine().getStatusCode()){
                case 417:
                    set_errorLabel.setText("Email must contain @!");
                    set_errorLabel.setVisible(true);
                    break;
                case 404:
                    logout();
                    break;
                case 200:
                    if(set_nameCheckBox.isSelected())
                        UserData.getUserData().setName(name);

                    if(set_surnameCheckBox.isSelected())
                        UserData.getUserData().setSurname(surname);

                    if(set_emailCheckBox.isSelected())
                        UserData.getUserData().setLogin(email);

                    welcomeLabel.setText("Hello, " + UserData.getUserData().getName() + " " + UserData.getUserData().getSurname() + "!");
                    emailLabel.setText(UserData.getUserData().getLogin());

                    set_errorLabel.setVisible(true);
                    set_errorLabel.setTextFill(Color.GREEN);
                    set_errorLabel.setText("Data changed successfully!");
                    break;
                case 403:
                    set_errorLabel.setText("Incorrect email or password!");
                    set_errorLabel.setVisible(true);
                    break;
                case 406:
                    set_errorLabel.setText("One of provided data is too short!");
                    set_errorLabel.setVisible(true);
                    break;
                case 409:
                    set_errorLabel.setText("That email is already taken!");
                    set_errorLabel.setVisible(true);
                    break;
                default:
                    set_errorLabel.setText("Unknown error!");
                    set_errorLabel.setVisible(true);
                    break;
            }
        } catch(Exception e) {
            System.out.println("[Set User Info] " + e.getMessage());
            logout();
        } finally {
            request.releaseConnection();
            set_nameCheckBox.setDisable(false);
            set_surnameCheckBox.setDisable(false);
            set_emailCheckBox.setDisable(false);
            set_passwordCheckBox.setDisable(false);
        }
    }

    private String getHash(String text){
        md.update(text.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for(byte b : digest)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @FXML
    private void toggleSettingsMenu(){
        settingsPane.setVisible(!settingsPane.isVisible());
        createAccButton.setDisable(!createAccButton.isDisabled());
        logoutButton.setDisable(!logoutButton.isDisabled());
        settingsButton.setDisable(!settingsButton.isDisabled());
        for(Node n : accountsVBox.getChildren()){
            AccountTile at = (AccountTile) n;
            at.getManageButton().setDisable(!at.getManageButton().isDisabled());
        }

        if(settingsPane.isVisible()){
            set_errorLabel.setVisible(false);

            set_nameTextField.setText(UserData.getUserData().getName());
            set_nameTextField.setEditable(false);
            set_nameCheckBox.setSelected(false);

            set_surnameTextField.setText(UserData.getUserData().getSurname());
            set_surnameTextField.setEditable(false);
            set_surnameCheckBox.setSelected(false);

            set_emailTextField.setText(UserData.getUserData().getLogin());
            set_emailTextField.setEditable(false);
            set_emailCheckBox.setSelected(false);

            set_newPasswordTextField.setText("");
            set_newPasswordTextField.setEditable(false);
            set_passwordCheckBox.setSelected(false);

            set_passwordTextField.setText("");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Hello, " + UserData.getUserData().getName() + " " + UserData.getUserData().getSurname() + "!");
        emailLabel.setText(UserData.getUserData().getLogin());

        notificationPane.setVisible(false);
        settingsPane.setVisible(false);

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[MessageDigest]" + e.getMessage());
        }
        assert md != null;

        refreshAccounts();
    }

    public void refreshAccounts(){
        accountsVBox.getChildren().clear();
        UserData.getUserData().getAccounts().clear();

        httpClient = HttpClientBuilder.create().build();
        String getUrl = api + "getAccounts?" +
                "tokenStr=" + UserData.getUserData().getToken();
        HttpGet request = new HttpGet(getUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            switch(response.getStatusLine().getStatusCode()){
                case 417:
                    logout();
                    break;
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
                    break;
                default:
                    // Nieznany blad
                    break;
            }
        } catch(Exception e) {
            System.out.println("[Get Accounts] " + e.getMessage());
            logout();
        } finally {
            request.releaseConnection();
        }

        for(Account acc : UserData.getUserData().getAccounts()){
            accountsVBox.getChildren().add(
                    new AccountTile(acc.getAccountName(), acc.getAccountID(), acc.getBalancePLN(), acc.getBalanceEUR(), acc.getBalanceUSD(), acc.getBalanceGBP())
            );
        }
    }

    @FXML
    private void logoutButton(){
        UserData.getUserData().setLogout(true);
        logout();
    }


    private void logout(){
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../Login/Login.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
