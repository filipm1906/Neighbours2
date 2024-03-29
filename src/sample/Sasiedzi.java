package sample;

public class Sasiedzi {
    private double[][] tablicaSasiadow;
    private int klasy[];

    public Sasiedzi(int k,int liczbaKlas) {
        tablicaSasiadow = new double[k][3];
        klasy = new int[liczbaKlas];
        wyczysc();
    }

    public void sprawdz(double odleglosc, double wynik,int pozycja) {
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            if (odleglosc < tablicaSasiadow[i][0]) {
                tablicaSasiadow[i][0] = odleglosc;
                tablicaSasiadow[i][1] = wynik;
                tablicaSasiadow[i][2] = pozycja;
                break;
            }
        }
    }

    public void sprawdz(double odleglosc, double wynik) {
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            if (odleglosc < tablicaSasiadow[i][0]) {
                tablicaSasiadow[i][0] = odleglosc;
                tablicaSasiadow[i][1] = wynik;
                break;
            }
        }
    }

    public int decyzja() {
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            //System.out.println("Sąsiady"+tablicaSasiadow[i][0]+" , "+tablicaSasiadow[i][1]);
                klasy[(int)tablicaSasiadow[i][1]]++;
        }
        int maxKlasa = -1;
        int indexMaxKlasy = -1;
        for (int i = 0; i < klasy.length; i++) {
            //System.out.println("Klasa "+i+"ma "+klasy[i]+" członków");
            if (klasy[i] > maxKlasa) {
                maxKlasa = klasy[i];
                indexMaxKlasy = i;
            }
        }
        return indexMaxKlasy;
    }
    public void wyczysc() {
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            tablicaSasiadow[i][0] = 9e30;
        }
        for (int i = 0; i < klasy.length; i++) {
            klasy[i]=0;
        }
    }

    public String zwrocSasiadow(){
        String sasiady ="";
        for(int i=0;i<tablicaSasiadow.length;i++){
            sasiady+="Wektor nr: " + (int) tablicaSasiadow[i][2] +
                    " odległość " + tablicaSasiadow[i][0] + " "+ Controller.slownikKlas.get((int)tablicaSasiadow[i][1]);
            sasiady+="\n";
        }
        return sasiady;
    }
}