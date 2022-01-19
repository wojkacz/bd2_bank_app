package pl.wojkacz.CustomObjects;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pl.wojkacz.AdminScene.AdminSceneController;
import pl.wojkacz.Data.Loan;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PendingLoanTile extends AnchorPane {
    private static final DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));
    AdminSceneController adminSceneController;
    private int index;

    public PendingLoanTile(Loan loan, AdminSceneController adminSceneController){
        index = loan.getIndex();
        this.adminSceneController = adminSceneController;

        this.setStyle("-fx-background-color: #e3e3e3");
        this.setPrefHeight(43);
        this.setPrefWidth(215);

        Label whoLabel = new Label();
        whoLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        whoLabel.setText("Account: " + loan.getAccountID());
        whoLabel.setAlignment(Pos.CENTER);
        whoLabel.setLayoutX(2.0);
        whoLabel.setLayoutY(6.0);
        whoLabel.setPrefHeight(17.0);
        whoLabel.setPrefWidth(192.0);

        Label amountLabel = new Label();
        amountLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        amountLabel.setText(df.format(loan.getAmount()) + " PLN");
        amountLabel.setLayoutX(2.0);
        amountLabel.setLayoutY(21.0);
        amountLabel.setPrefHeight(17.0);
        amountLabel.setPrefWidth(192.0);
        amountLabel.setAlignment(Pos.CENTER);

        this.getChildren().addAll(amountLabel, whoLabel);

        this.setOnMouseClicked(e -> {
            select();
            adminSceneController.selectLoan(index);
        });

        if(index == 0) select();
    }

    public void select(){
        if(!adminSceneController.buttonsDisabled)
            this.setStyle("-fx-background-color: #c0c0c0");
    }

    public void deselect(){
        if(!adminSceneController.buttonsDisabled)
            this.setStyle("-fx-background-color: #e3e3e3");
    }
}

