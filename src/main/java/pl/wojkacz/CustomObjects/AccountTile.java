package pl.wojkacz.CustomObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import pl.wojkacz.Data.Account;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class AccountTile extends AnchorPane {
    Button manageButton;
    private static final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    public AccountTile(final Account acc){
        df.setRoundingMode(RoundingMode.DOWN);
        this.setStyle("-fx-background-color: #e3e3e3");
        this.setPrefHeight(82);
        this.setPrefWidth(446);

        Label accountName = new Label();
        accountName.setText(acc.getAccountName());
        accountName.setFont(new Font("System", 14));
        accountName.setStyle("-fx-font-weight: bold");
        accountName.setLayoutX(14);
        accountName.setLayoutY(21);

        Label idLabel = new Label();
        idLabel.setText("ID: " + acc.getAccountID());
        idLabel.setLayoutX(14);
        idLabel.setLayoutY(39);

        Label plnLabel = new Label();
        plnLabel.setText("PLN " + df.format(acc.getBalancePLN()));
        plnLabel.setLayoutX(161);
        plnLabel.setLayoutY(5);

        Label usdLabel = new Label();
        usdLabel.setText("USD " + df.format(acc.getBalanceUSD()));
        usdLabel.setLayoutX(161);
        usdLabel.setLayoutY(22);

        Label eurLabel = new Label();
        eurLabel.setText("EUR " + df.format(acc.getBalanceEUR()));
        eurLabel.setLayoutX(161);
        eurLabel.setLayoutY(39);

        Label gbpLabel = new Label();
        gbpLabel.setText("GBP " + df.format(acc.getBalanceGBP()));
        gbpLabel.setLayoutX(161);
        gbpLabel.setLayoutY(56);

        manageButton = new Button();
        manageButton.setText("Manage");
        manageButton.setPrefHeight(72);
        manageButton.setPrefWidth(65);
        manageButton.setMnemonicParsing(false);
        manageButton.setLayoutX(331);
        manageButton.setLayoutY(5);

        manageButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Account.setCurrentAccount(acc);
                Parent root = null;
                try {
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../AccountScene/AccountScene.fxml")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert root != null;
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/Styles.css");

                Stage stage = (Stage) ((Button)event.getTarget()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
        });

        this.getChildren().addAll(
                accountName,
                idLabel,
                plnLabel,
                usdLabel,
                eurLabel,
                gbpLabel,
                manageButton
        );
    }

    public Button getManageButton() {
        return manageButton;
    }
}
