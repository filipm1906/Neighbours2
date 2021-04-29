package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.awt.Button;
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
    @FXML
    private ScatterChart<?,?> scatterChart;

    @FXML
    private Spinner<Integer> wyswietlanieY;
    @FXML
    private Spinner<Integer> wyswietlanieX;
    private int cecha1, cecha2;


    @FXML
    private MenuItem addRec;

    public Sasiedzi sas;
    public double[][] dane;

    private int parametrK;
    private String parametrP;

    private int ciagUczacy;
    private int ciagTestowy;

    private List<List<String>> pacjenci;
    private List<String> slownikKlas;

    public static String resultManual;

    public List<List<String>> wczytajDane(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
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
        //Spinnery do wykresu x i y.
        SpinnerValueFactory<Integer> aby = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,dane[0].length-2,1);
        SpinnerValueFactory<Integer> abx = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,dane[0].length-2,0);
        //przypisanie
        wyswietlanieY.setValueFactory(aby);
        wyswietlanieX.setValueFactory(abx);
        //funkcja testująca, sprawdza wczytywanie na podstawie pliku 'breast-cancer-wisconsin'
        //Testowanie1.testWczytywaniaDanych(dane.length);
        addRec.setDisable(false);
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
        CB_parametrP.setValue("Manhattan , p=1");
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
        TA_CiagTestowy.setText(wyswietlWiersze(ciagUczacy+1,dane.length));
        scatterChart.getData().clear();
        cecha1 = wyswietlanieX.getValue();
        cecha2 = wyswietlanieY.getValue();
        wyswietlPlaszczyzneDecyzji();
        wyswietlWykres(1,ciagUczacy);
        klasyfikuj();
        dziesieciokrotnaWalidacja(); //test
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

        double y;

        double dokladnosc = 0.10;
        double[] daneWykres = new double[3];
        double[] danePlik = new double[3];

        double x = 0;
        while (x <= 10) {
            y = 0;
            while (y <= 10) {
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
    }



    public void dodajRekord() {
        //System.out.println(pacjenci.size()); //test
        klasyfikuj(PopUp.display(dane[0].length));
//        System.out.println(dane[0].length);
//        System.out.println(pacjenci.size()); //test
//        System.out.println(pacjenci.get(pacjenci.size()-1));  //test
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
                if(!it2.hasNext()){
                    dane[i][j] = sprawdzKlase(s);
                }
                else {
                    dane[i][j] = Double.parseDouble(s);
                }
            }
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
                    sas.sprawdz(odleglosc, dane[j][dane[j].length-1]);
                }
                wynik = sas.decyzja();
                tablica[wynik].getData().add(new XYChart.Data(x, y));
                System.out.println("Wynik dla osoby numer: " + (i + 1) + "to " + wynik);
                sas.wyczysc();
            }
            for (int k=0; k<tablica.length; k++){
                tablica[k].setName("Ciąg testowy: "+slownikKlas.get(k));
                scatterChart.getData().add(tablica[k]);
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
            raport+="========== Podzbiór "+iloscZbiorow+" ==========\n";
            raport+="Dokładność klasyfikacji ciągu testowego to: "+tab[0]+"\n";
            //System.out.println("sredniaTestowy duże zbiory: "+sredniaTestowy); //test
            sredniaUczacy += tab[1];
            raport+="Dokładność klasyfikacji ciągu uczącego to: "+tab[1]+"\n";
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
            raport+="========== Podzbiór "+iloscZbiorow+" ==========\n";
            raport+="Dokładność klasyfikacji ciągu testowego to: "+tab[0]+"\n";
            //System.out.println("sredniaTestowy małe zbiory: "+sredniaTestowy); //test
            sredniaUczacy += tab[1];
            raport+="Dokładność klasyfikacji ciągu uczącego to: "+tab[1]+"\n";
            //System.out.println("sredniaUczący małe zbiory: "+sredniaUczacy); //test
            iteracja++;
            poczatekZakresu+=fragment;
            koniecZakresu+=fragment;
            //System.out.println("przesunięty zakres: "+poczatekZakresu+", "+koniecZakresu); //test
            iloscZbiorow++;
        }
        tabWynik[0] = sredniaTestowy/iteracja;
        tabWynik[1] = sredniaUczacy/iteracja;
        raport+="==============================\n";
        raport+="Ogólna dokładność walidacji: "+tabWynik[0]+", "+tabWynik[1];
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
        double odleglosc = 0;
        sas = new Sasiedzi(parametrK,slownikKlas.size());
        sas.wyczysc();
        double[] vector = new double[wektor.size()];
        for(int x=0; x<wektor.size(); x++) {
            vector[x] = Double.parseDouble(wektor.get(x));
        }
            for (int j = 0; j < ciagUczacy; j++) {
                if(parametrP.equals("Manhattan , p=1")){
                    odleglosc = Metryki.odlegloscManhattan(vector, dane[j]);
                } else if(parametrP.equals("Euklides , p=2")){
                    odleglosc = Metryki.odlegloscEuklides(vector, dane[j]);
                } else if(parametrP.equals("Czebyszew , p=3")){
                    odleglosc = Metryki.odlegloscCzebyszew(vector, dane[j]);
                }
                sas.sprawdz(odleglosc, dane[j][dane[j].length-1]);
            }
            resultManual = slownikKlas.get(sas.decyzja());
            sas.wyczysc();
            //System.out.println(resultManual);
            XYChart.Series series = new XYChart.Series();
                    double x = vector[0];
                    double y = vector[3];
                    series.setName("Nowy punkt: " + resultManual);
                    series.getData().add(new XYChart.Data(x, y));
            scatterChart.getData().add(series);
        }
    }
    //System.out.println("Rozmiar tablicy to: " + dane.length);
    //System.out.println("Jeden wiersz składa się z " + dane[0].length + " wartości");

