package algorytmyGenetyczne;

import java.util.Collections;

public class Algorytm {

    public Algorytm() {
    }

    // MCCORMICK FUNCTION
    public static double funkcjaPrzystosowania(double x1, double x2){
        double value = Math.sin((x1+x2)) + Math.pow((x1-x2),2) - 1.5*x1 +2.5*x2 + 1;
        return value;
    }

    public int obliczDlugoscChromosomu(int A, int B, int dokladnosc){
        return (int) Math.ceil((Math.log((B-A)*Math.pow(10,dokladnosc)) / Math.log(2)) + (Math.log(1) / Math.log(2)));
    }

    // Selekcja
    public Osobnik[] selekcjaNajlepszych(Populacja populacja){
        Collections.sort(populacja.osobnicy);
        return new Osobnik[]{populacja.osobnicy.get(0), populacja.osobnicy.get(1)};
    }

    public void oblicz(int poczatekZakresu, int koniecZakresu, int dokladnosc, int dlPopulacji, boolean czyMaksymalizacja){

        int iloscZmiennych = 2;
        int dlugoscChromosomu = obliczDlugoscChromosomu(poczatekZakresu, koniecZakresu, dokladnosc);
        System.out.println(dlugoscChromosomu);
        Populacja populacja = new Populacja(dlPopulacji, iloscZmiennych, dlugoscChromosomu);
        System.out.println(populacja.toString());
        populacja.osobnicy.forEach(x -> x.obliczWartoscFunkcjiPrzystsowania());
        populacja.osobnicy.forEach(x -> System.out.println(x.wartoscFunkcjiPrzystsowania));

        Osobnik[] osobnicyDoreprodukcji = selekcjaNajlepszych(populacja);
        System.out.println(osobnicyDoreprodukcji[0].toString() + osobnicyDoreprodukcji[1].toString());
    };

}
