package pl.wojkacz.AccountScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pl.wojkacz.Data.Account;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.ResourceBundle;

public class AccountSceneController implements Initializable {

    private static final DecimalFormat df = new DecimalFormat("0.00");

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameLabel.setText(Account.getCurrentAccount().getAccountName());
        idLabel.setText("ID: " + Account.getCurrentAccount().getAccountID());
        balancePLN.setText(df.format(Account.getCurrentAccount().getBalancePLN()));
        balanceEUR.setText(df.format(Account.getCurrentAccount().getBalanceEUR()));
        balanceGBP.setText(df.format(Account.getCurrentAccount().getBalanceGBP()));
        balanceUSD.setText(df.format(Account.getCurrentAccount().getBalanceUSD()));
    }
}
