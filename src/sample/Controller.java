package sample;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.*;

public class Controller implements Initializable {

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
    private TextArea wyswietlNowyRedord;
    @FXML
    private Slider sliderCU;
    @FXML
    private TextField WpiszWartoscCU;
    @FXML
    private Label LciagUczacy;
    @FXML
    private Label LciagTestowy;
    @FXML
    private ScatterChart<?, ?> scatterChart;
    @FXML
    private TextArea wyswietlWalidacje;
    @FXML
    private Spinner<String> wyswietlanieY;
    @FXML
    private Spinner<String> wyswietlanieX;
    @FXML
    private Button buttonDodajRekord;
    @FXML
    private Slider SliderDokladnosc;

    private int cecha1, cecha2;

    public Sasiedzi sas;
    public double[][] dane;

    private int parametrK;
    private String parametrP;

    private int ciagUczacy;
    private int ciagTestowy;
    private int iloscNowychR;

    public static List<List<String>> dodaneRekordy = new ArrayList<>();
    private String dodaneRekordyWyswietlenie = " ";

    private List<List<String>> pacjenci;
    public static List<String> slownikKlas;
    private List<String> atrybuty = new ArrayList<>();
    private double[][] extrema;
    private double[][] sasiady = new double[parametrK][2];
    public static String resultManual;
    private double dokladnosc = 1;

    public List<List<String>> wczytajDane(ActionEvent actionEvent) {
        atrybuty.clear();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        atrybuty.clear();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(null);
        pacjenci = new ArrayList<>();
        slownikKlas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                pacjenci.add(Arrays.asList(values));
            }
            for (int i = 0; i < pacjenci.get(0).size() - 1; i++) {
                atrybuty.add(pacjenci.get(0).get(i));
            }
            //atrybuty = pacjenci.get(0);
            //atrybuty.remove(atrybuty.size());
            //pacjenci.get(0).set(0, pacjenci.get(0).get(0).substring(1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        zamienNaDouble(pacjenci);
        //wyznaczanie wartości minimalnej i maksymalnej slidera - wyznaczanie ciągu uczącego
        sliderCU.setMin(1);
        sliderCU.setMax(dane.length);

//
        WpiszWartoscCU.textProperty().bindBidirectional(sliderCU.valueProperty(), NumberFormat.getNumberInstance());
//

        //Spinnery do wykresu x i y.
        //SpinnerValueFactory<Integer> aby = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,dane[0].length-2,1);
        //SpinnerValueFactory<Integer> abx = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,dane[0].length-2,0);

        SpinnerValueFactory<String> x = new SpinnerValueFactory.ListSpinnerValueFactory<String>(FXCollections.observableArrayList(atrybuty));
        SpinnerValueFactory<String> y = new SpinnerValueFactory.ListSpinnerValueFactory<String>(FXCollections.observableArrayList(atrybuty));
        //przypisanie
        wyswietlanieY.setValueFactory(x);
        wyswietlanieX.setValueFactory(y);

        //funkcja testująca, sprawdza wczytywanie na podstawie pliku 'breast-cancer-wisconsin'
        //Testowanie1.testWczytywaniaDanych(dane.length);
        buttonDodajRekord.setDisable(false);
        return pacjenci;
    }

    @FXML
    public void odczytWartoscCUczacy(MouseEvent mouseEvent) {

        ciagUczacy = (int) sliderCU.getValue();
//      LciagUczacy.setText(Integer.toString(ciagUczacy));
        WpiszWartoscCU.setText(Integer.toString(ciagUczacy));
        ciagTestowy = dane.length - ciagUczacy;
        LciagTestowy.setText(Integer.toString(ciagTestowy));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CB_parametrP.setValue("Manhattan , p=1");
        CB_parametrP.getItems().addAll("Manhattan , p=1", "Euklides , p=2", "Czebyszew , p=3");
        CB_parametrK.setValue(1);
        CB_parametrK.getItems().addAll(1, 3, 5, 7, 9);
        TA_CiagUczacy.setEditable(false);
        TA_CiagTestowy.setEditable(false);

    }

    public void selectWyswietlCiagUczacyTestowy(ActionEvent actionEvent) {
        /* Zwraca wybrane przez użytkownika zmienne (parametrP i palametr k) */
        parametrP = (String) CB_parametrP.getSelectionModel().getSelectedItem();
        parametrK = (int) (CB_parametrK.getSelectionModel().getSelectedItem());

        //System.out.println(ciagUczacy);
        //System.out.println(ciagTestowy);

        TA_CiagUczacy.setText(wyswietlWiersze(1, ciagUczacy));
        TA_CiagTestowy.setText(wyswietlWiersze(ciagUczacy + 1, dane.length));
    }

    public void selectWyswietlWykresNKK(ActionEvent actionEvent) {
        parametrP = (String) CB_parametrP.getSelectionModel().getSelectedItem();
        parametrK = (int) (CB_parametrK.getSelectionModel().getSelectedItem());
        scatterChart.getData().clear();

        int idX = atrybuty.indexOf(wyswietlanieX.getValue());
        int idY = atrybuty.indexOf(wyswietlanieY.getValue());
        cecha1 = idX;
        cecha2 = idY;
        System.out.println(idX + ", " + idY);

        wyswietlWykres(1,ciagUczacy);
        klasyfikuj();
    }

    public void selectWyswietlPlaszczyznyDecyzji(ActionEvent actionEvent) {
        parametrP = (String) CB_parametrP.getSelectionModel().getSelectedItem();
        parametrK = (int) (CB_parametrK.getSelectionModel().getSelectedItem());
        scatterChart.getData().clear();
        wyswietlPlaszczyzneDecyzji();
        wyswietlWykres(1,ciagUczacy);
}

    public void selectWyswietlwalidacjaDziesieciokrotna(ActionEvent actionEvent) {
        wyswietlWalidacje.setText(dziesieciokrotnaWalidacja());
    }


    private void wyswietlWykres(int poczatek, int koniec){
        XYChart.Series [] tablica = new XYChart.Series[slownikKlas.size()];
        for (int k=0; k<tablica.length; k++){
            tablica[k] = new XYChart.Series();
        }
        for (int i=(poczatek-1);i<koniec;i++) {
            double x = dane[i][cecha1];// ktory akt z pliku
            double y = dane[i][cecha2];
            tablica[(int)dane[i][dane[i].length-1]].getData().add(new XYChart.Data(x, y));
        }
        for (int k=0; k<tablica.length; k++){
            tablica[k].setName("Ciąg uczący: "+slownikKlas.get(k));
            scatterChart.getData().add(tablica[k]);
        }
    }

    //*//
    private void wyswietlPlaszczyzneDecyzji() {
        XYChart.Series[] tablica = new XYChart.Series[slownikKlas.size()];
        for (int k = 0; k < tablica.length; k++) {
            tablica[k] = new XYChart.Series();
        }
        double odleglosc = 0;
        sas = new Sasiedzi(parametrK, slownikKlas.size());
        int wynik;
        sas.wyczysc();

        double y = extrema[cecha2][1]-1;
        double x = extrema[cecha1][1]-1;

        dokladnosc = 1.01 - SliderDokladnosc.getValue();
        double[] daneWykres = new double[3];
        double[] danePlik = new double[3];


        while (x <= extrema[cecha1][0]+1) {
            y = extrema[cecha2][1]-1;
            while (y <= extrema[cecha2][0]+1) {
                daneWykres[0] = x;
                daneWykres[1] = y;
                for (int j = 0; j < ciagUczacy; j++) {
                    danePlik[0] = dane[j][cecha1];
                    danePlik[1] = dane[j][cecha2];
                    if (parametrP.equals("Manhattan , p=1")) {
                        odleglosc = Metryki.odlegloscManhattan(daneWykres, danePlik);
                    } else if (parametrP.equals("Euklides , p=2")) {
                        odleglosc = Metryki.odlegloscEuklides(daneWykres, danePlik);
                    } else if (parametrP.equals("Czebyszew , p=3")) {
                        odleglosc = Metryki.odlegloscCzebyszew(daneWykres, danePlik);
                    }
                    sas.sprawdz(odleglosc, dane[j][dane[j].length - 1]);
                }
                wynik = sas.decyzja();
                tablica[wynik].getData().add(new XYChart.Data(x, y));
                sas.wyczysc();
                y += dokladnosc;
            }
            x += dokladnosc;
        }
        for (int k = 0; k < tablica.length; k++) {
            tablica[k].setName("Płaszczyzna decyzji: " + slownikKlas.get(k));
            scatterChart.getData().add(tablica[k]);
        }
        NumberAxis xAxis = (NumberAxis) scatterChart.getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(extrema[cecha1][1]-1);
        xAxis.setUpperBound(extrema[cecha1][0]+1);

        NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(extrema[cecha2][1]-1);
        yAxis.setUpperBound(extrema[cecha2][0]+1);
    }

    public void dodajRekord() {
        //System.out.println(pacjenci.size()); //test
        klasyfikuj(PopUp.display(dane[0].length, atrybuty));
        aktualizujStringZRekordami();
        wyswietlNowyRedord.setText(dodaneRekordyWyswietlenie);

//        wyswietlNowyRedord.setText("");
        wyswietlNowyRedord.setText(dodaneRekordyWyswietlenie);

//        System.out.println(dane[0].length);
//        System.out.println(pacjenci.size()); //test
//        System.out.println(pacjenci.get(pacjenci.size()-1));  //test
    }

    public String aktualizujStringZRekordami() {
        dodaneRekordyWyswietlenie = "";
        for(int i=0; i<dodaneRekordy.size(); i++) {
            for (int j=0; j<dodaneRekordy.get(0).size(); j++) {
                    dodaneRekordyWyswietlenie+= dodaneRekordy.get(i).get(j);
                    dodaneRekordyWyswietlenie+=" ";
            }
            dodaneRekordyWyswietlenie+="\n";
        }
        System.out.print(dodaneRekordyWyswietlenie);
        return dodaneRekordyWyswietlenie;
    }

    public void zamienNaDouble(List<List<String>> tablica) {
        dane = new double[tablica.size() - 1][tablica.get(0).size()];
        extrema = new double [tablica.get(0).size() - 1][2];
        Iterator<List<String>> it = tablica.iterator();
        //pominięcie wiersza z opisami kolumn
        it.next();
        for (int i = 0; it.hasNext(); i++) {
            Iterator it2 = it.next().iterator();
            for (int j = 0; it2.hasNext(); j++) {
                String s = (String) it2.next();
                if(!it2.hasNext()){
                    dane[i][j] = sprawdzKlase(s);
                }
                else {
                    dane[i][j] = Double.parseDouble(s);
                    if(i==0) // określone jako maksimum
                    {
                        extrema[j][0] = extrema[j][1] = dane[i][j];
                    }
                    else {
                        if(dane[i][j] > extrema[j][0]){
                            extrema[j][0] = dane[i][j];
                        }
                        else if(dane[i][j] < extrema[j][1]) {
                            extrema[j][1] = dane[i][j];
                        }
                    }
                }
            }
        }
        for(int i=0; i<extrema.length; i++){
            for (int j=0; j<extrema[i].length; j++){
                System.out.print(extrema[i][j]);
            }
            System.out.println();
        }
    }


    public int sprawdzKlase(String s){
        Iterator it = slownikKlas.iterator();
        String klasa="";
        while(it.hasNext()){
            klasa = (String) it.next();
            if(klasa.equals(s)){
                return slownikKlas.indexOf(klasa);
            }
        }
        // jeśli nie znaleziono klasy na liście dodawana jest nowa klasa
        slownikKlas.add(s);
        return slownikKlas.indexOf(s);
    }

    public void klasyfikuj() {
        String sasiedziTestowy[] = new String[dane.length-ciagUczacy+1];
        double odleglosc = 0;
        sas = new Sasiedzi(parametrK,slownikKlas.size());
        int wynik = 0;
        sas.wyczysc();
        XYChart.Series [] tablica = new XYChart.Series[slownikKlas.size()];
        for (int k=0; k<tablica.length; k++){
            tablica[k] = new XYChart.Series();
        }
        for (int i = ciagUczacy; i < dane.length; i++) {
            double x = dane[i][cecha1];
            double y = dane[i][cecha2];
            for (int j = 0; j < ciagUczacy; j++) {
                if(parametrP.equals("Manhattan , p=1")){
                    odleglosc = Metryki.odlegloscManhattan(dane[i], dane[j]);
                } else if(parametrP.equals("Euklides , p=2")){
                    odleglosc = Metryki.odlegloscEuklides(dane[i], dane[j]);
                } else if(parametrP.equals("Czebyszew , p=3")){
                    odleglosc = Metryki.odlegloscCzebyszew(dane[i], dane[j]);
                }
                sas.sprawdz(odleglosc, dane[j][dane[j].length-1],j);

            }
            wynik = sas.decyzja();
            XYChart.Data<?,?> punkt = new XYChart.Data(x,y);
            sasiedziTestowy[i-ciagUczacy] = "Wektor nr: " + i+" sąsiedzi: \n"+sas.zwrocSasiadow();
            tablica[wynik].getData().add(punkt);
            System.out.println("nic");
            sas.wyczysc();
        }
        for (int k=0; k<tablica.length; k++){
            tablica[k].setName("Ciąg testowy: "+slownikKlas.get(k));
            scatterChart.getData().add(tablica[k]);
        }
        int licznik=0;
        for (XYChart.Series<?,?> s : tablica) {
            for (final XYChart.Data<?,?> item  : s.getData()) {
                // po chwili trzymania kursora na punkcie
                Tooltip tooltip = new Tooltip();
                tooltip.setText(sasiedziTestowy[licznik++]);
                tooltip.setStyle("-fx-font: normal bold 11 Langdon; "
                        + "-fx-base: #AE3522; "
                        + "-fx-text-fill: #FFFFFF;");
                Tooltip.install(item.getNode(), tooltip);
            }
        }
    }

    private double[] walidacja(int indexPoczatkowy, int indexKoncowy){
        /*
        Funkcja zwraca tablicę parametrów
        tablica[0] -> dokładność klasyfikacji ciągu testowego (jako ułamek od 0 do 1)
        tablica[1] -> ciągu uczącego
         */
        double[] tablica = new double[2];
        double odleglosc = 0;
        sas = new Sasiedzi(parametrK,slownikKlas.size());
        int wynik=0, iloscKlasyfikacji=0, iloscPoprawnych = 0;
        sas.wyczysc();

        // sprawdzenie dokładności klasyfikacji ciągu testowego
        for (int i = 0; i < indexPoczatkowy; i++) {
            for (int j = indexPoczatkowy; j < indexKoncowy; j++) {
                if(parametrP.equals("Manhattan , p=1")){
                    odleglosc = Metryki.odlegloscManhattan(dane[i], dane[j]);
                } else if(parametrP.equals("Euklides , p=2")){
                    odleglosc = Metryki.odlegloscEuklides(dane[i], dane[j]);
                } else if(parametrP.equals("Czebyszew , p=3")){
                    odleglosc = Metryki.odlegloscCzebyszew(dane[i], dane[j]);
                }
                sas.sprawdz(odleglosc, dane[j][dane[j].length-1]);
            }
            wynik = sas.decyzja();
            iloscKlasyfikacji++;
            if (wynik == dane[i][dane[i].length-1]) {
                iloscPoprawnych++;
            }
            sas.wyczysc();
        }

        for (int i = indexKoncowy; i < dane.length; i++) {
            for (int j = indexPoczatkowy; j < indexKoncowy; j++) {
                if(parametrP.equals("Manhattan , p=1")){
                    odleglosc = Metryki.odlegloscManhattan(dane[i], dane[j]);
                } else if(parametrP.equals("Euklides , p=2")){
                    odleglosc = Metryki.odlegloscEuklides(dane[i], dane[j]);
                } else if(parametrP.equals("Czebyszew , p=3")){
                    odleglosc = Metryki.odlegloscCzebyszew(dane[i], dane[j]);
                }
                sas.sprawdz(odleglosc, dane[j][dane[j].length-1]);
            }
            wynik = sas.decyzja();
            iloscKlasyfikacji++;
            if (wynik == dane[i][dane[i].length-1]) {
                iloscPoprawnych++;
            }
            sas.wyczysc();
        }
        //zapisanie do tablicy dokładności klasyfikacji ciągu testowego
        if(iloscKlasyfikacji!=0) {
            tablica[0] = iloscPoprawnych / (double) iloscKlasyfikacji;
        }
        else{
            tablica[0] = 0;
        }

        // sprawdzenie dokładności klasyfikacji ciągu uczącego
        iloscKlasyfikacji = iloscPoprawnych = 0;

        for (int i = indexPoczatkowy; i < indexKoncowy; i++) {
            for (int j = indexPoczatkowy; j < indexKoncowy; j++) {
                //rekord nie może być sam dla siebie sąsiadem
                if(i!=j) {
                    if (parametrP.equals("Manhattan , p=1")) {
                        odleglosc = Metryki.odlegloscManhattan(dane[i], dane[j]);
                    } else if (parametrP.equals("Euklides , p=2")) {
                        odleglosc = Metryki.odlegloscEuklides(dane[i], dane[j]);
                    } else if (parametrP.equals("Czebyszew , p=3")) {
                        odleglosc = Metryki.odlegloscCzebyszew(dane[i], dane[j]);
                    }
                    sas.sprawdz(odleglosc, dane[j][dane[j].length - 1]);
                }
            }
            wynik = sas.decyzja();
            iloscKlasyfikacji++;
            if (wynik == dane[i][dane[i].length-1]) {
                iloscPoprawnych++;
            }
            sas.wyczysc();
        }
        //zapisanie dokładności klasyfikacji dla ciągu uczącego
        if(iloscKlasyfikacji!=0) {
            tablica[1] = (double) iloscPoprawnych / iloscKlasyfikacji;
        }
        else{
            tablica [1] = 0;
        }
        System.out.println("Dokładność klasyfikacji ciągu testowego to: "+tablica[0]);
        System.out.println("Dokładność klasyfikacji ciągu uczącego to: "+tablica[1]);
        return tablica;
    }

    private String dziesieciokrotnaWalidacja() {
        if(dane.length<10) {
            return null;
        }
        double tab[] = new double[2];
        double tabWynik[] = new double[2];
        double sredniaTestowy = 0.0;
        double sredniaUczacy = 0.0;
        String raport = "Raport dla walidacji krzyżowej: \n"; //WiP
        raport += "testowy:    uczący:\n"; //WiP
        int iloscZbiorow=1; //WiP
        int iteracja = 0;
        double fragment = Math.round(dane.length/10.0);
        fragment = fragment == dane.length/10 ? fragment+1 : fragment; //Test zaokrąglenia
        System.out.println("fragment: "+fragment);
        int iloscMniejszychZbiorow = ((int)fragment*10)- dane.length;
        //System.out.println("iloscMniejszychZbiorow: "+iloscMniejszychZbiorow); //test
        int poczatekZakresu = 0;
        int koniecZakresu = (int)fragment;
        for(int i=0; i<10-iloscMniejszychZbiorow; i++) {
            tab = walidacja(poczatekZakresu, koniecZakresu);
            sredniaTestowy += tab[0];
            raport+="=== Podzbiór "+iloscZbiorow+" ===\n";
            //System.out.println("sredniaTestowy duże zbiory: "+sredniaTestowy); //test
            sredniaUczacy += tab[1];
            raport+=String.format("%1.4f",tab[0])+"      "+String.format("%1.4f",tab[1])+"\n";
            //System.out.println("sredniaUczący duże zbiory: "+sredniaUczacy); //test
            iteracja++;
            poczatekZakresu+=fragment;
            koniecZakresu+=fragment;
            //System.out.println("przesunięty zakres: "+poczatekZakresu+", "+koniecZakresu); //test
            iloscZbiorow++;
        }
        fragment--; //Overlap culprit
        koniecZakresu--;//fix
        for(int i=10-iloscMniejszychZbiorow; i<10; i++) {
            tab = walidacja(poczatekZakresu, koniecZakresu);
            sredniaTestowy += tab[0];
            raport+="=== Podzbiór "+iloscZbiorow+" ===\n";
            //System.out.println("sredniaTestowy małe zbiory: "+sredniaTestowy); //test
            sredniaUczacy += tab[1];
            raport+=String.format("%1.4f",tab[0])+"      "+String.format("%1.4f",tab[1])+"\n";
            //System.out.println("sredniaUczący małe zbiory: "+sredniaUczacy); //test
            iteracja++;
            poczatekZakresu+=fragment;
            koniecZakresu+=fragment;
            //System.out.println("przesunięty zakres: "+poczatekZakresu+", "+koniecZakresu); //test
            iloscZbiorow++;
        }
        tabWynik[0] = sredniaTestowy/iteracja;
        tabWynik[1] = sredniaUczacy/iteracja;
        raport+="===================\n";
        raport+="Ogólna dokładność: \n";
        raport+=String.format("%1.4f",tabWynik[0])+"      "+String.format("%1.4f",tabWynik[1]);
        tab = walidacja(0,ciagUczacy);
        raport+="\n \n";
        raport+="Jednokrotna walidacja \n";
        raport+="Ciąg testowy:      Ciąg uczący:\n";
        raport+=String.format("%1.4f",tab[0])+"             "+String.format("%1.4f",tab[1]);
        //System.out.println("tabWynik[0]: "+tabWynik[0]); //test
        //System.out.println("tabWynik[1]: "+tabWynik[1]); //test
        //System.out.println("Iteracje: "+iteracja); //test
        System.out.println(raport);
        return raport;
    }


    private String wyswietlWiersze(int wierszP, int wierszK){
        String tekst = "";
        for(int i=(wierszP-1);i<wierszK;i++){
            for(int j=0;j<dane[i].length;j++){
                if(j==dane[i].length-1){
                    tekst+=" ";
                    tekst+=slownikKlas.get((int)dane[i][j]);
                }
                else {
                    tekst += String.format("%3.0f", dane[i][j]);
                }
            }
            tekst+="\n";
        }
        return tekst;
    }

    public void klasyfikuj(List<String> wektor) {
        String sasiedziNowyRekord[] = new String[dane.length-ciagUczacy+1];
        double odleglosc = 0;
        int iloscSasiadow=0;
        sas = new Sasiedzi(parametrK,slownikKlas.size());
        sas.wyczysc();
        double[] vector = new double[wektor.size()];
        for(int x=0; x<wektor.size(); x++) {
            vector[x] = Double.parseDouble(wektor.get(x));
        }
        XYChart.Series [] tablicaNowyRekord = new XYChart.Series[1];
        for (int k=0; k<tablicaNowyRekord.length; k++){
            tablicaNowyRekord[k] = new XYChart.Series();
        }
        for (int j = 0; j < ciagUczacy; j++) {
            if(parametrP.equals("Manhattan , p=1")){
                odleglosc = Metryki.odlegloscManhattan(vector, dane[j]);
            } else if(parametrP.equals("Euklides , p=2")){
                odleglosc = Metryki.odlegloscEuklides(vector, dane[j]);
            } else if(parametrP.equals("Czebyszew , p=3")){
                odleglosc = Metryki.odlegloscCzebyszew(vector, dane[j]);
            }
            sas.sprawdz(odleglosc, dane[j][dane[j].length-1],j);

        }
        resultManual = slownikKlas.get(sas.decyzja());
        sasiedziNowyRekord[iloscSasiadow] = "Nowy rekord \nWektor nr: " + (iloscNowychR+1)+" sąsiedzi: \n"+sas.zwrocSasiadow();
        //System.out.println(resultManual);
        iloscSasiadow++;
        double x = vector[0];
        double y = vector[3];
        XYChart.Data<?,?> punkt = new XYChart.Data(x,y);
        tablicaNowyRekord[0].getData().add(punkt);
        sas.wyczysc();

        for (int k=0; k<tablicaNowyRekord.length; k++){
            tablicaNowyRekord[k].setName("Nowy punkt nr "+(iloscNowychR+1)+": " + resultManual);
            scatterChart.getData().add(tablicaNowyRekord[k]);
        }
        iloscNowychR++;
        int licznik=0;
        for (XYChart.Series<?,?> s : tablicaNowyRekord) {
            for (final XYChart.Data<?,?> item  : s.getData()) {
                // po chwili trzymania kursora na punkcie
                Tooltip tooltip = new Tooltip();
                tooltip.setText(sasiedziNowyRekord[licznik++]);
                tooltip.setStyle("-fx-font: normal bold 11 Langdon; "
                        + "-fx-base: #AE3522; "
                        + "-fx-text-fill: #FFFFFF;");
                Tooltip.install(item.getNode(), tooltip);
            }
        }
    }
}
//System.out.println("Rozmiar tablicy to: " + dane.length);
//System.out.println("Jeden wiersz składa się z " + dane[0].length + " wartości");

