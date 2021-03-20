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
import javafx.scene.text.Font;
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
        TextField poczatekZakresuForm = (TextField) scene.lookup("#poczatek_zakresu");
        TextField koniecZakresuForm = (TextField) scene.lookup("#koniec_zakresu");
        TextField iloscPopulacjiForm = (TextField) scene.lookup("#ilosc_populacji");
        TextField dokladnoscForm = (TextField) scene.lookup("#dokladnosc");
        TextField iloscEpokForm = (TextField) scene.lookup("#ilosc_epok");
        TextField iloscNajlepszychForm = (TextField) scene.lookup("#ilosc_najlepszych");
        TextField iloscStrategiiElitarnejForm = (TextField) scene.lookup("#ilosc_strategii_elitarnej");
        TextField prawdopodobienstwoKrzyzowaniaForm = (TextField) scene.lookup("#prawdopodobienstwo_krzyzowania");
        TextField prawdopodobienstwoMutacjiForm = (TextField) scene.lookup("#prawdopodobienstwo_mutacji");
        TextField prawdopodobienstwoInwersjiForm = (TextField) scene.lookup("#prawdopodobienstwo_inwersji");
        Text info = (Text) scene.lookup("#info");

        metodaSelekcjiForm.setValue(metodaSelekcjiForm.getItems().get(0));
        metodaKrzyzowaniaForm.setValue(metodaKrzyzowaniaForm.getItems().get(0));
        metodaMutacjiForm.setValue(metodaMutacjiForm.getItems().get(0));
        poczatekZakresuForm.setText("1");
        koniecZakresuForm.setText("10");
        iloscPopulacjiForm.setText("10");
        dokladnoscForm.setText("6");
        iloscEpokForm.setText("1000");
        iloscNajlepszychForm.setText("20");
        iloscStrategiiElitarnejForm.setText("10");
        prawdopodobienstwoKrzyzowaniaForm.setText("0.6");
        prawdopodobienstwoMutacjiForm.setText("0.4");
        prawdopodobienstwoInwersjiForm.setText("0.1");


        Algorytm algorytm = new Algorytm();

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                start.setText("W trakcie wykonywania ...");
                start.setStyle("-fx-font-size:16; -fx-background-color: #7f7fd7; -fx-text-fill: #fff");
                start.setDisable(true);

                try {
                    String metodaSelekcji = metodaSelekcjiForm.getValue().toString();
                    String metodaKrzyzowania = metodaKrzyzowaniaForm.getValue().toString();
                    String metodaMutacji = metodaMutacjiForm.getValue().toString();
                    Integer poczatekZakresu = Integer.parseInt(poczatekZakresuForm.getText());
                    Integer koniecZakresu = Integer.parseInt(koniecZakresuForm.getText());
                    Integer iloscPopulacji = Integer.parseInt(iloscPopulacjiForm.getText());
                    Integer dokladnosc = Integer.parseInt(dokladnoscForm.getText());
                    Integer iloscEpok = Integer.parseInt(iloscEpokForm.getText());
                    Integer iloscNajlepszych = Integer.parseInt(iloscNajlepszychForm.getText());
                    Integer iloscStrategiiElitarnej = Integer.parseInt(iloscStrategiiElitarnejForm.getText());
                    Double prawdopodobienstwoKrzyzowania = Double.parseDouble(prawdopodobienstwoKrzyzowaniaForm.getText());
                    Double prawdopodobienstwoMutacji = Double.parseDouble(prawdopodobienstwoMutacjiForm.getText());
                    Double prawdopodobienstwoInwersji = Double.parseDouble(prawdopodobienstwoInwersjiForm.getText());

                    long startTime = System.currentTimeMillis();

                    algorytm.oblicz(poczatekZakresu, koniecZakresu, dokladnosc, iloscPopulacji);

//                    Thread.sleep(1000);
                    // ...

                    long endTime = System.currentTimeMillis();
                    NumberFormat formatter = new DecimalFormat("#0.00000");
                    String time = formatter.format((endTime - startTime) / 1000d);
                    info.setText("Czas wykonania algorytmu: " + time + " seconds");
                    info.setVisible(true);
                    start.setText("START");
                    start.setStyle("-fx-font-size:48; -fx-background-color: #1e1e9c; -fx-text-fill: #fff");
                    start.setDisable(false);

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
