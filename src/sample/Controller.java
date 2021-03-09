package sample;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button btnOK;
    public ChoiceBox metryka;
    public ChoiceBox palametrK;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        metryka.getItems().add("Manhattan");
        metryka.getItems().add("Euklidesowa");
    }
}
