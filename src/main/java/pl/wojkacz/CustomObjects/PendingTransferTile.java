package pl.wojkacz.CustomObjects;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pl.wojkacz.AdminScene.AdminSceneController;
import pl.wojkacz.Data.Transfer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PendingTransferTile extends AnchorPane {
    private static final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    AdminSceneController adminSceneController;
    private int index;

    public PendingTransferTile(Transfer transfer, AdminSceneController adminSceneController){
        index = transfer.getIndex();
        this.adminSceneController = adminSceneController;

        this.setStyle("-fx-background-color: #e3e3e3");
        this.setPrefHeight(43);
        this.setPrefWidth(215);

        Label fromToLabel = new Label();
        fromToLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        fromToLabel.setText(transfer.getSenderAccountID().toString() + " to " + transfer.getReceiverAccountID());
        fromToLabel.setAlignment(Pos.CENTER);
        fromToLabel.setLayoutX(2.0);
        fromToLabel.setLayoutY(6.0);
        fromToLabel.setPrefHeight(17.0);
        fromToLabel.setPrefWidth(192.0);

        String curText;
        switch (transfer.getCurrencyID().toString()){
            case "0":
                curText = "PLN";
                break;
            case "1":
                curText = "USD";
                break;
            case "2":
                curText = "GBP";
                break;
            case "3":
                curText = "EUR";
                break;
            default:
                curText = "ERROR";
                break;
        }

        Label amountLabel = new Label();
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        amountLabel.setText(df.format(transfer.getAmount()) + " " + curText);
        amountLabel.setLayoutX(2.0);
        amountLabel.setLayoutY(21.0);
        amountLabel.setPrefHeight(17.0);
        amountLabel.setPrefWidth(192.0);
        amountLabel.setAlignment(Pos.CENTER);

        this.getChildren().addAll(amountLabel, fromToLabel);

        this.setOnMouseClicked(e -> {
            select();
            adminSceneController.selectTransfer(index);
        });

        if(index == 0) select();
    }

    public void select(){
        this.setStyle("-fx-background-color: #c0c0c0");
    }

    public void deselect(){
        this.setStyle("-fx-background-color: #e3e3e3");
    }
}
