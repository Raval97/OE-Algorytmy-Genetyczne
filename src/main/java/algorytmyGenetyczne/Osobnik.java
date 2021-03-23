package algorytmyGenetyczne;

import java.util.ArrayList;

public class Osobnik implements Comparable {

    double wartoscFunkcjiPrzystsowania;
    ArrayList<Chromosom> chromosomy;
    int iloscZmiennych;

    public Osobnik(int iloscZmiennych, ZakresZmiennej zakresZmiennejX1, ZakresZmiennej zakresZmiennejX2, int dokladnosc) {
        this.iloscZmiennych = iloscZmiennych;
        wartoscFunkcjiPrzystsowania = 0;
        chromosomy = new ArrayList<>();
        chromosomy.ensureCapacity(iloscZmiennych);
        chromosomy.add(new Chromosom(zakresZmiennejX1, dokladnosc));
        chromosomy.add(new Chromosom(zakresZmiennejX2, dokladnosc));
    }

    public void obliczWartoscFunkcjiPrzystsowania() {
        wartoscFunkcjiPrzystsowania = Algorytm.funkcjaPrzystosowania(
                chromosomy.get(0).dekodowanieDziesietne(),
                chromosomy.get(1).dekodowanieDziesietne()
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
