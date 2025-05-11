package Directeur_Flux;

import Database.Allocation;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class DirecteurFluxUI {

    ObservableList<String> observableList;
    protected DirecteurFluxAgent directeurFluxAgent;

    public void setControleurAgent(DirecteurFluxAgent directeurFluxAgent) {
        this.directeurFluxAgent = directeurFluxAgent;
    }

    public Stage createStage() throws ControllerException {
        StartContainer();

        Stage stage = new Stage();
        BorderPane mainPane = new BorderPane();

        // Ajouter une icône à la fenêtre
        try {
            Image iconstage = new Image(getClass().getResourceAsStream("/images/compagnie-aerienne.png"));
            stage.getIcons().add(iconstage);
        } catch (Exception e) {
            logMessage("Icône non trouvée, veuillez vérifier le chemin de 'compagnie-aerienne.png'.");
        }

        // Barre de navigation supérieure
        Label title = new Label("Contrôle Aérien - Directeur Flux");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        backButton.setOnAction(e -> stage.close());

        HBox navBar = new HBox(20, title, backButton);
        navBar.setStyle("-fx-background-color: #1E3A8A; -fx-padding: 15px;");
        navBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);
        mainPane.setTop(navBar);

        // Zone centrale : TableView
        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(15));
        centerBox.setAlignment(Pos.CENTER);

        // TableView
        TableView<Allocation> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #4B5563; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-width: 1px;");
        tableView.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.9));
        tableView.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.5));

        // Créer les colonnes du tableau
        TableColumn<Allocation, String> compagnieColumn = new TableColumn<>("Compagnie");
        compagnieColumn.setCellValueFactory(new PropertyValueFactory<>("compagnie"));

        TableColumn<Allocation, String> avionColumn = new TableColumn<>("Avion");
        avionColumn.setCellValueFactory(new PropertyValueFactory<>("avion"));

        TableColumn<Allocation, String> piloteColumn = new TableColumn<>("Pilote");
        piloteColumn.setCellValueFactory(new PropertyValueFactory<>("pilote"));

        TableColumn<Allocation, String> DateColumn = new TableColumn<>("Date Atterissage");
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Allocation, String> HeureColumn = new TableColumn<>("Heure Atterissage");
        HeureColumn.setCellValueFactory(cellData -> {
            String heureMinute = cellData.getValue().getHeureAtterissage();
            String[] parts = heureMinute.split(":");
            String formattedHeureMinute = parts[0] + " : " + parts[1];
            return new SimpleStringProperty(formattedHeureMinute);
        });

        TableColumn<Allocation, String> PisteColumn = new TableColumn<>("Piste");
        PisteColumn.setCellValueFactory(new PropertyValueFactory<>("piste"));

        tableView.getColumns().addAll(compagnieColumn, avionColumn, piloteColumn, DateColumn, HeureColumn, PisteColumn);

        compagnieColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        avionColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        piloteColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        DateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.20));
        HeureColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        PisteColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));

        // Styliser les colonnes
        for (TableColumn<Allocation, ?> column : tableView.getColumns()) {
            column.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-background-color: #F3F4F6; -fx-text-fill: #1E3A8A; -fx-font-weight: bold;");
        }

        tableView.setEffect(new DropShadow(5, Color.gray(0.3)));
        centerBox.getChildren().add(tableView);
        mainPane.setCenter(centerBox);

        // Journal en bas
        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(observableList);
        listView.setStyle("-fx-background-color: white; -fx-border-color: #4B5563; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        listView.setPrefHeight(150);

        listView.setCellFactory(listViewFactory -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 15px; -fx-padding: 8px;");
                    if (item.contains("Erreur")) {
                        setTextFill(Color.RED);
                        setStyle(getStyle() + "-fx-background-color: #FFEBEE;");
                    } else if (item.contains("succès")) {
                        setTextFill(Color.web("#10B981"));
                        setStyle(getStyle() + "-fx-background-color: #E6FFFA;");
                    } else {
                        setTextFill(Color.web("#333333"));
                        setStyle(getStyle() + "-fx-background-color: white;");
                    }
                    FadeTransition fade = new FadeTransition(Duration.millis(500), this);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.play();
                }
            }
        });

        observableList.addListener((ListChangeListener<String>) change -> {
            listView.scrollTo(observableList.size() - 1);
        });

        VBox journalBox = new VBox(5, new Label("Journal des Activités"), listView);
        journalBox.setStyle("-fx-background-color: #F3F4F6; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        journalBox.setPadding(new Insets(10));
        journalBox.setEffect(new DropShadow(5, Color.gray(0.3)));
        mainPane.setBottom(journalBox);

        // Fond dégradé
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#2D3748")),
                new Stop(1, Color.web("#4A5568"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        mainPane.setBackground(new Background(new BackgroundFill(gradient, null, null)));

        // Animation d'ouverture
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Charger les données initiales
        loadData(tableView);

        // Timeline pour rafraîchir le TableView
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            tableView.getItems().clear();
            loadData(tableView);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Scene scene = new Scene(mainPane, 1200, 700);
        stage.setTitle("Contrôle Aérien - Directeur Flux");
        stage.setScene(scene);
        return stage;
    }

    private void loadData(TableView<Allocation> tableView) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Allocation", "root", "");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM allocation WHERE arrive = 'Arrivée'");

            while (resultSet.next()) {
                Allocation allocation = new Allocation(
                        resultSet.getString("compagnie"),
                        resultSet.getString("avion"),
                        resultSet.getString("pilote"),
                        resultSet.getString("date"),
                        resultSet.getInt("heure"),
                        resultSet.getInt("minute"),
                        resultSet.getString("arrive"),
                        resultSet.getString("piste")
                );
                tableView.getItems().add(allocation);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logMessage("Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void StartContainer() throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        AgentController agentController = container.createNewAgent("DirecteurFlux", "Directeur_Flux.DirecteurFluxAgent", new Object[]{this});
        agentController.start();
    }

    public void logMessage(String aclMessage) {
        Platform.runLater(() -> {
            observableList.add(aclMessage);
        });
    }
}