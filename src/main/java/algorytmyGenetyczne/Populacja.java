package algorytmyGenetyczne;

import java.util.ArrayList;

public class Populacja {

    double sredniaFunkcjiPrzystsowania;
    ArrayList<Osobnik> osobnicy;
    int wielkoscPopulacji;

    public Populacja(int wielkoscPopulacji, int iloscZmiennych, ZakresZmiennej zakresX1, ZakresZmiennej zakresX2, int dokladnosc) {
        this.wielkoscPopulacji = wielkoscPopulacji;
        sredniaFunkcjiPrzystsowania = 0;
        osobnicy = new ArrayList<>();
        osobnicy.ensureCapacity(this.wielkoscPopulacji);
        for (int i = 0; i < this.wielkoscPopulacji; i++)
            osobnicy.add(new Osobnik(iloscZmiennych, zakresX1, zakresX2, dokladnosc));
    }

    @Override
    public String toString() {
        final String[] value = {"Populacja: \n"};
        osobnicy.forEach(e -> value[0] += e.toString());
        value[0] += "\n";
        return value[0];
    }
}
