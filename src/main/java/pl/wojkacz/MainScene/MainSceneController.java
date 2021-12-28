package pl.wojkacz.MainScene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

    final static String api = "http://localhost:8080/";
    HttpClient httpClient;
    UserData userData;

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
                        "tokenStr=" + userData.getToken();
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
    private void toggleSettingsMenu(){
        settingsPane.setVisible(!settingsPane.isVisible());
        createAccButton.setDisable(!createAccButton.isDisabled());
        logoutButton.setDisable(!logoutButton.isDisabled());
        settingsButton.setDisable(!settingsButton.isDisabled());
        for(Node n : accountsVBox.getChildren()){
            AccountTile at = (AccountTile) n;
            at.getManageButton().setDisable(!at.getManageButton().isDisabled());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userData = UserData.getUserData();
        welcomeLabel.setText("Hello, " + userData.getName() + " " + userData.getSurname() + "!");
        emailLabel.setText(userData.getLogin());

        notificationPane.setVisible(false);
        settingsPane.setVisible(false);

        refreshAccounts();
    }

    public void refreshAccounts(){
        accountsVBox.getChildren().clear();
        userData.getAccounts().clear();

        httpClient = HttpClientBuilder.create().build();
        String getUrl = api + "getAccounts?" +
                "tokenStr=" + userData.getToken();
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
                    List<Account> accs = userData.getAccounts();
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
        } finally {
            request.releaseConnection();
        }

        for(Account acc : userData.getAccounts()){
            accountsVBox.getChildren().add(
                    new AccountTile(acc.getAccountName(), acc.getAccountID(), acc.getBalancePLN(), acc.getBalanceEUR(), acc.getBalanceUSD(), acc.getBalanceGBP())
            );
        }
    }

    @FXML
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
