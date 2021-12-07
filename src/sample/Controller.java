package sample;

import javafx.application.Platform;
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
import javafx.scene.layout.StackPane;
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
    @FXML
    private Button wyswietlDane;
    @FXML
    private ProgressBar pasekPostepu;

    @FXML
    private Button proporcja;

    @FXML
    private TextArea TAciagUczacyW;

    @FXML
    private TextArea TAciagTestowyW;

    private int cecha1, cecha2;

    public Sasiedzi sas;
    public PopUpTabel popUpTabel;
    public double[][] dane;
    public double[][] losowe_dane;

    private int parametrK;
    private String parametrP;

    private int ciagUczacy;
    private int ciagTestowy;
    private int iloscNowychR;
    private double postepValue;
    private boolean planesDoneFlag = false;

    public static List<List<String>> dodaneRekordy = new ArrayList<>();
    private String dodaneRekordyWyswietlenie = " ";

    private List<List<String>> pacjenci;
    public static List<String> slownikKlas;
    private List<String> atrybuty = new ArrayList<>();
    private double[][] extrema;
    private double[][] sasiady = new double[parametrK][2];
    public static String resultManual;
    private double dokladnosc = 1;
    public IdealnaProporcja idealProp = new IdealnaProporcja();


    public List<List<String>> wczytajDane(ActionEvent actionEvent) {
        atrybuty.clear();
        pasekPostepu.setProgress(0.0);
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Nie można odnaleźć pliku!");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Nie można wczytać pliku! Sprawdź, " +
                    "czy plik nie jest otwarty w innych aplikacjach");
            alert.showAndWait();
        }
        zamienNaDouble(pacjenci);
        //wyznaczanie wartości minimalnej i maksymalnej slidera - wyznaczanie ciągu uczącego
        sliderCU.setMin(1);
        sliderCU.setMax(dane.length);
        sliderCU.setValue(dane.length);
        ciagUczacy = dane.length;

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
        wyswietlDane.setDisable(false);
        return pacjenci;
    }

    @FXML
    public void odczytWartoscCUczacy(MouseEvent mouseEvent) {

        ciagUczacy = (int) sliderCU.getValue();
//      LciagUczacy.setText(Integer.toString(ciagUczacy));
        WpiszWartoscCU.setText(Integer.toString(ciagUczacy));
//        ciagTestowy = dane.length - ciagUczacy;
//        LciagTestowy.setText(Integer.toString(ciagTestowy));
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
        ciagUczacy = (int) sliderCU.getValue();
        System.out.println("ciąg uczący to");
        System.out.println(ciagUczacy);
        //System.out.println(ciagTestowy);
        idealProp = new IdealnaProporcja();
        List<Klasy> k =  idealProp.podzialNaKlasy(pacjenci);
        //List<List<String>> i =  idealProp.generowanieDane(ciagUczacy);
        //if(i != null){System.out.println("jest");}
        List<List<String>> idealList = idealProp.methodsIP(ciagUczacy, dane.length-ciagUczacy);
        zamienNaDoubleIdealProporcja(idealList);
        TA_CiagUczacy.setText(wyswietlWiersze(1, ciagUczacy,dane));
        TA_CiagTestowy.setText(wyswietlWiersze(ciagUczacy + 1, dane.length,dane));
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

    public void selectWyswietlPlaszczyznyDecyzji(ActionEvent actionEvent) throws InterruptedException {
        parametrP = (String) CB_parametrP.getSelectionModel().getSelectedItem();
        parametrK = (int) (CB_parametrK.getSelectionModel().getSelectedItem());
        scatterChart.getData().clear();
        wyswietlPlaszczyzneDecyzji();
    }

    public void selectWyswietlwalidacjaDziesieciokrotna(ActionEvent actionEvent) {
        wyswietlWalidacje.setText(dziesieciokrotnaWalidacja());
    }

    public void selectWyswietlwalidacjaJednokrotna(ActionEvent actionEvent) {
        String tmpSingle[];
        tmpSingle = jednokrotnaWalidacja();
        TAciagTestowyW.setText(tmpSingle[0]);
        TAciagUczacyW.setText(tmpSingle[1]);
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
        Thread taskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                XYChart.Series[] tablica = new XYChart.Series[slownikKlas.size()];
                for (int k = 0; k < tablica.length; k++) {
                    tablica[k] = new XYChart.Series();
                }
                double odleglosc = 0;
                sas = new Sasiedzi(parametrK, slownikKlas.size());
                int wynik;
                sas.wyczysc();
                int idX = atrybuty.indexOf(wyswietlanieX.getValue());
                int idY = atrybuty.indexOf(wyswietlanieY.getValue());
                cecha1 = idX;
                cecha2 = idY;

                double dokl_poziom;
                double dokl_pion;
                dokladnosc = SliderDokladnosc.getValue();
                dokl_poziom = (extrema[cecha1][0]+1 - extrema[cecha1][1]-1) / dokladnosc;
                dokl_pion = (extrema[cecha2][0]+1 - extrema[cecha2][1]-1) / dokladnosc;
                System.out.println("dokładność poziom : " +dokl_poziom);
                System.out.println("dokładność pion : " +dokl_pion);

                double y = extrema[cecha2][1]-1;
                double x = extrema[cecha1][1]-1;

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
                        y += dokl_pion;
                    }
                    x += dokl_poziom;
                    postepValue = (x-extrema[cecha1][1]-1)/(extrema[cecha1][0]+1-extrema[cecha1][1]-1);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pasekPostepu.setProgress(postepValue);
                        }
                    });
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        for (int k = 0; k < tablica.length; k++) {
                            tablica[k].setName("Płaszczyzna decyzji: " + slownikKlas.get(k));
                            scatterChart.getData().add(tablica[k]);
                        }

                        // ustawienie zakresu osi
                        NumberAxis xAxis = (NumberAxis) scatterChart.getXAxis();
                        xAxis.setAutoRanging(false);
                        xAxis.setLowerBound(extrema[cecha1][1]-1);
                        xAxis.setUpperBound(extrema[cecha1][0]+1);

                        NumberAxis yAxis = (NumberAxis) scatterChart.getYAxis();
                        yAxis.setAutoRanging(false);
                        yAxis.setLowerBound(extrema[cecha2][1]-1);
                        yAxis.setUpperBound(extrema[cecha2][0]+1);

                        //dopasowanie wielkości punktów do dokładności płaszczyzn
                        System.out.println("Wysokość wykresu: " + scatterChart.getHeight());
                        System.out.println("Szerokość wykresu: " + scatterChart.getWidth());
                        int wsp_poziom = (int) (scatterChart.getWidth() / dokladnosc);
                        int wsp_pion = (int) (scatterChart.getHeight() / dokladnosc);
                        System.out.println("Wsp poziom to" +wsp_poziom);
                        System.out.println("Wsp pion" + wsp_pion);
                        //próbna zmiana punktów
                        planesDoneFlag = false;
                        for (XYChart.Series<?, ?> series : scatterChart.getData()) {
                            //for all series, take date, each data has Node (symbol) for representing point
                            for (XYChart.Data<?, ?> data : series.getData()) {
                                // this node is StackPane
                                StackPane stackPane =  (StackPane) data.getNode();
                                stackPane.setPrefWidth(wsp_poziom);
                                stackPane.setPrefHeight(wsp_pion);
                            }
                        }
                        planesDoneFlag = true;
                        wyswietlWykres(1, dane.length);
                    }
                });

            }
        });
        taskThread.start();
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
                    try{
                        dane[i][j] = Double.parseDouble(s);
                    }
                    catch(Exception e) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Błąd");
                        alert.setHeaderText(null);
                        alert.setContentText("Plik zawiera niepoprawne dane!\n Właściwy format pliku to:\n" +
                                "- w pierwszym wierszu nagłówki opisujące zawartość kolumn\n" +
                                "- w pozostałych wierszach wartości liczbowe wyrażające wartość danego parametru\n" +
                                "- w ostatniej kolumnie tekstowa lub liczbowa klasyfikacja danego wektora danych"
                                );
                        alert.showAndWait();
                    }
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
                    odleglosc = Metryki.odlegloscManhattan(losowe_dane[i], losowe_dane[j]);
                } else if(parametrP.equals("Euklides , p=2")){
                    odleglosc = Metryki.odlegloscEuklides(losowe_dane[i], losowe_dane[j]);
                } else if(parametrP.equals("Czebyszew , p=3")){
                    odleglosc = Metryki.odlegloscCzebyszew(losowe_dane[i], losowe_dane[j]);
                }
                sas.sprawdz(odleglosc, losowe_dane[j][losowe_dane[j].length-1]);
            }
            wynik = sas.decyzja();
            iloscKlasyfikacji++;
            if (wynik == losowe_dane[i][losowe_dane[i].length-1]) {
                iloscPoprawnych++;
            }
            sas.wyczysc();
        }

        for (int i = indexKoncowy; i < losowe_dane.length; i++) {
            for (int j = indexPoczatkowy; j < indexKoncowy; j++) {
                if(parametrP.equals("Manhattan , p=1")){
                    odleglosc = Metryki.odlegloscManhattan(losowe_dane[i], losowe_dane[j]);
                } else if(parametrP.equals("Euklides , p=2")){
                    odleglosc = Metryki.odlegloscEuklides(losowe_dane[i], losowe_dane[j]);
                } else if(parametrP.equals("Czebyszew , p=3")){
                    odleglosc = Metryki.odlegloscCzebyszew(losowe_dane[i], losowe_dane[j]);
                }
                sas.sprawdz(odleglosc, losowe_dane[j][losowe_dane[j].length-1]);
            }
            wynik = sas.decyzja();
            iloscKlasyfikacji++;
            if (wynik == losowe_dane[i][losowe_dane[i].length-1]) {
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
                        odleglosc = Metryki.odlegloscManhattan(losowe_dane[i], losowe_dane[j]);
                    } else if (parametrP.equals("Euklides , p=2")) {
                        odleglosc = Metryki.odlegloscEuklides(losowe_dane[i], losowe_dane[j]);
                    } else if (parametrP.equals("Czebyszew , p=3")) {
                        odleglosc = Metryki.odlegloscCzebyszew(losowe_dane[i], losowe_dane[j]);
                    }
                    sas.sprawdz(odleglosc, losowe_dane[j][losowe_dane[j].length - 1]);
                }
            }
            wynik = sas.decyzja();
            iloscKlasyfikacji++;
            if (wynik == losowe_dane[i][losowe_dane[i].length-1]) {
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
        losowanaDziesieciokrotnaWalidacja();
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
            //wyswietlaiie (początek zakresu, koniec zakresu.losowe dane)

            tab = walidacja(poczatekZakresu, koniecZakresu);
            sredniaTestowy += tab[0];
            raport+="=== Podzbiór "+iloscZbiorow+" ===\n";
            System.out.println("Test");
            raport+= wyswietlWiersze(poczatekZakresu +1, koniecZakresu, losowe_dane);
            //System.out.println("sredniaTestowy duże zbiory: "+sredniaTestowy); //test
            sredniaUczacy += tab[1];
            raport+=String.format("%1.4f",tab[0])+"      "+String.format("%1.4f",tab[1])+"\n";
            //System.out.println("sredniaUczący duże zbiory: "+sredniaUczacy); //test
            iteracja++;
            poczatekZakresu+=fragment;
            koniecZakresu+=fragment;

            //Komentarz dla draki
            //System.out.println("przesunięty zakres: "+poczatekZakresu+", "+koniecZakresu); //test
            iloscZbiorow++;
        }
        fragment--; //Overlap culprit
        koniecZakresu--;//fix
        for(int i=10-iloscMniejszychZbiorow; i<10; i++) {
            tab = walidacja(poczatekZakresu, koniecZakresu);
            sredniaTestowy += tab[0];
            raport+="=== Podzbiór "+iloscZbiorow+" ===\n";


            //wyswietlanie (początek zakresu, koniec zakresu.losowe dane)

            raport+= wyswietlWiersze(poczatekZakresu +1, koniecZakresu, losowe_dane);
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
        //Koniec walidacji 10-krotnej
        return raport;
    }

    private String[] jednokrotnaWalidacja() {
        losowanaDziesieciokrotnaWalidacja();
        double tab[];
        String message[] = new String[2];

        tab = walidacja(0,ciagUczacy);
        message[0] = String.format("%1.4f",tab[0]);
        message[1] = String.format("%1.4f",tab[1]);

        System.out.println("ZADZIALALA JEDNOKROTNA WALIDACJA!"); //Debug

        return message;
    }


    private String wyswietlWiersze(int wierszP, int wierszK,double[][] tablica){
        String tekst = "";
        for(int i=(wierszP-1);i<wierszK;i++){
            for(int j=0;j<tablica[i].length;j++){
                if(j==tablica[i].length-1){
                    tekst+=" ";
                    tekst+=slownikKlas.get((int)tablica[i][j]);
                }
                else {
                    tekst += String.format("%3.0f", tablica[i][j]);
                }
            }
            tekst+="\n";
        }
        return tekst;
    }

    public void klasyfikuj(List<String> wektor) {
        int idX = atrybuty.indexOf(wyswietlanieX.getValue());
        int idY = atrybuty.indexOf(wyswietlanieY.getValue());
        cecha1 = idX;
        cecha2 = idY;
        String sasiedziNowyRekord[] = new String[dane.length-ciagUczacy+1];
        double odleglosc = 0;
        int iloscSasiadow=0;
        sas = new Sasiedzi(parametrK,slownikKlas.size());
        sas.wyczysc();
        double[] vector = new double[wektor.size()];
        for(int x=0; x<wektor.size(); x++) {
            try{
                vector[x] = Double.parseDouble(wektor.get(x));
            }
            catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Podane dane zawierają wartości niebędące liczbami");
                alert.showAndWait();
                break;
            }
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
        double x = 0;
        double y = 0;
        try{
             x = vector[cecha1];
             y = vector[cecha2];
        }
        catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText("Podane dane zawierają wartości niebędące liczbami");
            alert.showAndWait();
        }
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

    public void zamienNaDoubleIdealProporcja(List<List<String>> tablica) {
        dane = new double[tablica.size()][tablica.get(0).size()];
        extrema = new double [tablica.get(0).size()][2];
        Iterator<List<String>> it = tablica.iterator();
        //pominięcie wiersza z opisami kolumn
        //it.next();
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

    public void losowanaDziesieciokrotnaWalidacja(){
        if(pacjenci.size()<10){
            System.out.println("Dane zbyt małe do 10-krotnej walidacji");
        }
        else
        {
            losowe_dane = new double[dane.length][dane[0].length];
            ArrayList<double[]> lista_rekordow = new ArrayList<double[]>();
            //dodanie danych do tymczasowej listy
            for(int i = 0; i<dane.length;i++){
                lista_rekordow.add(dane[i]);
            }
            Random los = new Random();
            for(int i = 0; i<dane.length;i++) {
                losowe_dane[i] = lista_rekordow.remove(los.nextInt(lista_rekordow.size()));
            }
            //System.out.println(wyswietlWiersze(1,dane.length,losowe_dane));
        }
    }

    public void wyswietldane(ActionEvent actionEvent) {
        popUpTabel = new PopUpTabel();
        popUpTabel.start(pacjenci);
    }
}
//System.out.println("Rozmiar tablicy to: " + dane.length);
//System.out.println("Jeden wiersz składa się z " + dane[0].length + " wartości");

