package sample;

import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.util.ArrayList;
import java.util.List;

public class PopUp {

    public static List<String> display(int iloscPol) {
        Stage popupwindow = new Stage();

        List<String> wyniki = new ArrayList<String>();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Manualne dopisanie rekordu");
        Label label1 = new Label("Pop up window now displayed");
        Button button1 = new Button("Anuluj");
        Button button2 = new Button("Dodaj");
        //tworzenie pól tekstowych na podstawie rozmiaru wektora
        ChoiceBox severity = new ChoiceBox();
        severity.getItems().add("łagodny");
        severity.getItems().add("złośliwy");
        //Dopytać, możliwe, że niepotrzebne
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        TextField[] textFields = new TextField[iloscPol - 1];
        for (int i = 0; i < iloscPol - 1; i++) {
            textFields[i] = new TextField(Integer.toString(i));
            layout.getChildren().add(textFields[i]);
        }
        button2.setOnAction(e -> {
            for (int i = 0; i < iloscPol - 1; i++) {
                wyniki.add(textFields[i].getText());
            }
            wyniki.add((String) (severity.getSelectionModel().getSelectedItem()));
            popupwindow.close();
        });
        layout.getChildren().addAll(label1, button1, button2, severity);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout, 600, 450);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
        return wyniki;
    }

//metoda składająca listę String z wprowadzonych danych
//Dodanie zwracanej listy na listę list "pacjenci"
}