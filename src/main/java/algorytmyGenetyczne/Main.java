package algorytmyGenetyczne;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Pane mainPane = FXMLLoader.load(getClass().getResource("/ManView.fxml"));
        Scene scene = new Scene(mainPane);
        stage.setScene(scene);
        stage.setTitle("Projekt");
        stage.show();



        Button start = (Button) scene.lookup("#start");

        System.out.println("dsf");
        start.setText("sdfdfds");
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                ComboBox m_mutacji = (ComboBox) scene.lookup("#m_mutacji");
                String mutacja = m_mutacji.getValue().toString();
                System.out.println(mutacja);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
