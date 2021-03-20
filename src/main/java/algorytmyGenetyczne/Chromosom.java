package algorytmyGenetyczne;

import java.util.ArrayList;
import java.util.Random;

public class Chromosom {

    double wartoscFunkcjiPrzystsowania;
    ArrayList<Integer> geny;
    int dlugosc;

    public Chromosom(int dlugosc) {

        this.dlugosc = dlugosc;
        wartoscFunkcjiPrzystsowania = 0;

        Random random = new Random();
        geny = new ArrayList<>();
        geny.ensureCapacity(dlugosc);
        for (int i = 0; i < dlugosc; i++)
            geny.add(Math.abs(random.nextInt() % 2));

    }

    public void obliczWartoscFunkcjiPrzystsowania() {
        wartoscFunkcjiPrzystsowania = 0;
        for (int i = 0; i < this.dlugosc; i++) {
            if (geny.get(i) == 1) {
                ++wartoscFunkcjiPrzystsowania;
            }
        }
    }

    public Double dekodowanieDziesietne(){
        int decimal = Integer.parseInt(getGenyToString(), 2);
        return -10 + decimal * ( 20 / (Math.pow(2,25) - 1));
    }

    public double getWartoscFunkcjiPrzystsowania() {
        return wartoscFunkcjiPrzystsowania;
    }

    public void setWartoscFunkcjiPrzystsowania(double wartoscFunkcjiPrzystsowania) {
        this.wartoscFunkcjiPrzystsowania = wartoscFunkcjiPrzystsowania;
    }

    public ArrayList<Integer> getGeny() {
        return geny;
    }

    public String getGenyToString() {
        final String[] value = {""};
        geny.forEach(e -> value[0] += e.toString());
        return value[0];
    }

    public void setGeny(ArrayList<Integer> geny) {
        this.geny = geny;
    }

    public int getDlugosc() {
        return dlugosc;
    }

    public void setDlugosc(int dlugosc) {
        this.dlugosc = dlugosc;
    }

    @Override
    public String toString() {
        final String[] value = {"Chromosom: "};
        geny.forEach(e -> value[0] += e.toString());
        value[0] += "  " + dekodowanieDziesietne().toString();
        value[0] += "\n";
        return value[0];
    }
}
