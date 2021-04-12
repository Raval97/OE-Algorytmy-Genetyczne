package algorytmyGenetyczne.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Chromosom {

    private Boolean reprezentacjaBinarna;
    public List<Integer> geny;
    public int dlugosc;
    public ZakresZmiennej zakres;
    public double wartoscRzeczywista;

    public Chromosom(ZakresZmiennej zakres, int dokladnosc) {
        reprezentacjaBinarna = true;
        this.zakres = zakres;
        this.dlugosc = obliczDlugoscChromosomu(zakres.zakresPoczatkowy, zakres.zakresKoncowy, dokladnosc);
        Random random = new Random();
        geny = new ArrayList<>();
        for (int i = 0; i < dlugosc; i++)
            geny.add(Math.abs(random.nextInt() % 2));
    }

    public Chromosom(List<Integer> geny, ZakresZmiennej zakres) {
        reprezentacjaBinarna = true;
        this.dlugosc = geny.size();
        this.zakres = zakres;
        this.geny = geny;
    }

    public Chromosom(ZakresZmiennej zakres) {
        reprezentacjaBinarna = false;
        this.zakres = zakres;
        Random random = new Random();
        wartoscRzeczywista = (random.nextInt() % zakres.zakresKoncowy - zakres.zakresPoczatkowy) + zakres.zakresPoczatkowy;
    }

    public Chromosom(double wartoscRzeczywista, ZakresZmiennej zakres) {
        reprezentacjaBinarna = false;
        this.zakres = zakres;
        this.wartoscRzeczywista = wartoscRzeczywista;
    }

    public double getWartoscRzeczywista() {
        if (reprezentacjaBinarna)
            wartoscRzeczywista = dekodowanieDziesietne();
        return wartoscRzeczywista;
    }

    public Double dekodowanieDziesietne() {
        int decimal = Integer.parseInt(getWartoscToString(), 2);
        return zakres.zakresPoczatkowy + decimal * ((zakres.zakresKoncowy - zakres.zakresPoczatkowy) / (Math.pow(2, dlugosc) - 1));
    }

    public String getWartoscToString() {
        final String[] value = {""};
        geny.forEach(e -> value[0] += e.toString());
        return value[0];
    }

    public static int obliczDlugoscChromosomu(Double A, Double B, int dokladnosc) {
        return (int) Math.ceil((Math.log((B - A) * Math.pow(10, dokladnosc)) / Math.log(2)) + (Math.log(1) / Math.log(2)));
    }

    @Override
    public String toString() {
        final String[] value = {"Chromosom= "};
        value[0] += BigDecimal.valueOf(getWartoscRzeczywista()).setScale(3, RoundingMode.HALF_UP).doubleValue() + " =>";
        geny.forEach(e -> value[0] += e.toString());
        value[0] += "\t\t";
        return value[0];
    }

}


