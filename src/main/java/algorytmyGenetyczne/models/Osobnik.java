package algorytmyGenetyczne.models;

import algorytmyGenetyczne.Algorytm;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Osobnik implements Comparable {

    public double wartoscFunkcjiPrzystsowania;
    public List<Chromosom> chromosomy;
    public int iloscZmiennych;

    public Osobnik(ZakresZmiennej[] zakresyZmiennych, int dokladnosc) {
        this.iloscZmiennych = zakresyZmiennych.length;
        wartoscFunkcjiPrzystsowania = 0;
        chromosomy = new ArrayList<>();
        for (int i = 0; i < iloscZmiennych; i++)
            chromosomy.add(new Chromosom(zakresyZmiennych[i], dokladnosc));
    }

    public Osobnik(ZakresZmiennej[] zakresyZmiennych) {
        this.iloscZmiennych = zakresyZmiennych.length;
        wartoscFunkcjiPrzystsowania = 0;
        chromosomy = new ArrayList<>();
        for (int i = 0; i < iloscZmiennych; i++)
            chromosomy.add(new Chromosom(zakresyZmiennych[i]));
    }

    public Osobnik(Chromosom chromosomyX1, Chromosom chromosomyX2) {
        this.chromosomy = Arrays.asList(chromosomyX1, chromosomyX2);
    }

    public void obliczWartoscFunkcjiPrzystsowania() {
        wartoscFunkcjiPrzystsowania = Algorytm.funkcjaDoOptymalizacji(
                chromosomy.get(0).getWartoscRzeczywista(),
                chromosomy.get(1).getWartoscRzeczywista()
        );
    }

    @Override
    public int compareTo(Object o) {
        double wartoscFunkcji = ((Osobnik) o).wartoscFunkcjiPrzystsowania;
        return Double.compare(this.wartoscFunkcjiPrzystsowania, wartoscFunkcji);
    }

    @Override
    public String toString() {
        final String[] value = {"Osobnik: "};
        chromosomy.forEach(e -> value[0] += e.toString());
        value[0] += "\n";
        return value[0];
    }
}

