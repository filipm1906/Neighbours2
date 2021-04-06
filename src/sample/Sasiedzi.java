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
        int klasaA = 0 ;
        int klasaB = 0;
        int klasaC = 0;
        for (int i = 0; i < tablicaSasiadow.length; i++) {
            if (tablicaSasiadow[i][1] == 0) {
                lagodne++;
            }else if (tablicaSasiadow[i][1]==2) {
                klasaA++;
            } else if (tablicaSasiadow[i][1]==3) {
                klasaB++;
            } else if (tablicaSasiadow[i][1]==4) {
                klasaC++;
            } else {
                zlosliwe++;
            }
        }
        if (zlosliwe > lagodne) {
            return 1;
        } else if(klasaA>klasaB && klasaA>klasaC){
            return 2;
        } else if(klasaB>klasaC && klasaB>klasaA){
            return 3;
        } else if(klasaC>klasaA && klasaC>klasaB){
            return 4;
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