package sample;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PopUpPunkty {
    public static void start(int iloscwierszy, String parametrP, List<String> atrybuty){
        Stage stage = new Stage();
        double[] x =  new double[iloscwierszy];
        double[] y =  new double[iloscwierszy];
        stage.setTitle("Odległość między punktami");
        Label label = new Label("Punkty");
        label.setFont(new Font("Arial", 16));
        Label labelx = new Label("x: ");
        Label labely = new Label("y: ");
        VBox vBox1 = new VBox();
        vBox1.setSpacing(1);
        VBox vBox2 = new VBox();
        vBox2.setSpacing(1);
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setPadding(new Insets(5,5, 5,5));
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(5,5, 5,5));
        TextField[] textFields = new TextField[iloscwierszy - 1];
        Label[] labels = new Label[iloscwierszy-1];
        TextField[] textFields2 = new TextField[iloscwierszy - 1];
        Label[] labels2 = new Label[iloscwierszy-1];
        for (int i = 0; i < iloscwierszy - 1; i++) {
            textFields[i] = new TextField(Integer.toString(i));
            textFields[i].setPrefWidth(80);
            labels[i] = new Label(atrybuty.get(i));
            textFields2[i] = new TextField(Integer.toString(i));
            textFields2[i].setPrefWidth(80);
            labels2[i] = new Label(atrybuty.get(i));
            vBox1.getChildren().addAll(labels[i]);
            vBox1.getChildren().addAll(textFields[i]);
            vBox2.getChildren().addAll(labels2[i]);
            vBox2.getChildren().addAll(textFields2[i]);
        }

        Button button2 = new Button("Wylicz");
        hBox.getChildren().addAll(labelx,vBox1,labely,vBox2);
        HBox hBoxButton = new HBox();
        hBoxButton.setSpacing(10);
        hBoxButton.setPadding(new Insets(5,5, 5,5));
        vBox.getChildren().addAll(hBox,button2);
        hBoxButton.getChildren().add(button2);
        button2.setOnAction(e -> {
            for (int i = 0; i < iloscwierszy - 1; i++) {
                x[i] = Double.parseDouble(textFields[i].getText());
                y[i] = Double.parseDouble(textFields2[i].getText());
            }
            double value=0;
            if (parametrP.equals("Manhattan , p=1")) {
                value = Metryki.odlegloscManhattan(x, y);
            } else if (parametrP.equals("Euklides , p=2")) {
                value = Metryki.odlegloscEuklides(x, y);
            } else if (parametrP.equals("Czebyszew , p=3")) {
                value = Metryki.odlegloscCzebyszew(x, y);
            }
            Label labelwynik = new Label("Wynik: " +value);
            if(hBoxButton.getChildren().get(hBoxButton.getChildren().size()-1).getClass().getTypeName().contains("Label") == true){
                hBoxButton.getChildren().remove(hBoxButton.getChildren().size()-1);
                hBoxButton.getChildren().add(labelwynik);
            }else{
                hBoxButton.getChildren().add(labelwynik);
            }
        });
        vBox.getChildren().add(hBoxButton);
        Scene scene = new Scene(vBox);
        stage.setMinWidth(600);
        stage.setScene(scene);
        stage.show();
    }
}
