package HotelApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/HotelApp/Login.fxml"));
        scene = new Scene(root, 600, 400);

        // load CSS
        scene.getStylesheets().add(getClass().getResource("/HotelApp/styles.css").toExternalForm());

        stage.setTitle("Hotel Management Login");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");       
        launch();
    }

    //login role//////////////////////////

    public static void showLogin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("Login.fxml"));
            Parent loginRoot = loader.load();

            Scene scene = new Scene(loginRoot, 1090, 715);
            scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Login - Hotel Management");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        try { setRoot("Login"); } 
        catch (Exception e) { e.printStackTrace(); } 
    }

}
