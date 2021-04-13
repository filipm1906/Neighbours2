package sample;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Testowanie1 {
    @Test
     static void testWczytywaniaDanych(int iloscRekordow) {
        assertEquals(683,iloscRekordow);
    }

    @Test
    public void testMetrykiManhattan() {
        //given
        double[] wektor1 = {1, 1, 1, 1, 1};
        double[] wektor2 = {0, 0, 0, 0, 0};
        //when
        double resultManhattan = Metryki.odlegloscManhattan(wektor1, wektor2);
        //then
        Assertions.assertEquals(4.0, resultManhattan);
    }

    @Test
    public void testMetrykiEuklidesa() {
        //given
        double[] wektor1 = {1, 1, 1, 1, 1};
        double[] wektor2 = {4.50, 0, 0, 0, 0};
        //when
        double resultEuklides = Metryki.odlegloscEuklides(wektor1, wektor2);
        //then
        Assertions.assertEquals(3.905124837953327, resultEuklides);
    }

    @Test
    public void testMetrykiCzebyszewa() {
        //given
        double[] wektor1 = {1, 1, 1, 1, 1};
        double[] wektor2 = {6.75, 0, 0, 0, 0};
        //when
        double resultCzebyszew = Metryki.odlegloscCzebyszew(wektor1, wektor2);
        //then
        Assertions.assertEquals(5.75, resultCzebyszew);
    }
    @Test
    public void testKlasyfikacjiManhattan() {
        //given
        double[] dane1 = {5,1,1,1,2,1,3,1,1,0};
        double[] dane2 = {5,4,4,5,7,10,3,2,1,0};
        double[] dane3 = {3,1,1,1,2,2,3,1,1,0};
        double[] dane4 = {7,4,6,4,6,1,4,3,1,1};
        double[] dane5 = {5,3,3,3,2,3,4,4,1,1};
        double[] dane6 = {6,8,8,1,3,4,3,7,1,0};

        //obliczanie metodą Manhattan
        Assertions.assertEquals(25,Metryki.odlegloscManhattan(dane1,dane6));
        Assertions.assertEquals(28,Metryki.odlegloscManhattan(dane2,dane6));
        Assertions.assertEquals(26,Metryki.odlegloscManhattan(dane3,dane6));
        Assertions.assertEquals(21,Metryki.odlegloscManhattan(dane4,dane6));
        Assertions.assertEquals(19,Metryki.odlegloscManhattan(dane5,dane6));


        Sasiedzi sas = new Sasiedzi(1,2);

        //Manhattan
        sas.sprawdz(25,0);
        sas.sprawdz(28,0);
        sas.sprawdz(26,1);
        sas.sprawdz(21,1);
        sas.sprawdz(19,0);

        Assertions.assertEquals(sas.decyzja(),0);
        sas.decyzja();
    }
    @Test
    public void TestEukides(){
        //Eukildes dane
        double[] dane7 = {5,2,3,1,6,10,5,1,1,1};
        double[] dane8 = {9,5,5,2,2,2,5,1,1,1};
        double[] dane9 = {1,1,1,1,2,2,2,1,1,0};
        double[] dane10 = {10,4,2,1,3,2,4,3,10,1};
        double[] dane11 = {4,1,1,1,2,1,3,1,1,0};
        double[] dane12 = {5,3,4,1,8,10,4,9,1,1};

        //Obliczanie metodą Euklides
        Assertions.assertEquals(8.426149773176359,Metryki.odlegloscEuklides(dane7,dane12));
        Assertions.assertEquals(13.674794331177344,Metryki.odlegloscEuklides(dane8,dane12));
        Assertions.assertEquals(14.035668847618199,Metryki.odlegloscEuklides(dane9,dane12));
        Assertions.assertEquals(15.362291495737216,Metryki.odlegloscEuklides(dane10,dane12));
        Assertions.assertEquals(14.0,Metryki.odlegloscEuklides(dane11,dane12));
        Sasiedzi sas = new Sasiedzi(3,2);

        //Euklides
        sas.sprawdz(8.426149773176359,1);
        sas.sprawdz(13.674794331177344,0);
        sas.sprawdz(14.035668847618199,1);
        sas.sprawdz(15.362291495737216,1);
        sas.sprawdz(14.0,0);
        Assertions.assertEquals(sas.decyzja(),0);
        sas.decyzja();
    }

    @Test
    public void TestCzebyszew(){
        //Czebyszew dane
        double[] dane13 = {10,2,2,1,2,6,1,1,2,1};
        double[] dane14 = {10,6,5,8,5,10,8,6,1,1};
        double[] dane15 = {5,3,2,4,2,1,1,1,1,0};
        double[] dane16 = {10,10,10,10,6,10,8,1,5,1};
        double[] dane17 = {1,1,1,1,2,1,1,1,1,0};
        double[] dane18 = {5,10,10,10,6,10,6,5,2,1};

        //Obliczanie metodą Czebyszew
        Assertions.assertEquals(9,Metryki.odlegloscCzebyszew(dane13,dane18));
        Assertions.assertEquals(5,Metryki.odlegloscCzebyszew(dane14,dane18));
        Assertions.assertEquals(9,Metryki.odlegloscCzebyszew(dane15,dane18));
        Assertions.assertEquals(5,Metryki.odlegloscCzebyszew(dane16,dane18));
        Assertions.assertEquals(9,Metryki.odlegloscCzebyszew(dane17,dane18));

        Sasiedzi sas = new Sasiedzi(3,2);
        //Czebyszew
        sas.sprawdz(9,1);
        sas.sprawdz(5,1);
        sas.sprawdz(9,0);
        sas.sprawdz(5,1);
        sas.sprawdz(9,0);

        Assertions.assertEquals(sas.decyzja(),1);
        sas.decyzja();
    }
}
