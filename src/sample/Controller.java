package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
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
    private ChoiceBox CB_parametrP;
    @FXML
    private ChoiceBox CB_parametrK;
    @FXML
    private TextField TF_CiagUczacy;
    @FXML
    private TextField TF_CiagTestowy;
    @FXML
    private TextArea TA_CiagUczacy;
    @FXML
    private TextArea TA_CiagTestowy;
    @FXML
    private Slider sliderCU;
    @FXML
    private Label LciagUczacy;
    @FXML
    private Label LciagTestowy;
    public Sasiedzi sas;
    public double[][] dane;

    private int parametrK;
    private String parametrP;

    private int ciagUczacy;
    private int ciagTestowy;

    private List<List<String>> pacjenci;

    public List<List<String>> wczytajDane(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);
        pacjenci = new ArrayList<>();
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
        //wyznaczanie wartości minimalnej i maksymalnej slidera - wyznaczanie ciągu uczącego
        sliderCU.setMin(1);
        sliderCU.setMax(dane.length);
        //funkcja testująca, sprawdza wczytywanie na podstawie pliku 'breast-cancer-wisconsin'
        Testowanie1.testWczytywaniaDanych(dane.length);
        return pacjenci;
    }
    @FXML
    public void odczytWartoscCUczacy(MouseEvent mouseEvent) {
        ciagUczacy = (int)sliderCU.getValue();
        LciagUczacy.setText(Integer.toString(ciagUczacy));
        ciagTestowy = dane.length - ciagUczacy;
        LciagTestowy.setText(Integer.toString(ciagTestowy));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CB_parametrP.setValue("Manhattan");
        CB_parametrP.getItems().addAll("Manhattan , p=1","Euklides , p=2","Czebyszew , p=3");
        CB_parametrK.setValue(1);
        CB_parametrK.getItems().addAll(1,3,5,7,9);
        TA_CiagUczacy.setEditable(false);
        TA_CiagTestowy.setEditable(false);
    }

    public void selectBtnOk(ActionEvent actionEvent) {
        /* Zwraca wybrane przez użytkownika zmienne (parametrP i palametr k) */
        parametrP = (String) CB_parametrP.getSelectionModel().getSelectedItem();
        parametrK = (int) (CB_parametrK.getSelectionModel().getSelectedItem());

        //System.out.println(ciagUczacy);
        //System.out.println(ciagTestowy);

        TA_CiagUczacy.setText(wyswietlWiersze(1,ciagUczacy));
        TA_CiagTestowy.setText(wyswietlWiersze(ciagUczacy+1,ciagTestowy));

        klasyfikuj();
    }

    public void dodajRekord() {
        System.out.println(pacjenci.size()); //test
        pacjenci.add(PopUp.display(dane[0].length));
        System.out.println(dane[0].length);
        System.out.println(pacjenci.size()); //test
        System.out.println(pacjenci.get(pacjenci.size()-1));  //test
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
                } else if (s.equals("KlasaA")) {
                    dane[i][j] = 2;
                } else if (s.equals("KlasaB")) {
                    dane[i][j] = 3;
                } else if (s.equals("KlasaC")) {
                    dane[i][j] = 4;
                } else {
                    dane[i][j] = Double.parseDouble(s);
                }

            }
        }
    }
        public void klasyfikuj() {
            double odleglosc = 0;
            sas = new Sasiedzi(parametrK);
            int wynik = 0;
            sas.wyczysc();

            for (int i = ciagUczacy; i < dane.length; i++) {
                for (int j = 0; j < ciagUczacy; j++) {
                    if(parametrP.equals("Manhattan")){
                        odleglosc = Metryki.odlegloscManhattan(dane[i], dane[j]);
                    } else if(parametrP.equals("Euklides")){
                        odleglosc = Metryki.odlegloscEuklides(dane[i], dane[j]);
                    } else if(parametrP.equals("Czebyszew")){
                        odleglosc = Metryki.odlegloscCzebyszew(dane[i], dane[j]);
                    }
                    sas.sprawdz(odleglosc, dane[j].length);
                }
                wynik = sas.decyzja();
                System.out.println("Wynik dla osoby numer: " + (i + 1) + "to " + wynik);
                if (wynik == dane[i].length) {
                    //poprawneOdpowiedzi++;
                }
                sas.wyczysc();
            }
        }
        private String wyswietlWiersze(int wierszP, int wierszK){
            String tekst = "";
            for(int i=(wierszP-1);i<wierszK;i++){
                for(int j=0;j<dane[i].length;j++){
                    if(j==dane[i].length-1){
                        if(dane[i][j]==0){
                            tekst +=" łagodny";
                        }else if (dane[i][j]==2) {
                            tekst +=" KlasaA";
                        } else if (dane[i][j]==3) {
                            tekst +=" KlasaB";
                        } else if (dane[i][j]==4) {
                            tekst +=" KlasaC";
                        } else{
                            tekst +=" złośliwy";
                        }
                    }
                    else {
                        tekst += String.format("%3.0f", dane[i][j]);
                    }
                }
                tekst+="\n";
            }
            return tekst;
        }
    //System.out.println("Rozmiar tablicy to: " + dane.length);
    //System.out.println("Jeden wiersz składa się z " + dane[0].length + " wartości");
}
