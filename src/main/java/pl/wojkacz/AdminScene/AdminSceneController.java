package pl.wojkacz.AdminScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Text;
import pl.wojkacz.CustomObjects.PendingLoanTile;
import pl.wojkacz.CustomObjects.PendingTransferTile;
import pl.wojkacz.Data.Loan;
import pl.wojkacz.Data.Transfer;
import pl.wojkacz.Data.UserData;
import pl.wojkacz.Main;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.*;

public class AdminSceneController implements Initializable {
    private static final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    private int selectedTransfer = 0;
    private int selectedLoan = 0;
    public boolean buttonsDisabled = false;

    List<Transfer> transfers = new ArrayList<>();
    List<Loan> loans = new ArrayList<>();

    // ****************************************************************
    // Main Pane

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label helloLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private VBox transfersVBox;

    @FXML
    private VBox loansVBox;

    @FXML
    private Button loanDetailsButton;

    @FXML
    private Button transferDetailsButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button logButton;

    @FXML
    private Button currenciesButton;

    // ****************************************************************
    // Transfer Details Pane

    @FXML
    private AnchorPane transferPane;

    @FXML
    private Label t_senderID;

    @FXML
    private Label t_receiverID;

    @FXML
    private Label t_amount;

    @FXML
    private Label t_currency;

    @FXML
    private Label t_date;

    @FXML
    private Label t_errorLabel;

    @FXML
    private Button t_declineButton;

    @FXML
    private Button t_acceptButton;

    // ****************************************************************
    // Loan Details Pane

    @FXML
    private AnchorPane loanPane;

    @FXML
    private Label l_userIDLabel;

    @FXML
    private Label l_accountIDLabel;

    @FXML
    private Label l_amountLabel;

    @FXML
    private Label l_installementLabel;

    @FXML
    private Label l_lengthLabel;

    @FXML
    private Label l_dateLabel;

    @FXML
    private Label l_errorLabel;

    @FXML
    private Button l_declineButton;

    @FXML
    private Button l_acceptButton;


    // ****************************************************************
    // Currencies Pane

    @FXML
    private AnchorPane currenciesPane;

    @FXML
    private Label c_errorLabel;

    @FXML
    private TextField c_usdBuy;

    @FXML
    private TextField c_usdSell;

    @FXML
    private TextField c_eurBuy;

    @FXML
    private TextField c_eurSell;

    @FXML
    private TextField c_gbpBuy;

    @FXML
    private TextField c_gbpSell;


    // ****************************************************************
    @FXML
    public void toggleCurrenciesPane(){
        currenciesPane.setVisible(!currenciesPane.isVisible());
        c_errorLabel.setVisible(false);
        toggleButtons();

        if(currenciesPane.isVisible()) refreshExchangeRate();
    }

    @FXML
    public void getCurrenciesFromAPI(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String getUrl = Main.api + "refreshCurrencies?" +
                "tokenStr=" + UserData.getUserData().getToken();
        HttpGet request = new HttpGet(getUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                c_errorLabel.setTextFill(Color.GREEN);
                c_errorLabel.setText("Successfully refreshed currencies!");
                c_errorLabel.setVisible(true);
            } else logout();
        } catch (Exception e) {
            System.out.println("[Get Currencies] " + e.getMessage());
            logout();
        } finally {
            request.releaseConnection();
        }
        refreshExchangeRate();
    }

    @FXML
    public void updateCurrencies(){
        c_errorLabel.setTextFill(Color.RED);
        c_errorLabel.setVisible(false);

        Double usdBuy, usdSell, gbpBuy, gbpSell, eurBuy, eurSell;
        try {
            usdBuy = Double.parseDouble(c_usdBuy.getText());
        } catch (NumberFormatException e){
            c_errorLabel.setText("USD - BUY must be numeric value!");
            c_errorLabel.setVisible(true);
            return;
        }

        try {
            usdSell = Double.parseDouble(c_usdSell.getText());
        } catch (NumberFormatException e){
            c_errorLabel.setText("USD - SELL must be numeric value!");
            c_errorLabel.setVisible(true);
            return;
        }

        try {
            gbpBuy = Double.parseDouble(c_gbpBuy.getText());
        } catch (NumberFormatException e){
            c_errorLabel.setText("GBP - BUY must be numeric value!");
            c_errorLabel.setVisible(true);
            return;
        }

        try {
            gbpSell = Double.parseDouble(c_gbpSell.getText());
        } catch (NumberFormatException e){
            c_errorLabel.setText("GBP - SELL must be numeric value!");
            c_errorLabel.setVisible(true);
            return;
        }

        try {
            eurBuy = Double.parseDouble(c_eurBuy.getText());
        } catch (NumberFormatException e){
            c_errorLabel.setText("EUR - BUY must be numeric value!");
            c_errorLabel.setVisible(true);
            return;
        }

        try {
            eurSell = Double.parseDouble(c_eurSell.getText());
        } catch (NumberFormatException e){
            c_errorLabel.setText("EUR - SELL must be numeric value!");
            c_errorLabel.setVisible(true);
            return;
        }

        if(eurBuy <= 0 || eurSell <= 0 || gbpBuy <= 0 || gbpSell <= 0 || usdBuy <= 0 || usdSell <= 0){
            c_errorLabel.setText("All values must be bigger than 0!");
            c_errorLabel.setVisible(true);
            return;
        }

        boolean ret = updateCurrency("USD", usdBuy, usdSell);
        if(ret) ret = updateCurrency("GBP", gbpBuy, gbpSell);
        if(ret) ret = updateCurrency("EUR", eurBuy, eurSell);
        if(ret) {
            c_errorLabel.setText("Currencies updated successfully!");
            c_errorLabel.setTextFill(Color.GREEN);
            c_errorLabel.setVisible(true);
        }
        else {
            c_errorLabel.setText("Error while updating currencies!");
            c_errorLabel.setVisible(true);
        }
    }

    private boolean updateCurrency(String name, Double buyVal, Double sellVal){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String postUrl = Main.api + "updateCurrency?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&name=" + name +
                "&buyVal=" + buyVal +
                "&sellVal=" + sellVal;
        HttpPost request = new HttpPost(postUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
                if(response.getStatusLine().getStatusCode() == 401)
                    logout();
                System.out.println("[" + response.getStatusLine().getStatusCode() + "] " + EntityUtils.toString(response.getEntity()));
                request.releaseConnection();
                return false;
            }
        } catch (Exception e){
            System.out.println("[Update Currency " + name + "] " + e.getMessage());
            request.releaseConnection();
            logout();
            return false;
        } finally {
            request.releaseConnection();
        }
        return true;
    }

    @FXML
    public void acceptSelectedTransfer(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String postUrl = Main.api + "acceptTransfer?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&pending_transfer_id=" + transfers.get(selectedTransfer).getPendingID();
        HttpPost request = new HttpPost(postUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
                if(response.getStatusLine().getStatusCode() == 403) {
                    t_errorLabel.setText("Not enough balance on this account to send it!");
                    t_errorLabel.setTextFill(Color.RED);
                    t_errorLabel.setVisible(true);
                    request.releaseConnection();
                    return;
                }
                logout();
                request.releaseConnection();
                return;
            }
        } catch (Exception e){
            System.out.println("[Accept Transfer] " + e.getMessage());
            request.releaseConnection();
            logout();
            return;
        } finally {
            request.releaseConnection();
        }

        t_errorLabel.setText("Transfer accepted!");
        t_errorLabel.setTextFill(Color.GREEN);
        t_errorLabel.setVisible(true);
        t_acceptButton.setDisable(true);
        t_declineButton.setDisable(true);
        refreshData();
    }

    @FXML
    public void declineSelectedTransfer(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String deleteUrl = Main.api + "deleteTransfer?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&pending_transfer_id=" + transfers.get(selectedTransfer).getPendingID();
        HttpDelete request = new HttpDelete(deleteUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
                logout();
                request.releaseConnection();
                return;
            }
        } catch (Exception e){
            System.out.println("[Decline Transfer] " + e.getMessage());
            request.releaseConnection();
            logout();
            return;
        } finally {
            request.releaseConnection();
        }

        t_errorLabel.setText("Transfer deleted!");
        t_errorLabel.setTextFill(Color.GREEN);
        t_errorLabel.setVisible(true);
        t_acceptButton.setDisable(true);
        t_declineButton.setDisable(true);
        refreshData();
    }

    private void toggleButtons(){
        logButton.setDisable(!logButton.isDisable());
        refreshButton.setDisable(!refreshButton.isDisable());
        currenciesButton.setDisable(!currenciesButton.isDisable());
        buttonsDisabled = !buttonsDisabled;
    }

    @FXML
    public void checkTransferDetails(){
        t_acceptButton.setDisable(false);
        t_declineButton.setDisable(false);
        df.setRoundingMode(RoundingMode.DOWN);
        transferPane.setVisible(!transferPane.isVisible());
        toggleButtons();

        if(transferPane.isVisible()) {
            Transfer t = transfers.get(selectedTransfer);

            t_errorLabel.setVisible(false);
            t_amount.setText(df.format(t.getAmount()));
            t_date.setText(t.getDate().toString());
            t_receiverID.setText(t.getReceiverAccountID().toString());
            t_senderID.setText(t.getSenderAccountID().toString());

            switch(t.getCurrencyID().toString()){
                case "0":
                    t_currency.setText("PLN");
                    break;
                case "1":
                    t_currency.setText("USD");
                    break;
                case "2":
                    t_currency.setText("GBP");
                    break;
                case "3":
                    t_currency.setText("EUR");
                    break;
                default:
                    logout();
                    break;
            }
        }
    }

    @FXML
    public void checkLoanDetails(){
        df.setRoundingMode(RoundingMode.UP);
        loanPane.setVisible(!loanPane.isVisible());
        toggleButtons();
        if(loanPane.isVisible()){
            Loan l = loans.get(selectedLoan);

            l_acceptButton.setDisable(false);
            l_declineButton.setDisable(false);

            l_errorLabel.setVisible(false);
            l_accountIDLabel.setText(l.getAccountID().toString());
            l_amountLabel.setText(df.format(l.getAmount()));
            l_dateLabel.setText(l.getDate().toString());
            l_lengthLabel.setText(Integer.toString(l.getLength()));
            l_userIDLabel.setText(l.getUserID().toString());
            l_installementLabel.setText(df.format(l.getInstallement()));
        }
    }

    @FXML
    private boolean refreshExchangeRate(){
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
                c_errorLabel.setText("Error with getting currency");
                request.releaseConnection();
                logout();
                return false;
            }
        } catch (Exception e) {
            System.out.println("[Get Currencies] " + e.getMessage());
            c_errorLabel.setText("Unknown Error");
            request.releaseConnection();
            logout();
            return false;
        } finally {
            request.releaseConnection();
        }

        c_usdBuy.setText(Double.toString(obj.getJSONObject("USD").getDouble("buy_price")));
        c_usdSell.setText(Double.toString(obj.getJSONObject("USD").getDouble("sell_price")));

        c_gbpBuy.setText(Double.toString(obj.getJSONObject("GBP").getDouble("buy_price")));
        c_gbpSell.setText(Double.toString(obj.getJSONObject("GBP").getDouble("sell_price")));

        c_eurBuy.setText(Double.toString(obj.getJSONObject("EUR").getDouble("buy_price")));
        c_eurSell.setText(Double.toString(obj.getJSONObject("EUR").getDouble("sell_price")));
        return true;
    }

    @FXML
    public void acceptSelectedLoan(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String postUrl = Main.api + "acceptLoan?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&pending_loan_id=" + loans.get(selectedLoan).getPendingID();
        HttpPost request = new HttpPost(postUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
                if(response.getStatusLine().getStatusCode() == 403) {
                    l_errorLabel.setText("This user already have a loan!");
                    l_errorLabel.setTextFill(Color.RED);
                    l_errorLabel.setVisible(true);
                    request.releaseConnection();
                    return;
                }
                logout();
                request.releaseConnection();
                return;
            }
        } catch (Exception e){
            System.out.println("[Accept Loan] " + e.getMessage());
            request.releaseConnection();
            logout();
            return;
        } finally {
            request.releaseConnection();
        }

        l_errorLabel.setText("Loan accepted!");
        l_errorLabel.setTextFill(Color.GREEN);
        l_errorLabel.setVisible(true);
        l_acceptButton.setDisable(true);
        l_declineButton.setDisable(true);
        refreshData();
    }

    @FXML
    public void declineSelectedLoan(){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String deleteUrl = Main.api + "deletePendingLoan?" +
                "tokenStr=" + UserData.getUserData().getToken() +
                "&pending_loan_id=" + loans.get(selectedLoan).getPendingID();
        HttpDelete request = new HttpDelete(deleteUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            if(response.getStatusLine().getStatusCode() != 200){
                logout();
                request.releaseConnection();
                return;
            }
        } catch (Exception e){
            System.out.println("[Decline Loan] " + e.getMessage());
            request.releaseConnection();
            logout();
            return;
        } finally {
            request.releaseConnection();
        }

        l_errorLabel.setText("Loan deleted!");
        l_errorLabel.setTextFill(Color.GREEN);
        l_errorLabel.setVisible(true);
        l_acceptButton.setDisable(true);
        l_declineButton.setDisable(true);
        refreshData();
    }

    @FXML
    public void refreshData(){

        if(!getDataFromAPI()) return;

        int i = 0;
        transfersVBox.getChildren().clear();
        for(Transfer t : transfers) {
            t.setIndex(i);
            transfersVBox.getChildren().add(new PendingTransferTile(t, this));
            i++;
        }

        i = 0;
        loansVBox.getChildren().clear();
        for(Loan l : loans) {
            l.setIndex(i);
            loansVBox.getChildren().add(new PendingLoanTile(l, this));
            i++;
        }

        transferDetailsButton.setDisable(transfers.isEmpty());
        loanDetailsButton.setDisable(loans.isEmpty());
        selectedLoan = 0;
        selectedTransfer = 0;
    }

    public boolean getDataFromAPI(){
        toggleButtons();
        transfers.clear();
        HttpClient httpClient = HttpClientBuilder.create().build();
        String getUrl = Main.api + "getPendingTransfers?" +
                "tokenStr=" + UserData.getUserData().getToken();
        HttpGet request = new HttpGet(getUrl);
        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            if(response.getStatusLine().getStatusCode() == 200){
                JSONObject obj = new JSONObject(result);

                int amount = obj.getJSONObject("data").getInt("amount");
                for (int i = 1; i <= amount; i++) {
                    Long recID = obj.getJSONObject("pending_transfer_" + i).getLong("receiver_account_id");
                    Long sendID = obj.getJSONObject("pending_transfer_" + i).getLong("sender_account_id");
                    Long pendingID = obj.getJSONObject("pending_transfer_" + i).getLong("pending_transfer_id");
                    Double amountMoney = obj.getJSONObject("pending_transfer_" + i).getDouble("amount");
                    Long curID;
                    switch(obj.getJSONObject("pending_transfer_" + i).getString("currency")){
                        case "PLN":
                            curID = 0L;
                            break;
                        case "USD":
                            curID = 1L;
                            break;
                        case "GBP":
                            curID = 2L;
                            break;
                        case "EUR":
                            curID = 3L;
                            break;
                        default:
                            return false;
                    }
                    LocalDate date = LocalDate.parse(obj.getJSONObject("pending_transfer_" + i).getString("date"));

                    Transfer t = new Transfer("pending_transfer_" + i, sendID, recID, curID, amountMoney, date);
                    t.setPendingID(pendingID);
                    transfers.add(t);
                }
            }
            else {
                logout();
                request.releaseConnection();
                return false;
            }
        } catch (Exception e){
            System.out.println("[Get Pending Transfers] " + e.getMessage());
            request.releaseConnection();
            logout();
            return false;
        } finally {
            request.releaseConnection();
        }

        loans.clear();
        getUrl = Main.api + "getPendingLoans?" +
                "tokenStr=" + UserData.getUserData().getToken();
        request = new HttpGet(getUrl);

        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);

            if(response.getStatusLine().getStatusCode() == 200){
                JSONObject obj = new JSONObject(result);

                int amount = obj.getJSONObject("data").getInt("amount");
                for (int i = 1; i <= amount; i++) {
                    Long accID = obj.getJSONObject("pending_loan_" + i).getLong("account_id");
                    Long userID = obj.getJSONObject("pending_loan_" + i).getLong("user_id");
                    Long pendingID = obj.getJSONObject("pending_loan_" + i).getLong("pending_loan_id");
                    Double amountMoney = obj.getJSONObject("pending_loan_" + i).getDouble("amount");
                    int length = obj.getJSONObject("pending_loan_" + i).getInt("length");
                    Double toPayBackMoney = obj.getJSONObject("pending_loan_" + i).getDouble("to_pay_back");
                    LocalDate date = LocalDate.parse(obj.getJSONObject("pending_loan_" + i).getString("date"));

                    Loan l = new Loan(amountMoney, toPayBackMoney, length, 0, date);
                    l.setAccountID(accID);
                    l.setUserID(userID);
                    l.setPendingID(pendingID);

                    loans.add(l);
                }
            }
            else {
                logout();
                request.releaseConnection();
                return false;
            }
        } catch (Exception e){
            System.out.println("[Get Pending Loans] " + e.getMessage());
            request.releaseConnection();
            logout();
            return false;
        } finally {
            request.releaseConnection();
        }

        toggleButtons();
        return true;
    }

    public void selectTransfer(int index){
        if(index == selectedTransfer)
            return;

        PendingTransferTile tt = (PendingTransferTile) transfersVBox.getChildren().get(selectedTransfer);
        tt.deselect();
        selectedTransfer = index;
    }

    public void selectLoan(int index){
        if(index == selectedLoan)
            return;

        PendingLoanTile tt = (PendingLoanTile) loansVBox.getChildren().get(selectedLoan);
        tt.deselect();
        selectedLoan = index;
    }

    @FXML
    public void logoutButton(){
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

        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        df.setRoundingMode(RoundingMode.DOWN);
        loanPane.setVisible(false);
        transferPane.setVisible(false);
        currenciesPane.setVisible(false);
        emailLabel.setText(UserData.getUserData().getLogin());
        helloLabel.setText("Hello, " + UserData.getUserData().getName() + " " + UserData.getUserData().getSurname());
        refreshData();
    }
}
