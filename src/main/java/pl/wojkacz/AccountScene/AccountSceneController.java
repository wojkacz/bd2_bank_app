package pl.wojkacz.AccountScene;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import pl.wojkacz.CustomObjects.TransferTile;
import pl.wojkacz.Data.Account;
import pl.wojkacz.Data.Transfer;
import pl.wojkacz.Data.UserData;
import pl.wojkacz.Main;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class AccountSceneController implements Initializable {

    private static final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final DecimalFormat df2 = new DecimalFormat("0.0000", new DecimalFormatSymbols(Locale.ENGLISH));
    private int selectedTransfer = 0;
    Long accountID = null;
    Double amount = null;
    String currencySelectedText = null;
    // **********************************************************************
    // Main page

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label idLabel;

    @FXML
    private Label balancePLN;

    @FXML
    private Label balanceEUR;

    @FXML
    private Label balanceGBP;

    @FXML
    private Label balanceUSD;

    @FXML
    private Button returnButton;

    @FXML
    private Button newTransferButton;

    @FXML
    private Button exchangeButton;

    @FXML
    private Button seeTransferButton;

    @FXML
    private Button refreshButton;

    @FXML
    private VBox transfersVBox;

    // **********************************************************************
    // Exchange page

    @FXML
    private AnchorPane exchangePane;

    @FXML
    private ChoiceBox<String> fromChoiceBox;

    @FXML
    private ChoiceBox<String> toChoiceBox;

    @FXML
    private TextField e_amountTextField;

    @FXML
    private Label e_errorLabel;

    @FXML
    private TextField e_exchangeAmountTextField;

    @FXML
    private Label usdPrice;

    @FXML
    private Label eurPrice;

    @FXML
    private Label gbpPrice;


    // **********************************************************************
    // See Transfer page

    @FXML
    private AnchorPane seeTransferPane;

    @FXML
    private Label senderIdLabel;

    @FXML
    private Label receiverIdLabel;

    @FXML
    private Label currencyNameLabel;

    @FXML
    private Label amountLabel;

    @FXML
    private Label dateLabel;

    // **********************************************************************
    // New Transfer page

    @FXML
    private AnchorPane newTransferPane;

    @FXML
    private TextField receiverTextField;

    @FXML
    private TextField amountTextField;

    @FXML
    private ChoiceBox<String> currencyChoiceBox;

    @FXML
    private Label n_errorLabel;

    @FXML
    private Button exitButton;

    @FXML
    private Button sendButton;

    // **********************************************************************
    // Confirm Transfer page

    @FXML
    private AnchorPane confirmTransferPane;

    @FXML
    private Label c_toLabel;

    @FXML
    private Label c_amountLabel;

    @FXML
    private Label c_currencyLabel;

    @FXML
    private Label c_approximatelyLabel;

    // **********************************************************************

    @FXML
    private void returnToUserView(){
        Account.setCurrentAccount(null);
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../MainScene/MainScene.fxml")));
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

    private void toggleButtons(){
        returnButton.setDisable(!returnButton.isDisable());
        newTransferButton.setDisable(!newTransferButton.isDisable());
        exchangeButton.setDisable(!exchangeButton.isDisable());
        seeTransferButton.setDisable(!seeTransferButton.isDisable());
        refreshButton.setDisable(!refreshButton.isDisable());
    }

    @FXML
    private void toggleConfirmationPaneVisibility(){
        exitButton.setDisable(!exitButton.isDisable());
        sendButton.setDisable(!sendButton.isDisable());
        confirmTransferPane.setVisible(!confirmTransferPane.isVisible());
    }

    @FXML
    private void toggleExchangePaneVisibility(){
        e_errorLabel.setVisible(false);
        e_errorLabel.setTextFill(Color.RED);

        if(!exchangePane.isVisible())
            refreshExchangeRate();

        exchangePane.setVisible(!exchangePane.isVisible());
        toggleButtons();
    }

    @FXML
    private void toggleSeeTransferPaneVisibility(){
        if(!seeTransferPane.isVisible()) {
            Transfer t = Account.getCurrentAccount().getTransfers().get(selectedTransfer);
            senderIdLabel.setText(t.getSenderAccountID().toString());
            receiverIdLabel.setText(t.getReceiverAccountID().toString());
            amountLabel.setText(t.getAmount().toString());
            dateLabel.setText(t.getDate().toString());

            if(t.getCurrencyID().equals(0L))
                currencyNameLabel.setText("PLN");
            else if(t.getCurrencyID().equals(1L))
                currencyNameLabel.setText("USD");
            else if(t.getCurrencyID().equals(2L))
                currencyNameLabel.setText("GBP");
            else if(t.getCurrencyID().equals(3L))
                currencyNameLabel.setText("EUR");
        }
        seeTransferPane.setVisible(!seeTransferPane.isVisible());
        toggleButtons();
    }

    @FXML
    private boolean setChanges(ActionEvent actionEvent){
        e_errorLabel.setVisible(false);
        e_errorLabel.setTextFill(Color.RED);

        String from = fromChoiceBox.getValue();
        String to = toChoiceBox.getValue();
        Double val = null;

        if(to.equals(from)){
            e_errorLabel.setText("You can not exchange same currencies!");
            e_errorLabel.setVisible(true);
            return false;
        }

        try {
            val = Double.parseDouble(e_amountTextField.getText());
        } catch(NumberFormatException e) {
            e_errorLabel.setText("Amount must be numeric value!");
            e_errorLabel.setVisible(true);
            return false;
        }
        if(val < 0.01) {
            e_errorLabel.setText("Amount must not be smaller than 0.01!");
            e_errorLabel.setVisible(true);
            return false;
        }

        JSONObject obj = refreshExchangeRate();
        if(obj == null) return false;

        Double price = null;
        if(to.equals("PLN"))
            price = obj.getJSONObject(from).getDouble("buy_price");
        else if(from.equals("PLN"))
            price = obj.getJSONObject(to).getDouble("sell_price");
        else {
            price = obj.getJSONObject(from).getDouble("buy_price");
            val *= price;

            price = obj.getJSONObject(to).getDouble("sell_price");
        }

        double retVal = val*price;
        if(retVal < 0.01) {
            e_errorLabel.setText("Value after exchange must be at lest 0.01!");
            e_errorLabel.setVisible(true);
            return false;
        }

        e_exchangeAmountTextField.setText(df.format(retVal));
        return true;
    }

    @FXML
    private JSONObject refreshExchangeRate(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String getUrl = Main.api + "getCurrencies";
        HttpGet request = new HttpGet(getUrl);
        JSONObject obj = null;
        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            if (response.getStatusLine().getStatusCode() == 200) {
                obj = new JSONObject(result);
            } else {
                e_errorLabel.setText("Error with getting currency");
                request.releaseConnection();
                return null;
            }
        } catch (Exception e) {
            System.out.println("[Get Currencies] " + e.getMessage());
            e_errorLabel.setText("Unknown Error");
            request.releaseConnection();
            return null;
        } finally {
            request.releaseConnection();
        }

        String eurPriceText = df2.format(obj.getJSONObject("EUR").getDouble("buy_price")) + " | " + df2.format(obj.getJSONObject("EUR").getDouble("sell_price"));
        eurPrice.setText(eurPriceText);

        String usdPriceText = df2.format(obj.getJSONObject("USD").getDouble("buy_price")) + " | " + df2.format(obj.getJSONObject("USD").getDouble("sell_price"));
        usdPrice.setText(usdPriceText);

        String gbpPriceText = df2.format(obj.getJSONObject("GBP").getDouble("buy_price")) + " | " + df2.format(obj.getJSONObject("GBP").getDouble("sell_price"));
        gbpPrice.setText(gbpPriceText);
        return obj;
    }

    @FXML
    private void exchangeCurrency(){
        if(setChanges(null)){

            HttpClient httpClient = HttpClientBuilder.create().build();
            String postUrl = Main.api + "exchangeCurrency" +
                    "?tokenStr=" + UserData.getUserData().getToken() +
                    "&accountID=" + Account.getCurrentAccount().getAccountID() +
                    "&amount=" + df.format(Double.parseDouble(e_amountTextField.getText())) +
                    "&currencyFromName=" + fromChoiceBox.getValue() +
                    "&currencyToName=" + toChoiceBox.getValue();
            System.out.println(postUrl);
            HttpPost request = new HttpPost(postUrl);

            try {
                HttpResponse response = httpClient.execute(request);
                switch (response.getStatusLine().getStatusCode()){
                    case 200:
                        e_amountTextField.setText("");
                        e_errorLabel.setTextFill(Color.GREEN);
                        e_errorLabel.setText("Currency exchanged successfully!");
                        e_errorLabel.setVisible(true);
                        break;
                    case 405: // brak srodkow
                        e_errorLabel.setText("Insufficient balance on your account!");
                        e_errorLabel.setVisible(true);
                        break;
                    case 406:
                        e_errorLabel.setText("Value must be at lest 0.01!");
                        e_errorLabel.setVisible(true);
                        break;
                    case 417:
                        request.releaseConnection();
                        logout();
                        return;
                    default:
                        e_errorLabel.setText("Unknown error!");
                        e_errorLabel.setVisible(true);
                        break;
                }
            } catch (Exception e) {
                System.out.println("[Exchange] " + e.getMessage());
                request.releaseConnection();
                logout();
                return;
            } finally {
                request.releaseConnection();
            }

            refreshAccountButton();
        }
    }

    @FXML
    private void sendTransfer(){
        n_errorLabel.setVisible(false);
        n_errorLabel.setTextFill(Color.RED);

        String accountIDText = receiverTextField.getText();
        String amountText = amountTextField.getText();
        currencySelectedText = currencyChoiceBox.getValue();

        if(accountIDText == null) {
            n_errorLabel.setText("Account ID must not be empty!");
            n_errorLabel.setVisible(true);
            return;
        }

        if(amountText == null) {
            n_errorLabel.setText("Amount must not be empty!");
            n_errorLabel.setVisible(true);
            return;
        }


        try {
            accountID = Long.parseLong(accountIDText);
        } catch(NumberFormatException e) {
            n_errorLabel.setText("Account ID must be numeric value!");
            n_errorLabel.setVisible(true);
            return;
        }
        if(accountID < 0) {
            n_errorLabel.setText("Account ID must not be smaller than 0!");
            n_errorLabel.setVisible(true);
            return;
        }


        try {
            amount = Double.parseDouble(amountText);
        } catch(NumberFormatException e) {
            n_errorLabel.setText("Amount must be numeric value!");
            n_errorLabel.setVisible(true);
            return;
        }
        if(amount < 0.01) {
            n_errorLabel.setText("Amount must not be smaller than 0.01!");
            n_errorLabel.setVisible(true);
            return;
        }

        if(!currencySelectedText.equals("PLN") && !currencySelectedText.equals("GBP") && !currencySelectedText.equals("USD") && !currencySelectedText.equals("EUR")) {
                n_errorLabel.setText("Internal error with currency!");
                n_errorLabel.setVisible(true);
                return;
        }

        if(accountID.equals(Account.getCurrentAccount().getAccountID())){
            n_errorLabel.setText("You can not send transfer to the same account!");
            n_errorLabel.setVisible(true);
            return;
        }

        c_toLabel.setText(accountIDText);
        c_amountLabel.setText(df.format(amount));
        c_currencyLabel.setText(currencySelectedText);

        if(currencySelectedText.equals("PLN"))
            c_approximatelyLabel.setText("-");
        else {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String getUrl = Main.api + "getCurrencies";
            HttpGet request = new HttpGet(getUrl);

            try {
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                if (response.getStatusLine().getStatusCode() == 200) {
                    JSONObject obj = new JSONObject(result);
                    Double price = obj.getJSONObject(currencySelectedText).getDouble("buy_price");
                    c_approximatelyLabel.setText(df.format(amount * price) + " PLN");
                } else {
                    c_approximatelyLabel.setText("!ERROR!");
                }
            } catch (Exception e) {
                System.out.println("[Get Currencies] " + e.getMessage());
                c_approximatelyLabel.setText("!ERROR!");
            } finally {
                request.releaseConnection();
            }
        }

        toggleConfirmationPaneVisibility();
    }

    @FXML
    private void acceptTransfer(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String postUrl = Main.api + "sendTransfer" +
                "?tokenStr=" + UserData.getUserData().getToken() +
                "&sender_account_id=" + Account.getCurrentAccount().getAccountID() +
                "&receiver_account_id=" + accountID +
                "&currencyName=" + currencySelectedText +
                "&amount=" + df.format(amount);
        System.out.println(postUrl);
        HttpPost request = new HttpPost(postUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            switch (response.getStatusLine().getStatusCode()){

                case 200:
                    receiverTextField.setText("");
                    amountTextField.setText("");

                    n_errorLabel.setTextFill(Color.GREEN);
                    n_errorLabel.setText("Transfer sent!");
                    n_errorLabel.setVisible(true);
                    refreshAccountButton();
                    break;
                case 202:
                    receiverTextField.setText("");
                    amountTextField.setText("");

                    n_errorLabel.setTextFill(Color.GREEN);
                    n_errorLabel.setText("Transfer added to pending list!");
                    n_errorLabel.setVisible(true);
                    refreshAccountButton();
                    break;
                case 400:
                    n_errorLabel.setText("[Internal] Currency not found!");
                    n_errorLabel.setVisible(true);
                    break;
                case 409:
                    n_errorLabel.setText("Incorrect receiver account ID!");
                    n_errorLabel.setVisible(true);
                    break;
                case 303:
                    n_errorLabel.setText("[Internal] Incorrect sender account ID!");
                    n_errorLabel.setVisible(true);
                    break;
                case 417:
                    request.releaseConnection();
                    logout();
                    return;
                case 406:
                    n_errorLabel.setText("Insufficient Balance!");
                    n_errorLabel.setVisible(true);
                    break;
                default:
                    n_errorLabel.setText("Unknown error!");
                    n_errorLabel.setVisible(true);
                    break;
            }
        } catch (Exception e) {
            System.out.println("[Send Transfer] " + e.getMessage());
            request.releaseConnection();
            logout();
            return;
        } finally {
            request.releaseConnection();
        }
        toggleConfirmationPaneVisibility();
    }

    @FXML
    private void toggleNewTransferPaneVisibility(){
        newTransferPane.setVisible(!newTransferPane.isVisible());

        receiverTextField.setText("");
        amountTextField.setText("");

        n_errorLabel.setVisible(false);
        n_errorLabel.setTextFill(Color.RED);
        toggleButtons();
    }


    public void selectTransfer(int index){
        if(index == selectedTransfer)
            return;

        TransferTile tt = (TransferTile) transfersVBox.getChildren().get(selectedTransfer);
        tt.deselect();
        selectedTransfer = index;
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



    @FXML
    private void refreshAccountButton(){
        transfersVBox.getChildren().clear();
        refreshAccount();
    }

    private void refreshAccount(){
        toggleButtons();
        if(!UserData.refreshAccountsData()){
            logout();
        }

        if(!Account.getTransfersFromAPI()){
            logout();
        }

        nameLabel.setText(Account.getCurrentAccount().getAccountName());
        idLabel.setText("ID: " + Account.getCurrentAccount().getAccountID());
        balancePLN.setText(df.format(Account.getCurrentAccount().getBalancePLN()));
        balanceEUR.setText(df.format(Account.getCurrentAccount().getBalanceEUR()));
        balanceGBP.setText(df.format(Account.getCurrentAccount().getBalanceGBP()));
        balanceUSD.setText(df.format(Account.getCurrentAccount().getBalanceUSD()));

        int i = 0;
        for(Transfer t : Account.getCurrentAccount().getTransfers()) {
            t.setIndex(i);
            transfersVBox.getChildren().add(new TransferTile(t, this));
            i++;
        }

        toggleButtons();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        df.setRoundingMode(RoundingMode.DOWN);
        df2.setRoundingMode(RoundingMode.DOWN);
        refreshAccount();
        seeTransferPane.setVisible(false);
        newTransferPane.setVisible(false);
        exchangePane.setVisible(false);
        confirmTransferPane.setVisible(false);

        currencyChoiceBox.setItems(FXCollections.observableArrayList("PLN", "USD", "GBP", "EUR"));
        currencyChoiceBox.setValue("PLN");

        toChoiceBox.setItems(FXCollections.observableArrayList("PLN", "USD", "GBP", "EUR"));
        toChoiceBox.setValue("PLN");
        toChoiceBox.setOnAction(this::setChanges);

        fromChoiceBox.setItems(FXCollections.observableArrayList("PLN", "USD", "GBP", "EUR"));
        fromChoiceBox.setValue("PLN");
        fromChoiceBox.setOnAction(this::setChanges);

        e_exchangeAmountTextField.setEditable(false);
    }
}
