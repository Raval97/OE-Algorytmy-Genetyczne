package algorytmyGenetyczne;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Populacja {

    List<Osobnik> osobnicy;
    int wielkoscPopulacji;
    double sredniaFunkcjaPrzystosowania;

    public Populacja(int wielkoscPopulacji, ZakresZmiennej[] zakresy, int dokladnosc) {
        this.wielkoscPopulacji = wielkoscPopulacji;
        osobnicy = new ArrayList<>();
        for (int i = 0; i < this.wielkoscPopulacji; i++)
            osobnicy.add(new Osobnik(zakresy, dokladnosc));
    }

    public double obliczSredniaFunkcjePrzystsowania(int dokladnosc){
        this.osobnicy.forEach(Osobnik::obliczWartoscFunkcjiPrzystsowania);
        double suma = osobnicy.stream().map(x -> x.wartoscFunkcjiPrzystsowania).reduce(0D, Double::sum);
        Double srednia = BigDecimal.valueOf(suma/wielkoscPopulacji).setScale(dokladnosc, RoundingMode.HALF_UP).doubleValue();
        sredniaFunkcjaPrzystosowania = srednia;
        return srednia;
    }

    public Osobnik najlepszyOsobnik(boolean czyMaksymalizacja){
        Collections.sort(this.osobnicy);
        if(czyMaksymalizacja)
            Collections.reverse(this.osobnicy);
        return this.osobnicy.get(0);
    }

    public double liczOdchylenie(){
        double suma = 0;
        for (int i = 0; i < osobnicy.size(); i++)
            suma += Math.pow((osobnicy.get(i).wartoscFunkcjiPrzystsowania - sredniaFunkcjaPrzystosowania),2);
        return Math.pow((suma/osobnicy.size()),0.5);
    }

    @Override
    public String toString() {
        final String[] value = {"Populacja: \n"};
        osobnicy.forEach(e -> value[0] += e.toString());
        value[0] += "\n";
        return value[0];
    }
}
