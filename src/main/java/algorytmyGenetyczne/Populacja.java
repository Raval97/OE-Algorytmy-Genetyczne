package algorytmyGenetyczne;

import java.util.ArrayList;
import java.util.Random;

public class Populacja {

    double wartoscFunkcjiPrzystsowania;
    ArrayList<Chromosom> chromosomy;
    int wielkoscPopulacji;

    public Populacja(int wielkoscPopulacji, int dlugoscChromosomu) {

        this.wielkoscPopulacji = wielkoscPopulacji;
        wartoscFunkcjiPrzystsowania = 0;
        chromosomy = new ArrayList<>();
        chromosomy.ensureCapacity(this.wielkoscPopulacji);
        for (int i = 0; i < this.wielkoscPopulacji; i++)
            chromosomy.add(new Chromosom(dlugoscChromosomu));

    }

    public double getWartoscFunkcjiPrzystsowania() {
        return wartoscFunkcjiPrzystsowania;
    }

    public void setWartoscFunkcjiPrzystsowania(double wartoscFunkcjiPrzystsowania) {
        this.wartoscFunkcjiPrzystsowania = wartoscFunkcjiPrzystsowania;
    }
    public ArrayList<Chromosom> getChromosomy() {
        return chromosomy;
    }

    public void setChromosomy(ArrayList<Chromosom> chromosomy) {
        this.chromosomy = chromosomy;
    }

    public int getWielkoscPopulacji() {
        return wielkoscPopulacji;
    }

    public void setWielkoscPopulacji(int wielkoscPopulacji) {
        this.wielkoscPopulacji = wielkoscPopulacji;
    }

    @Override
    public String toString() {
        final String[] value = {"Populacja: \n"};
        chromosomy.forEach(e -> value[0] += e.toString());
        value[0] += "\n";
        return value[0];
    }
}
