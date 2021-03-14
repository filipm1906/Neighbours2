package sample;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class Testowanie1 {
    @Test
     static void testWczytywaniaDanych(int iloscRekordow) {
        assertEquals(683,iloscRekordow);
    }
}
