package algorytmyGenetyczne;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Pane mainPane = FXMLLoader.load(getClass().getResource("/ManView.fxml"));
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Algorytmy Genetyczne");
        stage.show();

        Button start = (Button) scene.lookup("#start");
        ComboBox metodaSelekcjiForm = (ComboBox) scene.lookup("#metoda_selekcji");
        ComboBox metodaKrzyzowaniaForm = (ComboBox) scene.lookup("#metoda_krzyzowania");
        ComboBox metodaMutacjiForm = (ComboBox) scene.lookup("#metoda_mutacji");
        ComboBox rodzajOptymalizacjiForm = (ComboBox) scene.lookup("#rodzaj_optymalizacji");
        TextField poczatekZakresuX1Form = (TextField) scene.lookup("#poczatek_zakresu_x1");
        TextField koniecZakresuX1Form = (TextField) scene.lookup("#koniec_zakresu_x1");
        TextField poczatekZakresuX2Form = (TextField) scene.lookup("#poczatek_zakresu_x2");
        TextField koniecZakresuX2Form = (TextField) scene.lookup("#koniec_zakresu_x2");
        TextField iloscPopulacjiForm = (TextField) scene.lookup("#ilosc_populacji");
        TextField dokladnoscForm = (TextField) scene.lookup("#dokladnosc");
        TextField iloscEpokForm = (TextField) scene.lookup("#ilosc_epok");
        TextField procentNajlepszychForm = (TextField) scene.lookup("#ilosc_najlepszych");
        TextField iloscStrategiiElitarnejForm = (TextField) scene.lookup("#ilosc_strategii_elitarnej");
        TextField prawdopodobienstwoKrzyzowaniaForm = (TextField) scene.lookup("#prawdopodobienstwo_krzyzowania");
        TextField prawdopodobienstwoMutacjiForm = (TextField) scene.lookup("#prawdopodobienstwo_mutacji");
        TextField prawdopodobienstwoInwersjiForm = (TextField) scene.lookup("#prawdopodobienstwo_inwersji");
        Text info = (Text) scene.lookup("#info");

        metodaSelekcjiForm.setValue(metodaSelekcjiForm.getItems().get(1));
        metodaKrzyzowaniaForm.setValue(metodaKrzyzowaniaForm.getItems().get(0));
        metodaMutacjiForm.setValue(metodaMutacjiForm.getItems().get(0));
        rodzajOptymalizacjiForm.setValue(rodzajOptymalizacjiForm.getItems().get(0));
        poczatekZakresuX1Form.setText("-1.5");
        koniecZakresuX1Form.setText("4");
        poczatekZakresuX2Form.setText("-3");
        koniecZakresuX2Form.setText("4");
        dokladnoscForm.setText("4");
        iloscPopulacjiForm.setText("100");
        iloscEpokForm.setText("50");
        procentNajlepszychForm.setText("0.3");
        iloscStrategiiElitarnejForm.setText("2");
        prawdopodobienstwoKrzyzowaniaForm.setText("0.7");
        prawdopodobienstwoMutacjiForm.setText("0.1");
        prawdopodobienstwoInwersjiForm.setText("0.1");

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    String metodaSelekcji = metodaSelekcjiForm.getValue().toString();
                    String metodaKrzyzowania = metodaKrzyzowaniaForm.getValue().toString();
                    String metodaMutacji = metodaMutacjiForm.getValue().toString();
                    Double poczatekZakresuX1 = Double.parseDouble(poczatekZakresuX1Form.getText());
                    Double koniecZakresuX1 = Double.parseDouble(koniecZakresuX1Form.getText());
                    Double poczatekZakresuX2 = Double.parseDouble(poczatekZakresuX2Form.getText());
                    Double koniecZakresuX2 = Double.parseDouble(koniecZakresuX2Form.getText());
                    Integer iloscPopulacji = Integer.parseInt(iloscPopulacjiForm.getText());
                    Integer dokladnosc = Integer.parseInt(dokladnoscForm.getText());
                    Integer iloscEpok = Integer.parseInt(iloscEpokForm.getText());
                    Double procentNajlepszych = Double.parseDouble(procentNajlepszychForm.getText());
                    Integer iloscStrategiiElitarnej = Integer.parseInt(iloscStrategiiElitarnejForm.getText());
                    iloscStrategiiElitarnej += iloscStrategiiElitarnej%2 == 1 ? 1 : 0;
                    Double prawdopodobienstwoKrzyzowania = Double.parseDouble(prawdopodobienstwoKrzyzowaniaForm.getText());
                    Double prawdopodobienstwoMutacji = Double.parseDouble(prawdopodobienstwoMutacjiForm.getText());
                    Double prawdopodobienstwoInwersji = Double.parseDouble(prawdopodobienstwoInwersjiForm.getText());
                    Boolean maksymalizacja = rodzajOptymalizacjiForm.getValue().toString().equals("Maksymalizacja");

                    Algorytm algorytm = new Algorytm(
                            metodaSelekcji,
                            metodaKrzyzowania,
                            metodaMutacji,
                            poczatekZakresuX1,
                            koniecZakresuX1,
                            poczatekZakresuX2,
                            koniecZakresuX2,
                            iloscPopulacji,
                            dokladnosc,
                            iloscEpok,
                            procentNajlepszych,
                            iloscStrategiiElitarnej,
                            prawdopodobienstwoKrzyzowania,
                            prawdopodobienstwoMutacji,
                            prawdopodobienstwoInwersji,
                            maksymalizacja
                    );
                    long duration = algorytm.oblicz();

                    NumberFormat formatter = new DecimalFormat("#0.000");
                    String time = formatter.format(duration / 1000d);
                    info.setText("Czas wykonania algorytmu: " + time + " sekund");
                    info.setVisible(true);


                } catch (Exception exception) {
                    exception.printStackTrace();
                    info.setText("Prosze uzupełnić poprawnie wszystkie parametry");
                    info.setVisible(true);
                }

            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
