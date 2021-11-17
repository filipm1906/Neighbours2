package sample;

import java.util.*;

public class IdealnaProporcja {
    List<Klasy> klasyList = new ArrayList<>();
    List<List<Integer>> uzyte =  new ArrayList<>();
    public List<String> getRandomWektor(List<List<String>> lista, List<Integer> indexes, int i){
        List<String> wektor = new ArrayList<>();
        boolean ind = false;
        boolean uz = false;
        int index = 0;
        Random random = new Random();
        index = random.nextInt(lista.size()); //losowany index
        ind = indexes.contains(index); // czy już wylosowany?

        // uz = this.uzyte.get(i).contains(index); // czy już wylosowany?
        // System.out.println( "Uz :"+uz);
        System.out.println(ind);
        if(ind==false){
            indexes.add(index);
        }
        if(ind==true){
            while (ind){
                index = random.nextInt(lista.size()); //losowany index
                ind = indexes.contains(index); // czy już wylosowany?
                System.out.println(index);
                System.out.println("\nIndex: " +ind);
            }
        }
        wektor = lista.get(index);
        System.out.println(indexes);
        System.out.println("Wektor: "+wektor);
        //System.out.println("\nIndex: " +index);
        return wektor;
    }
    public List<String> getRandomWektorTest(List<List<String>> lista, List<Integer> indexes, int i){
        List<String> wektor = new ArrayList<>();
        boolean ind = false;
        boolean uz = false;
        int index = 0;
        Random random = new Random();
        index = random.nextInt(lista.size()); //losowany index
        ind = indexes.contains(index); // czy już wylosowany?
        System.out.println( "Uz :"+uz);
        System.out.println(ind);
        if(ind==false){
            indexes.add(index);
        }
        if(ind==true){
            while (ind){
                index = random.nextInt(lista.size()); //losowany index
                ind = indexes.contains(index); // czy już wylosowany?
                System.out.println(index);
                System.out.println("\nIndex: " +ind);
            }
        }
        wektor = lista.get(index);
        System.out.println(indexes);
        System.out.println("Wektor: "+wektor);
        //System.out.println("\nIndex: " +index);
        return wektor;
    }
    //ta metoda może być dodana w  clasie controller
    public List<List<String>> methodsIP (double uczacy, double testowy){ //wartości z slider uczący i testowy ilość
        List<List<String>> AllList = new ArrayList<>();
        try{
            List<List<String>> uczacyList = generowanieDane(uczacy);
            //if(uczacyList != null){System.out.println("uczacy wyliczony");}
            for (int u=0; u<klasyList.size(); u++){
                uzyte.add(this.klasyList.get(u).indexes);
                System.out.println("Uzyto : "+uzyte.get(u));
            }
            List<List<String>> testowyList = generowanieDaneTest(testowy);
            //if(testowyList != null){System.out.println("testowy wyliczony");}
            AllList.addAll(uczacyList);
            AllList.addAll(testowyList);

        }catch (Exception e){
            System.out.println("Błąd:" +e.toString());
        }
        return AllList;
    }
    public List<List<String>> generowanieDane(double parametr){ //parametry uczący/ testowy wartość z slider
        List<List<String>> lista = new ArrayList<>();
        int iloscklas = this.klasyList.size();
        //System.out.println(iloscklas);
        //System.out.println("ilość ciągu: "+parametr);
        for(int i=0; i<this.klasyList.size(); i++){
            double prop = Math.floor(parametr/iloscklas);// wylicz jaka proporcja uczący
            //System.out.println("parametr: "+prop);
            int cnt= 0;
            while(cnt != prop){
                List<String> wektor = getRandomWektor(klasyList.get(i).lista, klasyList.get(i).indexes, i);
                lista.add(wektor); //wektor jeden z danej klasy add do listy uczący/ testowy
                //System.out.println(wektor);
                cnt++;
            }

        }
        while(lista.size() < parametr){ //ilość ciagu uczącego lub testowego jest nie parzysta to losuje loswowy klase
            Random random = new Random();
            int index  = random.nextInt(iloscklas); //losowa klasa
            List<String> wektor = getRandomWektor(klasyList.get(index).lista,klasyList.get(index).indexes, index);
            lista.add(wektor); //wektor jeden z danej klasy add do listy uczący/ testowy
            //System.out.println(wektor);
        }
        return lista;
    }
    public List<List<String>> generowanieDaneTest(double parametr){ //parametry uczący/ testowy wartość z slider
        List<List<String>> lista = new ArrayList<>();
        int iloscklas = this.klasyList.size();
        //System.out.println(iloscklas);
        //System.out.println("ilość ciągu: "+parametr);
        for(int i=0; i<this.klasyList.size(); i++){
            double prop = Math.floor(parametr/iloscklas);// wylicz jaka proporcja uczący
            //System.out.println("parametr: "+prop);
            int cnt= 0;
            while(cnt != prop){
                List<String> wektor = getRandomWektorTest(klasyList.get(i).lista, uzyte.get(i), i);
                lista.add(wektor); //wektor jeden z danej klasy add do listy uczący/ testowy
                //System.out.println(wektor);
                cnt++;
            }

        }
        while(lista.size() < parametr){ //ilość ciagu uczącego lub testowego jest nie parzysta to losuje loswowy klase
            Random random = new Random();
            int index  = random.nextInt(iloscklas); //losowa klasa
            List<String> wektor = getRandomWektor(klasyList.get(index).lista, uzyte.get(index), index);
            lista.add(wektor); //wektor jeden z danej klasy add do listy uczący/ testowy
            //System.out.println(wektor);
        }
        return lista;
    }
    public List<Klasy> podzialNaKlasy(List<List<String>> pacjenci){
        List<String> klasy = Controller.slownikKlas;
        for (int i=0; i<klasy.size(); i++){
            int size= 0;
            String last = "";
            List<List<String>> lista  = new ArrayList<List<String>>();
            Iterator<List<String>> it = pacjenci.iterator();
            for (int k = 0; it.hasNext(); k++) {
                if(k == 0){
                    it.next();
                }
                List<String> s = it.next();
                String koniec = s.get(s.size()-1);
                //System.out.println(koniec);
                String klasa = klasy.get(i);
                if(koniec.contains(klasa)) {
                    lista.add(s);
                    //System.out.println(s);
                }
            }
            klasyList.add(new Klasy(lista));
        }
        return klasyList;
    }
}
