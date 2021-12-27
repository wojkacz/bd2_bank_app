package pl.wojkacz.CustomObjects;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class AccountTile extends AnchorPane {
    String accName;
    Long accID;
    Double balancePLN;
    Double balanceEUR;
    Double balanceUSD;
    Double balanceGBP;

    public AccountTile(String accName, Long accID, Double balancePLN, Double balanceEUR, Double balanceUSD, Double balanceGBP){
        this.setStyle("-fx-background-color: #e3e3e3");
        this.setPrefHeight(82);
        this.setPrefWidth(446);

        Label accountName = new Label();
        accountName.setText(accName);
        accountName.setFont(new Font("System", 14));
        accountName.setStyle("-fx-font-weight: bold");
        accountName.setLayoutX(14);
        accountName.setLayoutY(21);

        Label idLabel = new Label();
        idLabel.setText("ID: " + accID);
        idLabel.setLayoutX(14);
        idLabel.setLayoutY(39);

        Label plnLabel = new Label();
        plnLabel.setText("PLN " + balancePLN);
        plnLabel.setLayoutX(161);
        plnLabel.setLayoutY(5);

        Label usdLabel = new Label();
        usdLabel.setText("USD " + balanceUSD);
        usdLabel.setLayoutX(161);
        usdLabel.setLayoutY(22);

        Label eurLabel = new Label();
        eurLabel.setText("EUR " + balanceEUR);
        eurLabel.setLayoutX(161);
        eurLabel.setLayoutY(39);

        Label gbpLabel = new Label();
        gbpLabel.setText("GBP " + balanceGBP);
        gbpLabel.setLayoutX(161);
        gbpLabel.setLayoutY(57);

        Button manageButton = new Button();
        manageButton.setText("Manage");
        manageButton.setPrefHeight(72);
        manageButton.setPrefWidth(65);
        manageButton.setMnemonicParsing(false);
        manageButton.setLayoutX(331);
        manageButton.setLayoutY(5);

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
}
