package algorytmyGenetyczne;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class Chromosom {

    ArrayList<Integer> geny;
    int dlugosc;

    public Chromosom(int dlugosc) {
        this.dlugosc = dlugosc;
        Random random = new Random();
        geny = new ArrayList<>();
        geny.ensureCapacity(dlugosc);
        for (int i = 0; i < dlugosc; i++)
            geny.add(Math.abs(random.nextInt() % 2));
    }

    public Double dekodowanieDziesietne(){
        int decimal = Integer.parseInt(getWartoscToString(), 2);
        return -10 + decimal * ( 20 / (Math.pow(2,25) - 1));
    }

    public String getWartoscToString() {
        final String[] value = {""};
        geny.forEach(e -> value[0] += e.toString());
        return value[0];
    }

    @Override
    public String toString() {
        final String[] value = {"Chromosom= "};
        value[0] += BigDecimal.valueOf(dekodowanieDziesietne()).setScale(3, RoundingMode.HALF_UP).doubleValue()+" =>";
        geny.forEach(e -> value[0] += e.toString());
        value[0] += "\t\t";
        return value[0];
    }
}
