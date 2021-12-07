package sample;


import com.sun.javafx.scene.layout.region.Margins;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.*;

public class PopUpTabel{
    private TableView tableView;
    private  Stage stage;
    private  boolean pierwszywiersz;
    public PopUpTabel(){
        tableView = new TableView();
        stage = new Stage();
        pierwszywiersz = false;
    }
    public void start(List<List<String>> dane){
        stage.setTitle("Tabela z danymi");
        Label label = new Label("Zbiór danych");
        label.setFont(new Font("Arial", 16));
        //edytowanie tabeli
        tableView.setEditable(false);
        int iloscwierszy = dane.size();
        //System.out.println("Ilosc: "+ilosc);
        List<List<String>> nrlista = new ArrayList<>();
        System.out.println(iloscwierszy);
        for(int n=0; n<iloscwierszy; n++){
            List<String> minList = new ArrayList<>();
            minList.add(String.format("%d",n));
            minList.addAll(dane.get(n));
            nrlista.add(minList);
            //System.out.println(nrlista.get(n));
        }
        ObservableList<List<String>> nrlObs = FXCollections.observableArrayList(nrlista);
        int ilosc = nrlista.get(0).size();
        //kolumny
        TableColumn<List<String>, String>[] columns = new TableColumn[ilosc];

        columns[0] = new TableColumn("Nr");
        //headers
        for (int i=1; i<ilosc; i++) {
            TableColumn<List<String>, String> tc = new TableColumn(nrlObs.get(0).get(i));
            columns[i]= tc;
        }
        //wyświetl dane
        for(int j=0; j<ilosc; j++){
            final int cnt = j;
            columns[j].setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(cnt)));
            columns[j].setSortable(false);
        }
        //columns[0].setSortType(TableColumn.SortType.ASCENDING);
        Iterator<List<String>> it = nrlObs.iterator();
        //pominięcie wiersza z opisami kolumn
        if(!pierwszywiersz) {
            it.next();
            pierwszywiersz = true;
        }
        ObservableList<List<String>> danewejsciowe = FXCollections.observableArrayList();
        for (int k= 0; it.hasNext(); k++) {
            danewejsciowe.add(it.next());
        }
       // tableView.prefHeightProperty().bind(stage.heightProperty());
       // tableView.prefWidthProperty().bind(stage.widthProperty());

        tableView.getColumns().addAll(columns);
        tableView.setItems(danewejsciowe);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10 ,10, 10,10));
        vBox.getChildren().addAll(label,tableView);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }
}