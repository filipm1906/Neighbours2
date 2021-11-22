package sample;

import java.util.ArrayList;
import java.util.List;

public class Klasy {
    List<List<String>> lista;   //lista wektorów dla wyników pozytywz
    List<Integer> indexes = new ArrayList<>();

    public Klasy(List<List<String>> lista) {
        this.lista = lista;
    }
}

