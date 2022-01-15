package pl.wojkacz.AccountScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.wojkacz.CustomObjects.TransferTile;
import pl.wojkacz.Data.Account;
import pl.wojkacz.Data.Transfer;
import pl.wojkacz.Data.UserData;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public class AccountSceneController implements Initializable {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private int selectedTransfer = 0;

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
    private void toggleExchangePaneVisibility(){
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
    private void toggleNewTransferPaneVisibility(){
        newTransferPane.setVisible(!newTransferPane.isVisible());
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
        refreshAccount();
        seeTransferPane.setVisible(false);
        newTransferPane.setVisible(false);
        exchangePane.setVisible(false);
    }
}
