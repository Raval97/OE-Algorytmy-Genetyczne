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
        TextField iloscBitowForm = (TextField) scene.lookup("#ilosc_bitow");
        TextField iloscEpokForm = (TextField) scene.lookup("#ilosc_epok");
        TextField iloscNajlepszychForm = (TextField) scene.lookup("#ilosc_najlepszych");
        TextField iloscStrategiiElitarnejForm = (TextField) scene.lookup("#ilosc_strategii_elitarnej");
        TextField prawdopodobienstwoKrzyzowaniaForm = (TextField) scene.lookup("#prawdopodobienstwo_krzyzowania");
        TextField prawdopodobienstwoMutacjiForm = (TextField) scene.lookup("#prawdopodobienstwo_mutacji");
        TextField prawdopodobienstwoZamianyForm = (TextField) scene.lookup("#prawdopodobienstwo_zamiany");
        Text error = (Text) scene.lookup("#error");

        metodaSelekcjiForm.setValue(metodaSelekcjiForm.getItems().get(0));
        metodaKrzyzowaniaForm.setValue(metodaKrzyzowaniaForm.getItems().get(0));
        metodaMutacjiForm.setValue(metodaMutacjiForm.getItems().get(0));
        poczatekZakresuForm.setText("1");
        koniecZakresuForm.setText("10");
        iloscPopulacjiForm.setText("100");
        iloscBitowForm.setText("40");
        iloscEpokForm.setText("1000");
        iloscNajlepszychForm.setText("20");
        iloscStrategiiElitarnejForm.setText("10");
        prawdopodobienstwoKrzyzowaniaForm.setText("0.6");
        prawdopodobienstwoMutacjiForm.setText("0.4");
        prawdopodobienstwoZamianyForm.setText("0.1");

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {

                try {
                    String metodaSelekcji = metodaSelekcjiForm.getValue().toString();
                    String metodaKrzyzowania = metodaKrzyzowaniaForm.getValue().toString();
                    String metodaMutacji = metodaMutacjiForm.getValue().toString();
                    Integer poczatekZakresu = Integer.parseInt(poczatekZakresuForm.getText());
                    Integer koniecZakresu = Integer.parseInt(koniecZakresuForm.getText());
                    Integer iloscPopulacji = Integer.parseInt(iloscPopulacjiForm.getText());
                    Integer iloscBitow = Integer.parseInt(iloscPopulacjiForm.getText());
                    Integer iloscEpok = Integer.parseInt(iloscEpokForm.getText());
                    Integer iloscNajlepszych = Integer.parseInt(iloscNajlepszychForm.getText());
                    Integer iloscStrategiiElitarnej = Integer.parseInt(iloscStrategiiElitarnejForm.getText());
                    Double prawdopodobienstwoKrzyzowania = Double.parseDouble(prawdopodobienstwoKrzyzowaniaForm.getText());
                    Double prawdopodobienstwoMutacji = Double.parseDouble(prawdopodobienstwoMutacjiForm.getText());
                    Double prawdopodobienstwoZamiany = Double.parseDouble(prawdopodobienstwoZamianyForm.getText());

                    System.out.println(metodaSelekcji);
                    System.out.println(metodaKrzyzowania);
                    System.out.println(metodaMutacji);
                    System.out.println(poczatekZakresu);
                    System.out.println(koniecZakresu);
                    System.out.println(iloscPopulacji);
                    System.out.println(iloscBitow);
                    System.out.println(iloscEpok);
                    System.out.println(iloscNajlepszych);
                    System.out.println(iloscStrategiiElitarnej);
                    System.out.println(prawdopodobienstwoKrzyzowania);
                    System.out.println(prawdopodobienstwoMutacji);
                    System.out.println(prawdopodobienstwoZamiany);

                } catch (Exception exception) {
                    exception.printStackTrace();
                    error.setVisible(true);
                }

            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
