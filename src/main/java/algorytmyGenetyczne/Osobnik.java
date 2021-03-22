package algorytmyGenetyczne;

import java.util.ArrayList;

public class Osobnik implements Comparable {

    double wartoscFunkcjiPrzystsowania;
    ArrayList<Chromosom> chromosomy;
    int iloscGenow;

    public Osobnik(int iloscGenow, int dlugoscChromosomu) {
        this.iloscGenow = iloscGenow;
        wartoscFunkcjiPrzystsowania = 0;
        chromosomy = new ArrayList<>();
        chromosomy.ensureCapacity(iloscGenow);
        for (int i = 0; i < iloscGenow; i++)
            chromosomy.add(new Chromosom(dlugoscChromosomu));
    }

    public void obliczWartoscFunkcjiPrzystsowania() {
        wartoscFunkcjiPrzystsowania = Algorytm.funkcjaPrzystosowania(
                chromosomy.get(0).dekodowanieDziesietne(), chromosomy.get(1).dekodowanieDziesietne());
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
