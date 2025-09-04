package HotelApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) {
        Parent root = loadFXML("Main");              
        scene = new Scene(root, 1100, 680);
        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    
    public static void setRoot(String fxml) {
        scene.setRoot(loadFXML(fxml));
    }

    
    private static Parent loadFXML(String name) {
        String path = "/HotelApp/" + name + ".fxml";
        try {
            URL url = App.class.getResource(path);
            if (url == null) {
                throw new IllegalStateException("FXML not found: " + path);
            }
            return FXMLLoader.load(url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load FXML: " + path, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
