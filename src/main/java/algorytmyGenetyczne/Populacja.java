package algorytmyGenetyczne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Populacja {

    List<Osobnik> osobnicy;
    int wielkoscPopulacji;

    public Populacja(int wielkoscPopulacji, int iloscZmiennych, ZakresZmiennej zakresX1, ZakresZmiennej zakresX2, int dokladnosc) {
        this.wielkoscPopulacji = wielkoscPopulacji;
        osobnicy = new ArrayList<>();
        for (int i = 0; i < this.wielkoscPopulacji; i++)
            osobnicy.add(new Osobnik(iloscZmiennych, zakresX1, zakresX2, dokladnosc));
    }

    public double obliczSredniaFunkcjePrzystsowania(){
        this.osobnicy.forEach(Osobnik::obliczWartoscFunkcjiPrzystsowania);
        double sum = osobnicy.stream().map(x -> x.wartoscFunkcjiPrzystsowania).reduce(0D, Double::sum);
        return sum/wielkoscPopulacji;
    }

    public Osobnik najlepszyOsobnik(){
        Collections.sort(this.osobnicy);
        return this.osobnicy.get(0);
    }

    @Override
    public String toString() {
        final String[] value = {"Populacja: \n"};
        osobnicy.forEach(e -> value[0] += e.toString());
        value[0] += "\n";
        return value[0];
    }
}
