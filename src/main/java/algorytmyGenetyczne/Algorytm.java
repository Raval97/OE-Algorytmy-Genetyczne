package algorytmyGenetyczne;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Algorytm {

    String metodaSelekcji;
    String metodaKrzyzowania;
    String metodaMutacji;
    Double poczatekZakresuX1;
    Double koniecZakresuX1;
    Double poczatekZakresuX2;
    Double koniecZakresuX2;
    Integer wielkoscPopulacji;
    Integer dokladnosc;
    Integer iloscEpok;
    Double procentNajlepszych;
    Integer iloscStrategiiElitarnej;
    Double prawdopodobienstwoKrzyzowania;
    Double prawdopodobienstwoMutacji;
    Double prawdopodobienstwoInwersji;
    Boolean maksymalizacja;

    static Random random = new Random();

    public Algorytm(String metodaSelekcji, String metodaKrzyzowania, String metodaMutacji, Double poczatekZakresuX1,
                    Double koniecZakresuX1, Double poczatekZakresuX2, Double koniecZakresuX2, Integer wielkoscPopulacji,
                    Integer dokladnosc, Integer iloscEpok, Double procentNajlepszych, Integer iloscStrategiiElitarnej,
                    Double prawdopodobienstwoKrzyzowania, Double prawdopodobienstwoMutacji,
                    Double prawdopodobienstwoInwersji, Boolean maksymalizacja) {
        this.metodaSelekcji = metodaSelekcji;
        this.metodaKrzyzowania = metodaKrzyzowania;
        this.metodaMutacji = metodaMutacji;
        this.poczatekZakresuX1 = poczatekZakresuX1;
        this.koniecZakresuX1 = koniecZakresuX1;
        this.poczatekZakresuX2 = poczatekZakresuX2;
        this.koniecZakresuX2 = koniecZakresuX2;
        this.wielkoscPopulacji = wielkoscPopulacji;
        this.dokladnosc = dokladnosc;
        this.iloscEpok = iloscEpok;
        this.procentNajlepszych = procentNajlepszych;
        this.iloscStrategiiElitarnej = iloscStrategiiElitarnej;
        this.prawdopodobienstwoKrzyzowania = prawdopodobienstwoKrzyzowania;
        this.prawdopodobienstwoMutacji = prawdopodobienstwoMutacji;
        this.prawdopodobienstwoInwersji = prawdopodobienstwoInwersji;
        this.maksymalizacja = maksymalizacja;
    }

    // MCCORMICK FUNCTION
    public static double funkcjaDoOptymalizacji(double x1, double x2) {
        double value = Math.sin((x1 + x2)) + Math.pow((x1 - x2), 2) - 1.5 * x1 + 2.5 * x2 + 1;
        return value;
    }

    // #####################################################################################
    // ###############################  SELEKCJA  ##########################################
    // #####################################################################################
    private List<Osobnik> selekcja(String rodzaj, List<Osobnik> osobnicy, Double iloscNajlepszych, boolean maksymalizacja) {
        int rozmiar = (int) (wielkoscPopulacji * iloscNajlepszych);
        switch (rodzaj) {
            case "Selekcja turniejowa":
                return selekcjaTurniejowa(osobnicy, rozmiar, maksymalizacja);
            case "Selekcja - Koło Ruletki":
                return selekcjaKoloRuletki(osobnicy, rozmiar, maksymalizacja);
            default:
                return selekcjaNajlepszych(osobnicy, rozmiar, maksymalizacja);
        }
    }

    private List<Osobnik> selekcjaNajlepszych(List<Osobnik> osobnicy, int rozmiar, boolean maksymalizacja) {
        Collections.sort(osobnicy);
        if (maksymalizacja)
            Collections.reverse(osobnicy);
        List<Osobnik> osobnicyDoReprodukcji = new ArrayList<>();
        for (int i = 0; i < rozmiar; i++)
            osobnicyDoReprodukcji.add(osobnicy.get(i));
        return osobnicyDoReprodukcji;
    }

    private List<Osobnik> selekcjaTurniejowa(List<Osobnik> osobnicy, int rozmiar, boolean maksymalizacja) {
        Collections.shuffle(osobnicy);
        List<Osobnik> osobnicyDoReprodukcji = new ArrayList<>();
        List<List<Osobnik>> listaTurniejow = new ArrayList<>();
        double sredniaDlugoscGrupy = (double) osobnicy.size() / rozmiar;
        double temp = 0;
        while (temp < osobnicy.size() - 1) {
            listaTurniejow.add(osobnicy.subList((int) temp, (int) (temp + sredniaDlugoscGrupy)));
            temp += sredniaDlugoscGrupy;
        }
        listaTurniejow.forEach(turniej -> {
            Collections.sort(turniej);
            if (maksymalizacja)
                Collections.reverse(turniej);
            osobnicyDoReprodukcji.add(turniej.get(0));
        });
        return osobnicyDoReprodukcji;
    }

    private List<Osobnik> selekcjaKoloRuletki(List<Osobnik> osobnicy, int rozmiar, boolean maksymalizacja) {
        List<Osobnik> osobnicyDoReprodukcji = new ArrayList<>();
        double sumaFx = 0;
        double sumaCzesciowa = 0;
        double przeskalowanie = osobnicy.get(0).wartoscFunkcjiPrzystsowania + 1;
        if (maksymalizacja) {
            for (int i = 0; i < wielkoscPopulacji; i++)
                sumaFx += osobnicy.get(i).wartoscFunkcjiPrzystsowania + przeskalowanie;
            while (osobnicyDoReprodukcji.size() < rozmiar) {
                int rand = random.nextInt((int) sumaFx);
                for (int i = wielkoscPopulacji - 1; i >= 0; i--) {
                    sumaCzesciowa += (osobnicy.get(i).wartoscFunkcjiPrzystsowania + przeskalowanie);
                    if (sumaCzesciowa >= rand && !osobnicyDoReprodukcji.contains(osobnicy.get(i))) {
                        osobnicyDoReprodukcji.add(osobnicy.get(i));
                        sumaCzesciowa = 0;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < wielkoscPopulacji; i++)
                sumaFx += 1 / (osobnicy.get(i).wartoscFunkcjiPrzystsowania + przeskalowanie);
            while (osobnicyDoReprodukcji.size() < rozmiar) {
                double rand = random.nextDouble();
                for (int i = 0; i < wielkoscPopulacji; i++) {
                    sumaCzesciowa += (1 / (osobnicy.get(i).wartoscFunkcjiPrzystsowania + przeskalowanie)) / sumaFx;
                    if (sumaCzesciowa >= rand && !osobnicyDoReprodukcji.contains(osobnicy.get(i))) {
                        osobnicyDoReprodukcji.add(osobnicy.get(i));
                        sumaCzesciowa = 0;
                        break;
                    }
                }
            }
        }
        return osobnicyDoReprodukcji;
    }

    // #####################################################################################
    // ###############################  KRZYZOWANIE  #######################################
    // #####################################################################################
    private List<Osobnik> krzyzowanie(String rodzaj, List<Osobnik> osobnicyDoGeneracji, Double prawdopodobienstwo, int dlugosc) {
        List<Osobnik> nowaGeneracja = new ArrayList<>();
        while (nowaGeneracja.size() < dlugosc) {
            int randInt1 = random.nextInt(osobnicyDoGeneracji.size());
            int randInt2 = randInt1;
            while (randInt1 == randInt2)
                randInt2 = random.nextInt(osobnicyDoGeneracji.size());
            Osobnik rodzic1 = osobnicyDoGeneracji.get(randInt1);
            Osobnik rodzic2 = osobnicyDoGeneracji.get(randInt2);
            if (prawdopodobienstwo >= random.nextDouble()) {
                List<List<Integer>> x1;
                List<List<Integer>> x2;
                switch (rodzaj) {
                    case "Krzyżowanie Jednopunkowe":
                        x1 = krzyzowanieJednopunktowe(rodzic1.chromosomy.get(0), rodzic2.chromosomy.get(0));
                        x2 = krzyzowanieJednopunktowe(rodzic1.chromosomy.get(1), rodzic2.chromosomy.get(1));
                        break;
                    case "Krzyzowanie Dwupunktowe":
                        x1 = krzyzowanieDwupunktowe(rodzic1.chromosomy.get(0), rodzic2.chromosomy.get(0));
                        x2 = krzyzowanieDwupunktowe(rodzic1.chromosomy.get(1), rodzic2.chromosomy.get(1));
                        break;
                    case "Krzyzowanie Trzypunktowe":
                        x1 = krzyzowanieTrzypunktowe(rodzic1.chromosomy.get(0), rodzic2.chromosomy.get(0));
                        x2 = krzyzowanieTrzypunktowe(rodzic1.chromosomy.get(1), rodzic2.chromosomy.get(1));
                        break;
                    default:
                        x1 = krzyzowanieJednorodne(rodzic1.chromosomy.get(0), rodzic2.chromosomy.get(0));
                        x2 = krzyzowanieJednorodne(rodzic1.chromosomy.get(1), rodzic2.chromosomy.get(1));
                        break;
                }
                nowaGeneracja.addAll(Arrays.asList(
                        new Osobnik(
                                new Chromosom(x1.get(0), rodzic1.chromosomy.get(0).zakres),
                                new Chromosom(x2.get(0), rodzic1.chromosomy.get(1).zakres)),
                        new Osobnik(
                                new Chromosom(x1.get(1), rodzic1.chromosomy.get(0).zakres),
                                new Chromosom(x2.get(1), rodzic1.chromosomy.get(1).zakres))
                ));
            }
        }
        return nowaGeneracja;
    }

    private List<List<Integer>> krzyzowanieJednopunktowe(Chromosom rodzic1, Chromosom rodzic2) {

        int dlugoscChromosomu = rodzic1.dlugosc;
        List<List<Integer>> dzieci = new ArrayList<>();

        Double rand = ((double) (random.nextInt(80) + 10)) / 100;
        int locus1 = (int) ((double) dlugoscChromosomu * rand);
        locus1 += locus1 == 0 ? 1 : 0;

        List<Integer> dziecko1_left = rodzic1.geny.subList(0, locus1 + 1);
        List<Integer> dziecko1_right = rodzic2.geny.subList(locus1 + 1, dlugoscChromosomu);
        List<Integer> dziecko1 = new ArrayList<>();
        dziecko1.addAll(dziecko1_left);
        dziecko1.addAll(dziecko1_right);

        List<Integer> dziecko2_left = rodzic2.geny.subList(0, locus1 + 1);
        List<Integer> dziecko2_right = rodzic1.geny.subList(locus1 + 1, dlugoscChromosomu);
        List<Integer> dziecko2 = new ArrayList<>();
        dziecko2.addAll(dziecko2_left);
        dziecko2.addAll(dziecko2_right);

        dzieci.addAll(Arrays.asList(dziecko1, dziecko2));
        return dzieci;
    }

    private List<List<Integer>> krzyzowanieDwupunktowe(Chromosom rodzic1, Chromosom rodzic2) {

        int dlugoscChromosomu = rodzic1.dlugosc;
        List<List<Integer>> dzieci = new ArrayList<>();

        Double rand1 = ((double) (random.nextInt(30) + 10)) / 100;
        Double rand2 = ((double) (random.nextInt(30) + 40)) / 100;
        int locus1 = (int) ((double) dlugoscChromosomu * rand1);
        int locus2 = (int) ((double) dlugoscChromosomu * rand2);
        locus1 += locus1 == 0 ? 1 : 0;
        locus2 += locus2 == 0 ? 1 : 0;

        List<Integer> dziecko1_1 = rodzic1.geny.subList(0, locus1 + 1);
        List<Integer> dziecko1_2 = rodzic2.geny.subList(locus1 + 1, locus2 + 1);
        List<Integer> dziecko1_3 = rodzic1.geny.subList(locus2 + 1, dlugoscChromosomu);
        List<Integer> dziecko1 = new ArrayList<>();
        dziecko1.addAll(dziecko1_1);
        dziecko1.addAll(dziecko1_2);
        dziecko1.addAll(dziecko1_3);

        List<Integer> dziecko2_1 = rodzic2.geny.subList(0, locus1 + 1);
        List<Integer> dziecko2_2 = rodzic1.geny.subList(locus1 + 1, locus2 + 1);
        List<Integer> dziecko2_3 = rodzic2.geny.subList(locus2 + 1, dlugoscChromosomu);
        List<Integer> dziecko2 = new ArrayList<>();
        dziecko2.addAll(dziecko2_1);
        dziecko2.addAll(dziecko2_2);
        dziecko2.addAll(dziecko2_3);

        dzieci.addAll(Arrays.asList(dziecko1, dziecko2));
        return dzieci;
    }

    private List<List<Integer>> krzyzowanieTrzypunktowe(Chromosom rodzic1, Chromosom rodzic2) {

        int dlugoscChromosomu = rodzic1.dlugosc;
        List<List<Integer>> dzieci = new ArrayList<>();

        Double rand1 = ((double) (random.nextInt(20) + 5)) / 100;
        Double rand2 = ((double) (random.nextInt(20) + 25)) / 100;
        Double rand3 = ((double) (random.nextInt(20) + 55)) / 100;
        int locus1 = (int) ((double) dlugoscChromosomu * rand1);
        int locus2 = (int) ((double) dlugoscChromosomu * rand2);
        int locus3 = (int) ((double) dlugoscChromosomu * rand3);
        locus1 += locus1 == 0 ? 1 : 0;
        locus2 += locus2 == 0 ? 1 : 0;
        locus3 += locus3 == 0 ? 1 : 0;

        List<Integer> dziecko1_1 = rodzic1.geny.subList(0, locus1 + 1);
        List<Integer> dziecko1_2 = rodzic2.geny.subList(locus1 + 1, locus2 + 1);
        List<Integer> dziecko1_3 = rodzic1.geny.subList(locus2 + 1, locus3 + 1);
        List<Integer> dziecko1_4 = rodzic2.geny.subList(locus3 + 1, dlugoscChromosomu);
        List<Integer> dziecko1 = new ArrayList<>();
        dziecko1.addAll(dziecko1_1);
        dziecko1.addAll(dziecko1_2);
        dziecko1.addAll(dziecko1_3);
        dziecko1.addAll(dziecko1_4);

        List<Integer> dziecko2_1 = rodzic2.geny.subList(0, locus1 + 1);
        List<Integer> dziecko2_2 = rodzic1.geny.subList(locus1 + 1, locus2 + 1);
        List<Integer> dziecko2_3 = rodzic2.geny.subList(locus2 + 1, locus3 + 1);
        List<Integer> dziecko2_4 = rodzic1.geny.subList(locus3 + 1, dlugoscChromosomu);
        List<Integer> dziecko2 = new ArrayList<>();
        dziecko2.addAll(dziecko2_1);
        dziecko2.addAll(dziecko2_2);
        dziecko2.addAll(dziecko2_3);
        dziecko2.addAll(dziecko2_4);

        dzieci.addAll(Arrays.asList(dziecko1, dziecko2));
        return dzieci;
    }

    private List<List<Integer>> krzyzowanieJednorodne(Chromosom rodzic1, Chromosom rodzic2) {

        int dlugoscChromosomu = rodzic1.dlugosc;
        List<List<Integer>> dzieci = new ArrayList<>();
        List<Integer> dziecko1 = new ArrayList<>();
        List<Integer> dziecko2 = new ArrayList<>();
        List<Integer> wzorzec = new ArrayList<>();
        for (int i = 0; i < dlugoscChromosomu; i++)
            wzorzec.add(Math.abs(random.nextInt() % 2));
        for (int i = 0; i < dlugoscChromosomu; i++) {
            if (wzorzec.get(i) == 1) {
                dziecko1.add(rodzic2.geny.get(i));
                dziecko2.add(rodzic1.geny.get(i));
            } else {
                dziecko1.add(rodzic1.geny.get(i));
                dziecko2.add(rodzic2.geny.get(i));
            }
        }
        dzieci.addAll(Arrays.asList(dziecko1, dziecko2));
        return dzieci;
    }

    // #####################################################################################
    // ###############################  MUTACJA  ###########################################
    // #####################################################################################
    private Osobnik mutacja(String rodzaj, Osobnik osobnik, Double prawdopodobienstwo) {
        if (prawdopodobienstwo >= random.nextDouble()) {
            switch (rodzaj) {
                case "Mutacja Jednopunkowa":
                    osobnik = mutacjaJednopunktowa(osobnik);
                    break;
                case "Mutacja Dwupunktowa":
                    osobnik = mutacjaDwupunktowa(osobnik);
                    break;
                default:
                    osobnik = mutacjaBrzegowa(osobnik);
                    break;
            }
        }
        return osobnik;
    }

    private Osobnik mutacjaJednopunktowa(Osobnik osobnik) {
        Double rand = random.nextDouble();
        int locusX1 = (int) ((double) osobnik.chromosomy.get(0).dlugosc * rand);
        int locusX2 = (int) ((double) osobnik.chromosomy.get(1).dlugosc * rand);
        osobnik.chromosomy.get(0).geny.set(locusX1, osobnik.chromosomy.get(0).geny.get(locusX1) == 1 ? 1 : 0);
        osobnik.chromosomy.get(1).geny.set(locusX2, osobnik.chromosomy.get(1).geny.get(locusX2) == 1 ? 1 : 0);
        return osobnik;
    }

    private Osobnik mutacjaDwupunktowa(Osobnik osobnik) {
        Double rand = random.nextDouble();
        int locusX1_1 = (int) ((double) osobnik.chromosomy.get(0).dlugosc * rand);
        int locusX1_2 = (int) ((double) osobnik.chromosomy.get(0).dlugosc * rand);
        int locusX2_1 = (int) ((double) osobnik.chromosomy.get(1).dlugosc * rand);
        int locusX2_2 = (int) ((double) osobnik.chromosomy.get(1).dlugosc * rand);
        osobnik.chromosomy.get(0).geny.set(locusX1_1, osobnik.chromosomy.get(0).geny.get(locusX1_1) == 1 ? 1 : 0);
        osobnik.chromosomy.get(0).geny.set(locusX1_2, osobnik.chromosomy.get(0).geny.get(locusX1_2) == 1 ? 1 : 0);
        osobnik.chromosomy.get(1).geny.set(locusX2_1, osobnik.chromosomy.get(1).geny.get(locusX2_1) == 1 ? 1 : 0);
        osobnik.chromosomy.get(1).geny.set(locusX2_2, osobnik.chromosomy.get(1).geny.get(locusX2_2) == 1 ? 1 : 0);
        return osobnik;
    }

    private Osobnik mutacjaBrzegowa(Osobnik osobnik) {
        Double rand = random.nextDouble();
        if (rand > 0.5) {
            osobnik.chromosomy.get(0).geny.set(0, osobnik.chromosomy.get(0).geny.get(0) == 1 ? 1 : 0);
            osobnik.chromosomy.get(1).geny.set(0, osobnik.chromosomy.get(1).geny.get(0) == 1 ? 1 : 0);
        } else {
            Integer locusEnd = osobnik.chromosomy.get(0).geny.size() - 1;
            osobnik.chromosomy.get(0).geny.set(locusEnd, osobnik.chromosomy.get(0).geny.get(locusEnd) == 1 ? 1 : 0);
            osobnik.chromosomy.get(1).geny.set(locusEnd, osobnik.chromosomy.get(1).geny.get(locusEnd) == 1 ? 1 : 0);
        }
        return osobnik;
    }

    // #####################################################################################
    // ###############################  INWERSJA  ##########################################
    // #####################################################################################
    private Osobnik inwersja(Osobnik osobnik, Double prawdopodobienstwo) {
        if (prawdopodobienstwo >= random.nextDouble())
            osobnik.chromosomy.replaceAll(chr -> inwersjaChromosomu(chr));
        return osobnik;
    }

    private Chromosom inwersjaChromosomu(Chromosom chromosom) {
        Double rand1 = ((double) (random.nextInt(40) + 10)) / 100;
        Double rand2 = ((double) (random.nextInt(40) + 50)) / 100;
        int locus1 = (int) ((double) chromosom.dlugosc * rand1);
        int locus2 = (int) ((double) chromosom.dlugosc * rand2);
        for (int i = locus1; i < locus2; i++)
            chromosom.geny.set(i, chromosom.geny.get(i) == 1 ? 0 : 1);
        return chromosom;
    }

    public Long oblicz() throws IOException {

        long startTime = System.currentTimeMillis();
        ZakresZmiennej[] zakresyZmiennych = {
                new ZakresZmiennej(poczatekZakresuX1, koniecZakresuX1),
                new ZakresZmiennej(poczatekZakresuX2, koniecZakresuX2)};
        Populacja populacja = new Populacja(wielkoscPopulacji, zakresyZmiennych, dokladnosc);
        List<Osobnik> osobnicyDoreprodukcji;
        List<Osobnik> osobnicyOpercajeGenetyczne;
        List<String> wynikiAlgorytmu = new ArrayList<>();
        List<Double> fx_populacji = new ArrayList<>();
        List<Double> odchylenieStandardowe = new ArrayList<>();

        fx_populacji.add(populacja.obliczSredniaFunkcjePrzystsowania(5));
        wynikiAlgorytmu.add("EPOKA 0\tfx_populacji=" + populacja.obliczSredniaFunkcjePrzystsowania(5)
                + "\t\tnajlepszy_osobnik_fx: " + populacja.najlepszyOsobnik(maksymalizacja).wartoscFunkcjiPrzystsowania + "\n");
        System.out.println("fx_populacji_startowej=" + populacja.obliczSredniaFunkcjePrzystsowania(5) + " \n");

        for (int i = 0; i < iloscEpok; i++) {
            osobnicyDoreprodukcji = selekcja(metodaSelekcji, populacja.osobnicy, procentNajlepszych, maksymalizacja);
            osobnicyOpercajeGenetyczne = krzyzowanie(metodaKrzyzowania, osobnicyDoreprodukcji, prawdopodobienstwoKrzyzowania, (wielkoscPopulacji - iloscStrategiiElitarnej));
            osobnicyOpercajeGenetyczne.replaceAll(o -> mutacja(metodaMutacji, o, prawdopodobienstwoMutacji));
            osobnicyOpercajeGenetyczne.replaceAll(o -> inwersja(o, prawdopodobienstwoMutacji));
            populacja.osobnicy.removeAll(populacja.osobnicy.subList(iloscStrategiiElitarnej, populacja.wielkoscPopulacji));
            populacja.osobnicy.addAll(osobnicyOpercajeGenetyczne);

            fx_populacji.add(populacja.obliczSredniaFunkcjePrzystsowania(5));
            odchylenieStandardowe.add(liczOdchylenie(populacja.osobnicy, fx_populacji.get(fx_populacji.size()-1)));
            wynikiAlgorytmu.add("epoka " + (i + 1) + " \tfx_populacji=" + populacja.obliczSredniaFunkcjePrzystsowania(5)
                    + "\t\tnajlepszy_osobnik_fx: " + populacja.najlepszyOsobnik(maksymalizacja).wartoscFunkcjiPrzystsowania + "\n");
            System.out.print("epoka  " + (i + 1) + " fx_populacji=" + populacja.obliczSredniaFunkcjePrzystsowania(5) + "\t\t");
            System.out.println("najlepszy_osobnik_fx: " + populacja.najlepszyOsobnik(maksymalizacja).wartoscFunkcjiPrzystsowania);
        }

        long endTime = System.currentTimeMillis();

//        String nazwaFolderu = System.getProperty("user.dir") + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-d-HH:mm:ss_")) + random.nextInt(10);
        String nazwaFolderu = System.getProperty("user.dir") + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH.mm.ss_")) + random.nextInt(10);
        File theDir = new File( nazwaFolderu);
        theDir.mkdirs();
        zapiszWynikDoPliku(wynikiAlgorytmu, nazwaFolderu+"/wynik.txt");
        rysujWykres(fx_populacji, nazwaFolderu, "Średnia", "Funkcja Przystosowania");
        rysujWykres(odchylenieStandardowe, nazwaFolderu, "Odchylenie", "Srednie odchylenie standardowe");

        return endTime - startTime;

    }

    private void rysujWykres(List<Double> lista, String path, String nazwa, String yTittle){

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries data = new XYSeries("wartosc");
        for (int i = 0; i <lista.size() ; i++)
            data.add(Double.valueOf(i), lista.get(i));
        dataset.addSeries(data);
        SwingUtilities.invokeLater(() -> {
            WykresFactory example = null;
            try {
                example = new WykresFactory(path, nazwa, yTittle, dataset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            example.setAlwaysOnTop(true);
            example.pack();
            example.setSize(800, 400);
            example.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            example.setVisible(true);
        });
    }

    private double liczOdchylenie(List<Osobnik> osobnicy, double sredniaPopulacji){
        double suma = 0;
        for (int i = 0; i < osobnicy.size(); i++)
            suma += Math.pow((osobnicy.get(i).wartoscFunkcjiPrzystsowania - sredniaPopulacji),2);
        return Math.pow((suma/osobnicy.size()),0.5);
    }

    private void zapiszWynikDoPliku(List<String> wynikiAlgorytmu, String plik) {
        try {
            File myObj = new File(plik);
            myObj.createNewFile();
            FileWriter myWriter = new FileWriter(plik);
            wynikiAlgorytmu.forEach(wynik -> {
                try {
                    myWriter.write(wynik);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            myWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
