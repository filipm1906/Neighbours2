package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.util.ArrayList;
import java.util.List;

public class PopUp {

    public static String etykieta = "";

    public static List<String> display(int iloscPol, List<String> atrybuty) {
        Stage popupwindow = new Stage();

        List<String> wyniki = new ArrayList<String>();

        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Manualne dopisanie rekordu");
//        Label label1 = new Label("Klasyfikacja półautomatyczna");
        Button button1 = new Button("Anuluj");
        Button button2 = new Button("Dodaj");
        //tworzenie pól tekstowych na podstawie rozmiaru wektora
//        ChoiceBox severity = new ChoiceBox();
//        severity.getItems().add("łagodny");
//        severity.getItems().add("złośliwy");
        //Dopytać, możliwe, że niepotrzebne
        button1.setOnAction(e -> popupwindow.close());
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15, 15, 15, 15));
        TextField[] textFields = new TextField[iloscPol - 1];
        Label[] labels = new Label[iloscPol-1];
        for (int i = 0; i < iloscPol - 1; i++) {
            textFields[i] = new TextField(Integer.toString(i));
            labels[i] = new Label(atrybuty.get(i));
            layout.getChildren().add(labels[i]);
            layout.getChildren().add(textFields[i]);
        }

        button2.setOnAction(e -> {
            for (int i = 0; i < iloscPol - 1; i++) {
                wyniki.add(textFields[i].getText());
            }
            popupwindow.close();
        });
        layout.getChildren().addAll(button1, button2);
        layout.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(layout);
        popupwindow.setMinWidth(600);
        layout.setVgrow(button2, Priority.ALWAYS);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
        Controller.dodaneRekordy.add(wyniki);
        return wyniki;
    }

//metoda składająca listę String z wprowadzonych danych
//Dodanie zwracanej listy na listę list "pacjenci"
}