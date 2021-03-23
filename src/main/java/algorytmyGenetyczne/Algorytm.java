package algorytmyGenetyczne;

import java.util.Arrays;
import java.util.Collections;

public class Algorytm {

    String metodaSelekcji ;
    String metodaKrzyzowania ;
    String metodaMutacji ;
    Double poczatekZakresuX1 ;
    Double koniecZakresuX1 ;
    Double poczatekZakresuX2 ;
    Double koniecZakresuX2 ;
    Integer iloscPopulacji ;
    Integer dokladnosc ;
    Integer iloscEpok ;
    Integer iloscNajlepszych ;
    Integer iloscStrategiiElitarnej ;
    Double prawdopodobienstwoKrzyzowania ;
    Double prawdopodobienstwoMutacji ;
    Double prawdopodobienstwoInwersji ;
    Boolean maksymalizacja ;

    public Algorytm(String metodaSelekcji, String metodaKrzyzowania, String metodaMutacji, Double poczatekZakresuX1,
                    Double koniecZakresuX1, Double poczatekZakresuX2, Double koniecZakresuX2, Integer iloscPopulacji,
                    Integer dokladnosc, Integer iloscEpok, Integer iloscNajlepszych, Integer iloscStrategiiElitarnej,
                    Double prawdopodobienstwoKrzyzowania, Double prawdopodobienstwoMutacji,
                    Double prawdopodobienstwoInwersji, Boolean maksymalizacja) {
        this.metodaSelekcji = metodaSelekcji;
        this.metodaKrzyzowania = metodaKrzyzowania;
        this.metodaMutacji = metodaMutacji;
        this.poczatekZakresuX1 = poczatekZakresuX1;
        this.koniecZakresuX1 = koniecZakresuX1;
        this.poczatekZakresuX2 = poczatekZakresuX2;
        this.koniecZakresuX2 = koniecZakresuX2;
        this.iloscPopulacji = iloscPopulacji;
        this.dokladnosc = dokladnosc;
        this.iloscEpok = iloscEpok;
        this.iloscNajlepszych = iloscNajlepszych;
        this.iloscStrategiiElitarnej = iloscStrategiiElitarnej;
        this.prawdopodobienstwoKrzyzowania = prawdopodobienstwoKrzyzowania;
        this.prawdopodobienstwoMutacji = prawdopodobienstwoMutacji;
        this.prawdopodobienstwoInwersji = prawdopodobienstwoInwersji;
        this.maksymalizacja = maksymalizacja;
    }

    // MCCORMICK FUNCTION
    public static double funkcjaPrzystosowania(double x1, double x2){
        double value = Math.sin((x1+x2)) + Math.pow((x1-x2),2) - 1.5*x1 +2.5*x2 + 1;
        return value;
    }

    public Osobnik[] selekcja(String rodzaj, Populacja populacja, int iloscNajlepszych){
        Collections.sort(populacja.osobnicy);

        switch (rodzaj){
            case "Selekcja turniejowa":
                return selekcjaTurniejowa(populacja, iloscNajlepszych);
            case "Kolo ruletki":
                return selekcjaKoloRuletki(populacja, iloscNajlepszych);
            default:
                return selekcjaNajlepszych(populacja, iloscNajlepszych);
        }
    }

    public Osobnik[] selekcjaNajlepszych(Populacja populacja, int iloscNajlepszych){
        int rozmiar = iloscPopulacji * iloscNajlepszych / 100;
        Osobnik[] osobnicyDoReprodukcji = new Osobnik[rozmiar];
        for (int i = 0; i < rozmiar; i++) {
            osobnicyDoReprodukcji[i] = populacja.osobnicy.get(i);
        }
        return osobnicyDoReprodukcji;
    }

    public Osobnik[] selekcjaTurniejowa(Populacja populacja, int iloscNajlepszych){
        return new Osobnik[]{};
    }

    public Osobnik[] selekcjaKoloRuletki(Populacja populacja, int iloscNajlepszych){
        return new Osobnik[]{};
    }


    public void oblicz(){

        int iloscZmiennych = 2;
        int dlugoscChromosomuX1 = Chromosom.obliczDlugoscChromosomu(poczatekZakresuX1, koniecZakresuX1, dokladnosc);
        int dlugoscChromosomuX2 = Chromosom.obliczDlugoscChromosomu(poczatekZakresuX2, koniecZakresuX2, dokladnosc);
        System.out.println(dlugoscChromosomuX1);
        System.out.println(dlugoscChromosomuX2);

        ZakresZmiennej zakresX1 = new ZakresZmiennej(poczatekZakresuX1, koniecZakresuX1);
        ZakresZmiennej zakresX2 = new ZakresZmiennej(poczatekZakresuX2, koniecZakresuX2);
        Populacja populacja = new Populacja(iloscPopulacji, iloscZmiennych, zakresX1, zakresX2, dokladnosc);
        System.out.println(populacja.toString());

        populacja.osobnicy.forEach(x -> x.obliczWartoscFunkcjiPrzystsowania());
        populacja.osobnicy.forEach(x -> System.out.println(x.wartoscFunkcjiPrzystsowania));

        Osobnik[] osobnicyDoreprodukcji = selekcja(metodaSelekcji, populacja, iloscNajlepszych);
        Arrays.stream(osobnicyDoreprodukcji).forEach(x -> System.out.print(x.toString()));
    };

}
