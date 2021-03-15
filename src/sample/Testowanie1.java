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
}
