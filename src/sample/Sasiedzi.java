package sample;

public class Sasiedzi {
    double[][] tablicaSasiadow;

    public Sasiedzi(int k) {
        tablicaSasiadow = new double[k][2];
        wyczysc();
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
        int lagodne = 0;
        int zlosliwe = 0;
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            if (tablicaSasiadow[i][1] == 0) {
                lagodne++;
            } else {
                zlosliwe++;
            }
        }
        if (zlosliwe > lagodne) {
            return 1;
        } else {
            return 0;
        }
    }

    public void wyczysc() {
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            tablicaSasiadow[i][0] = 9e30;
        }
    }


}