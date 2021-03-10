package sample;

public class Metryki {

    public static double odlegloscManhattan(double[] zestaw1 ,double[] zestaw2){
        double odleglosc = 0.0;
        for(int i=0;i<zestaw1.length-1;i++){
            odleglosc +=  Math.abs(zestaw1[i]-zestaw2[i]);
        }
        return odleglosc;
    }

    public static double odlegloscEuklides(double[] zestaw1 ,double[] zestaw2){
        double odleglosc = 0.0;
        //ostatnia kolumna nie jest uwzględniana w obliczaniu odległości, gdyż jest to klasyfikacja przypadku
        for(int i=0;i<zestaw1.length-1;i++){
            odleglosc += (zestaw1[i]-zestaw2[i]) * (zestaw1[i]-zestaw2[i]);
        }
        odleglosc = Math.sqrt(odleglosc);
        return odleglosc;
    }

    public static double odlegloscCzebyszew(double[] zestaw1 ,double[] zestaw2){
        double odleglosc = 0.0;
        double tmp = 0.0;
        for(int i=0;i<zestaw1.length-1;i++){
            tmp = Math.abs(zestaw1[i] - zestaw2[i]);
            if(tmp > odleglosc){
                odleglosc = tmp;
            }
        }
        return odleglosc;
    }
}
