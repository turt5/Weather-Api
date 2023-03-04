module com.example.weather_api {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires okhttp3;
    requires java.json;
    requires org.json;

    opens com.example.weather_api to javafx.fxml;
    exports com.example.weather_api;
}