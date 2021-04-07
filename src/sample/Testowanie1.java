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
    public void testKlasyfikacji() {
        //given
        double[] dane1 = {5,1,1,1,2,1,3,1,1,0};
        double[] dane2 = {5,4,4,5,7,10,3,2,1,0};
        double[] dane3 = {3,1,1,1,2,2,3,1,1,0};
        double[] dane4 = {6,8,8,1,3,4,3,7,1,0};
        Assertions.assertEquals(25,Metryki.odlegloscManhattan(dane1,dane4));
        Assertions.assertEquals(28,Metryki.odlegloscManhattan(dane2,dane4));
        Assertions.assertEquals(26,Metryki.odlegloscManhattan(dane3,dane4));
        Sasiedzi sas = new Sasiedzi(1,2);
        sas.sprawdz(25,0);
        sas.sprawdz(28,0);
        sas.sprawdz(26,0);
        Assertions.assertEquals(sas.decyzja(),0);
        sas.decyzja();
    }

}
