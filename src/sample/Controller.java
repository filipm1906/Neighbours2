package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;

public class Controller implements Initializable {
    private ObservableList<String> MetrykaList = FXCollections.observableArrayList("Manhattan", "Euklidesa");
    @FXML
    private Button btnOK;
    @FXML
    private ChoiceBox CB_metryka;
    @FXML
    private ChoiceBox CB_parametrK;
    public Sasiedzi sas;
    public double[][] dane;

    private int parametrK;
    private String metryka;
    private int podzial;

    public List<List<String>> wczytajDane(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);
        List<List<String>> pacjenci = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                pacjenci.add(Arrays.asList(values));
            }
            pacjenci.get(0).set(0, pacjenci.get(0).get(0).substring(1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        zamienNaDouble(pacjenci);
        //funkcja testująca, sprawdza wczytywanie na podstawie pliku 'breast-cancer-wisconsin'
        Testowanie1.testWczytywaniaDanych(dane.length);
        return pacjenci;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CB_metryka.setValue("Manhattan");
        CB_metryka.getItems().addAll("Manhattan","Euklidesa");
        CB_parametrK.setValue(1);
        CB_parametrK.getItems().addAll(1,3,5,7);
    }

    public void selectBtnOk(ActionEvent actionEvent) {
        /* Zwraca wybrane przez użytkownika zmienne (metryka i palametr k) */
        metryka = (String) CB_metryka.getSelectionModel().getSelectedItem();
        parametrK = (int) (CB_parametrK.getSelectionModel().getSelectedItem());

        System.out.println(metryka);
        System.out.println(parametrK);
        klasyfikuj();

    }


    public void zamienNaDouble(List<List<String>> tablica) {
        dane = new double[tablica.size() - 1][tablica.get(0).size()];
        Iterator<List<String>> it = tablica.iterator();
        //pominięcie wiersza z opisami kolumn
        it.next();
        for (int i = 0; it.hasNext(); i++) {
            Iterator it2 = it.next().iterator();
            for (int j = 0; it2.hasNext(); j++) {
                String s = (String) it2.next();
                if (s.equals("lagodny")) {
                    //lagodny -> 0
                    //zlosliwuy -> 1
                    dane[i][j] = 0;
                } else if (s.equals("zlosliwy")) {
                    dane[i][j] = 1;
                } else {
                    dane[i][j] = Double.parseDouble(s);
                }

            }
        }
    }

        public void klasyfikuj(){
            double odleglosc = 0;
            sas = new Sasiedzi(parametrK);
            int wynik = 0;
            sas.wyczysc();

            for (int i = podzial; i < dane.length; i++) {
                for (int j = 0; j < podzial; j++) {
                    odleglosc = Metryki.odlegloscEuklides(dane[i], dane[j]);
                    sas.sprawdz(odleglosc, dane[j][9]);
                }
                wynik = sas.decyzja();
                System.out.println("Wynik dla osoby numer: " + (i + 1) + "to " + wynik);
                if (wynik == dane[i][9]) {
                    //poprawneOdpowiedzi++;
                }
                sas.wyczysc();
            }
        }
        //System.out.println("Rozmiar tablicy to: " + dane.length);
        //System.out.println("Jeden wiersz składa się z " + dane[0].length + " wartości");
}

