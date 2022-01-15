package pl.wojkacz.CustomObjects;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pl.wojkacz.AccountScene.AccountSceneController;
import pl.wojkacz.Data.Account;
import pl.wojkacz.Data.Transfer;


public class TransferTile extends AnchorPane {
    AccountSceneController accountSceneController;
    private int index;
    public TransferTile(final Transfer transfer, AccountSceneController accountSceneController){
        index = transfer.getIndex();
        this.accountSceneController = accountSceneController;

        this.setStyle("-fx-background-color: #e3e3e3");
        this.setPrefHeight(56);
        this.setPrefWidth(184);

        Label tofromLabel = new Label();
        tofromLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        tofromLabel.setLayoutX(8.0);
        tofromLabel.setLayoutY(6.0);

        Label idLabel = new Label();
        idLabel.setFont(Font.font("System", 14));
        idLabel.setLayoutY(6.0);

        Label amountLabel = new Label();
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        amountLabel.setLayoutX(10.0);
        amountLabel.setLayoutY(26.0);

        String text = "";
        if(transfer.getReceiverAccountID().equals(Account.getCurrentAccount().getAccountID())){
            tofromLabel.setText("From:");
            idLabel.setText(transfer.getSenderAccountID().toString());
            idLabel.setLayoutX(50.0);

            text = "+ ";
            amountLabel.setTextFill(Color.valueOf("#30d730"));
        }
        else {
            tofromLabel.setText("To:");
            idLabel.setText(transfer.getReceiverAccountID().toString());
            idLabel.setLayoutX(32.0);

            text = "- ";
            amountLabel.setTextFill(Color.valueOf("#ff0000"));
        }

        text += transfer.getAmount();
        if(transfer.getCurrencyID().equals(0L))
            text += " PLN";
        else if(transfer.getCurrencyID().equals(1L))
            text += " USD";
        else if(transfer.getCurrencyID().equals(2L))
            text += " GBP";
        else if(transfer.getCurrencyID().equals(3L))
            text += " EUR";

        amountLabel.setText(text);

        this.getChildren().addAll(
                tofromLabel,
                idLabel,
                amountLabel
        );

        this.setOnMouseClicked(e -> {
            select();
            accountSceneController.selectTransfer(index);
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
