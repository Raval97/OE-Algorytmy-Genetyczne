package algorytmyGenetyczne;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chromosom {

    List<Integer> geny;
    int dlugosc;
    ZakresZmiennej zakres;

    public Chromosom(ZakresZmiennej zakres, int dokladnosc) {
        this.zakres = zakres;
        this.dlugosc = obliczDlugoscChromosomu(zakres.zakresPoczatkowy, zakres.zakresKoncowy, dokladnosc);
        Random random = new Random();
        geny = new ArrayList<>();
        for (int i = 0; i < dlugosc; i++)
            geny.add(Math.abs(random.nextInt() % 2));
    }

    public Chromosom(List<Integer> geny, ZakresZmiennej zakres) {
        this.dlugosc = geny.size();
        this.zakres = zakres;
        this.geny = geny;
    }

    public Double dekodowanieDziesietne(){
        int decimal = Integer.parseInt(getWartoscToString(), 2);
        return zakres.zakresPoczatkowy + decimal * ( (zakres.zakresKoncowy - zakres.zakresPoczatkowy) / (Math.pow(2,dlugosc) - 1));
    }

    public String getWartoscToString() {
        final String[] value = {""};
        geny.forEach(e -> value[0] += e.toString());
        return value[0];
    }

    public static int obliczDlugoscChromosomu(Double A, Double B, int dokladnosc){
        return (int) Math.ceil((Math.log((B-A)*Math.pow(10,dokladnosc)) / Math.log(2)) + (Math.log(1) / Math.log(2)));
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


