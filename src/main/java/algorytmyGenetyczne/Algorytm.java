package algorytmyGenetyczne;

public class Algorytm {

    public Algorytm() {
    }

    // MCCORMICK FUNCTION
    public double funkcjaPrzystosowania(int x1, int x2){
        double value = Math.sin((x1+x2)) + Math.pow((x1-x2),2) - 1.5*x1 +2.5*x2 + 1;
        return value;
    }

    public int obliczDlugoscChromosomu(int A, int B, int dokladnosc){
        return (int) Math.ceil((Math.log((B-A)*Math.pow(10,dokladnosc)) / Math.log(2)) + (Math.log(1) / Math.log(2)));
    }

    public void oblicz(int poczatekZakresu, int koniecZakresu, int dokladnosc, int dlPopulacji){

        int dlugoscChromosomu = obliczDlugoscChromosomu(poczatekZakresu, koniecZakresu, dokladnosc);
        System.out.println(dlugoscChromosomu);
        Populacja populacja = new Populacja(dlPopulacji, dlugoscChromosomu);
        System.out.println(populacja.toString());
    };

}
