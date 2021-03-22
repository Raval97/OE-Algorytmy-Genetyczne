package algorytmyGenetyczne;

import java.util.ArrayList;

public class Populacja {

    double sredniaFunkcjiPrzystsowania;
    ArrayList<Osobnik> osobnicy;
    int wielkoscPopulacji;

    public Populacja(int wielkoscPopulacji, int iloscGenow, int dlugoscChromosomu) {
        this.wielkoscPopulacji = wielkoscPopulacji;
        sredniaFunkcjiPrzystsowania = 0;
        osobnicy = new ArrayList<>();
        osobnicy.ensureCapacity(this.wielkoscPopulacji);
        for (int i = 0; i < this.wielkoscPopulacji; i++)
            osobnicy.add(new Osobnik(iloscGenow, dlugoscChromosomu));
    }

    @Override
    public String toString() {
        final String[] value = {"Populacja: \n"};
        osobnicy.forEach(e -> value[0] += e.toString());
        value[0] += "\n";
        return value[0];
    }
}
