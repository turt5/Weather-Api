package com.example.weather_api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class WeatherApp extends Application {

    private static final String API_KEY = "108e658ece27b123c22273339290aec2";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String IMAGE_URL = "http://openweathermap.org/img/w/";

    private Label locationLabel;
    private Label temperatureLabel;
    private Label descriptionLabel;
    private Label humidityLabel;
    private Label windLabel;
    private ImageView iconImageView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        locationLabel = new Label();
        temperatureLabel = new Label();
        descriptionLabel = new Label();
        humidityLabel = new Label();
        windLabel = new Label();
        iconImageView = new ImageView();

        Button searchButton = new Button("Search");
        TextField cityTextField = new TextField();
        cityTextField.setPromptText("Enter city name");

        HBox searchBox = new HBox(cityTextField, searchButton);
        searchBox.setSpacing(10);
        searchBox.setAlignment(Pos.CENTER);

        GridPane weatherGrid = new GridPane();
        weatherGrid.setHgap(10);
        weatherGrid.setVgap(10);
        weatherGrid.setAlignment(Pos.CENTER);
        weatherGrid.add(new Label("Location: "), 0, 0);
        weatherGrid.add(locationLabel, 1, 0);
        weatherGrid.add(new Label("Temperature: "), 0, 1);
        weatherGrid.add(temperatureLabel, 1, 1);
        weatherGrid.add(new Label("Description: "), 0, 2);
        weatherGrid.add(descriptionLabel, 1, 2);
        weatherGrid.add(new Label("Humidity: "), 0, 3);
        weatherGrid.add(humidityLabel, 1, 3);
        weatherGrid.add(new Label("Wind: "), 0, 4);
        weatherGrid.add(windLabel, 1, 4);
        weatherGrid.add(iconImageView, 2, 0, 1, 5);

        VBox root = new VBox(searchBox, weatherGrid);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 500, 300);

        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.show();

        searchButton.setOnAction(event -> {
            String city = cityTextField.getText().trim();

            if (city.isEmpty()) {
                showAlert("Please enter a city name");
                return;
            }

            String url = API_URL + "?q=" + city + "&appid=" + API_KEY;

            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonObject = new JSONObject(response.toString());

                    // Extract weather data from the JSON object
                    String cityName = jsonObject.getString("name");
                    JSONObject main = jsonObject.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    int humidity = main.getInt("humidity");
                    JSONArray weatherArray = jsonObject.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String description = weather.getString("description");
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    double windSpeed = wind.getDouble("speed");
                    String iconCode = weather.getString("icon");

                    // Update UI with weather data
                    Platform.runLater(() -> {
                        locationLabel.setText(cityName);
                        temperatureLabel.setText(String.format("%.1f °C", temperature - 273.15));
                        descriptionLabel.setText(description);
                        humidityLabel.setText(humidity + "%");
                        windLabel.setText(windSpeed + " m/s");
                        iconImageView.setImage(new Image(IMAGE_URL + iconCode + ".png"));
                    });

                } else {
                    showAlert("HTTP error code: " + responseCode);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                showAlert("An error occurred while fetching weather data");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

