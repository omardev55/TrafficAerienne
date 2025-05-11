package Avion;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiEvent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class AvionUI {

    protected AvionAgent avionAgent;
    ObservableList<String> observableList;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Allocation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private Connection connection;

    public Stage createStage() throws StaleProxyException {
        StartContainer();

        // Establish database connection
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logMessage("Connexion à la base de données établie.");
        } catch (SQLException e) {
            logMessage("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }

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
        Label title = new Label("Contrôle Aérien - Avion");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        backButton.setOnAction(e -> stage.close());

        HBox navBar = new HBox(20, title, backButton);
        navBar.setStyle("-fx-background-color: #1E3A8A; -fx-padding: 15px;");
        navBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);
        mainPane.setTop(navBar);

        // Zone centrale : Formulaire et images
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(15));

        // Image à gauche
        ImageView leftImageView = new ImageView();
        leftImageView.setPreserveRatio(true);
        leftImageView.setSmooth(true);
        leftImageView.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.3));
        leftImageView.setFitHeight(300);
        try {
            Image leftImage = new Image(getClass().getResourceAsStream("/images/pistesoleil.jpg"));
            leftImageView.setImage(leftImage);
        } catch (Exception e) {
            logMessage("Image non trouvée, veuillez vérifier le chemin de 'pistesoleil.jpg'.");
        }
        StackPane leftImagePane = new StackPane(leftImageView);
        leftImagePane.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        leftImagePane.setPadding(new Insets(10));
        leftImagePane.setEffect(new DropShadow(5, Color.gray(0.3)));

        // Formulaire au centre
        VBox formBox = new VBox(10);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        formBox.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.35));

        Label formTitle = new Label("Gestion de l'Avion");
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #4B5563;");

        ChoiceBox<String> select1 = new ChoiceBox<>();
        select1.setStyle("-fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        ChoiceBox<String> select2 = new ChoiceBox<>();
        select2.setStyle("-fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Button decollageButton = new Button("Décollage");
        Button atterrissageButton = new Button("Atterrissage");
        decollageButton.setDisable(true);
        atterrissageButton.setDisable(true);

        decollageButton.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        atterrissageButton.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        DropShadow shadow = new DropShadow(10, Color.gray(0.4));
        decollageButton.setOnMouseEntered(e -> {
            decollageButton.setCursor(Cursor.HAND);
            decollageButton.setEffect(shadow);
            decollageButton.setScaleX(1.05);
            decollageButton.setScaleY(1.05);
        });
        decollageButton.setOnMouseExited(e -> {
            decollageButton.setCursor(Cursor.DEFAULT);
            decollageButton.setEffect(null);
            decollageButton.setScaleX(1.0);
            decollageButton.setScaleY(1.0);
        });

        atterrissageButton.setOnMouseEntered(e -> {
            atterrissageButton.setCursor(Cursor.HAND);
            atterrissageButton.setEffect(shadow);
            atterrissageButton.setScaleX(1.05);
            atterrissageButton.setScaleY(1.05);
        });
        atterrissageButton.setOnMouseExited(e -> {
            atterrissageButton.setCursor(Cursor.DEFAULT);
            atterrissageButton.setEffect(null);
            atterrissageButton.setScaleX(1.0);
            atterrissageButton.setScaleY(1.0);
        });

        Label compagnieIcon = new Label("✈️ ");
        Label avionIcon = new Label("🛩️ ");

        HBox buttonBox = new HBox(10, decollageButton, atterrissageButton);
        buttonBox.setAlignment(Pos.CENTER);

        formBox.getChildren().addAll(
                formTitle,
                separator,
                createLabeledFieldWithIcon("Compagnie", compagnieIcon, select1),
                createLabeledFieldWithIcon("Avion", avionIcon, select2),
                buttonBox
        );
        formBox.setAlignment(Pos.TOP_CENTER);

        // Image à droite
        ImageView rightImageView = new ImageView();
        rightImageView.setPreserveRatio(true);
        rightImageView.setSmooth(true);
        rightImageView.fitWidthProperty().bind(mainPane.widthProperty().multiply(0.3));
        rightImageView.setFitHeight(300);
        try {
            Image rightImage = new Image(getClass().getResourceAsStream("/images/avionpiste.jpg"));
            rightImageView.setImage(rightImage);
        } catch (Exception e) {
            logMessage("Image non trouvée, veuillez vérifier le chemin de 'avionpiste.jpg'.");
        }
        StackPane rightImagePane = new StackPane(rightImageView);
        rightImagePane.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        rightImagePane.setPadding(new Insets(10));
        rightImagePane.setEffect(new DropShadow(5, Color.gray(0.3)));

        centerBox.getChildren().addAll(leftImagePane, formBox, rightImagePane);
        HBox.setHgrow(leftImagePane, Priority.ALWAYS);
        HBox.setHgrow(formBox, Priority.ALWAYS);
        HBox.setHgrow(rightImagePane, Priority.ALWAYS);
        mainPane.setCenter(centerBox);

        // Journal en bas
        observableList = FXCollections.observableArrayList();
        ListView<String> conversationList = new ListView<>(observableList);
        conversationList.setStyle("-fx-background-color: white; -fx-border-color: #4B5563; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        conversationList.setPrefHeight(150);

        conversationList.setCellFactory(listView -> new ListCell<String>() {
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
            conversationList.scrollTo(observableList.size() - 1);
        });

        VBox journalBox = new VBox(5, new Label("Journal des Activités"), conversationList);
        journalBox.setStyle("-fx-background-color: #F3F4F6; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        journalBox.setPadding(new Insets(10));
        journalBox.setEffect(new DropShadow(5, Color.gray(0.3)));
        mainPane.setBottom(journalBox);

        // Actions des boutons
        decollageButton.setOnAction(e -> {
            String selectedAvion = select2.getValue();
            String selectedCompagnie = select1.getValue();
            if (selectedAvion != null && selectedCompagnie != null) {
                try {
                    PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM allocation WHERE compagnie = ? AND avion = ?");
                    deleteStatement.setString(1, selectedCompagnie);
                    deleteStatement.setString(2, selectedAvion);
                    deleteStatement.executeUpdate();
                    String message = String.format("Pilote : Ici %s de %s, je vais Décoller veuillez me confirmer ma piste !", selectedAvion, selectedCompagnie);

                    GuiEvent event = new GuiEvent(this, 2);
                    event.addParameter(message);
                    event.addParameter(selectedCompagnie);
                    event.addParameter(selectedAvion);
                    avionAgent.onGuiEvent(event);

                    select1.setValue(null);
                    select2.setValue(null);
                    refreshUI(select1, select2, decollageButton, atterrissageButton);

                    atterrissageButton.setDisable(true);
                    decollageButton.setDisable(true);
                } catch (SQLException ex) {
                    logMessage("Erreur lors du décollage : " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        atterrissageButton.setOnAction(e -> {
            String selectedAvion = select2.getValue();
            String selectedCompagnie = select1.getValue();
            if (selectedAvion != null && selectedCompagnie != null) {
                try {
                    int nextPisteNumber = 1;
                    boolean pisteFound = false;
                    while (nextPisteNumber <= 60) {
                        PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM allocation WHERE piste = ? AND arrive = 'Arrivée'");
                        checkStatement.setInt(1, nextPisteNumber);
                        ResultSet resultSet = checkStatement.executeQuery();
                        if (!resultSet.next()) {
                            PreparedStatement updateStatement = connection.prepareStatement("UPDATE allocation SET arrive = 'Arrivée', piste = ? WHERE compagnie = ? AND avion = ?");
                            updateStatement.setInt(1, nextPisteNumber);
                            updateStatement.setString(2, selectedCompagnie);
                            updateStatement.setString(3, selectedAvion);
                            updateStatement.executeUpdate();
                            pisteFound = true;
                            break;
                        }
                        nextPisteNumber++;
                    }

                    if (pisteFound) {
                        atterrissageButton.setDisable(true);
                        decollageButton.setDisable(false);
                        String message = String.format("Pilote : Ici %s de %s, je vais atterrir veuillez m'accorder une piste !", selectedAvion, selectedCompagnie);

                        GuiEvent event = new GuiEvent(this, 1);
                        event.addParameter(message);
                        event.addParameter(nextPisteNumber);
                        event.addParameter(selectedCompagnie);
                        event.addParameter(selectedAvion);
                        avionAgent.onGuiEvent(event);
                    } else {
                        logMessage("Erreur : Toutes les pistes sont occupées.");
                    }
                } catch (SQLException ex) {
                    logMessage("Erreur lors de l'atterrissage : " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

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

        // Populate select1 with compagnie values
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT DISTINCT compagnie FROM allocation");
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<String> compagnies = FXCollections.observableArrayList();
            while (resultSet.next()) {
                compagnies.add(resultSet.getString("compagnie"));
            }
            select1.setItems(compagnies);
        } catch (SQLException e) {
            logMessage("Erreur lors du chargement des compagnies : " + e.getMessage());
            e.printStackTrace();
        }

        // Handle select1 selection event
        select1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String selectedCompagnie = newValue;
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT avion FROM allocation WHERE compagnie = ?");
                    preparedStatement.setString(1, selectedCompagnie);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ObservableList<String> avions = FXCollections.observableArrayList();
                    while (resultSet.next()) {
                        avions.add(resultSet.getString("avion"));
                    }
                    select2.setItems(avions);
                } catch (SQLException e) {
                    logMessage("Erreur lors du chargement des avions : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                decollageButton.setDisable(true);
                atterrissageButton.setDisable(true);
            }
        });

        // Handle select2 selection event
        select2.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    String selectedAvion = newValue;
                    String selectedCompagnie = select1.getValue();
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT arrive FROM allocation WHERE compagnie = ? AND avion = ?");
                    preparedStatement.setString(1, selectedCompagnie);
                    preparedStatement.setString(2, selectedAvion);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String arriveStatus = resultSet.getString("arrive");
                        if (arriveStatus.equals("Arrivée")) {
                            decollageButton.setDisable(false);
                            atterrissageButton.setDisable(true);
                        } else if (arriveStatus.equals("Départ")) {
                            decollageButton.setDisable(true);
                            atterrissageButton.setDisable(false);
                        } else {
                            decollageButton.setDisable(true);
                            atterrissageButton.setDisable(true);
                        }
                    }
                } catch (SQLException e) {
                    logMessage("Erreur lors de la vérification du statut : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                decollageButton.setDisable(true);
                atterrissageButton.setDisable(true);
            }
        });

        // Timeline pour rafraîchir l'UI
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            refreshUI(select1, select2, decollageButton, atterrissageButton);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Scene scene = new Scene(mainPane, 1200, 700);
        stage.setTitle("Contrôle Aérien - Avion");
        stage.setScene(scene);
        return stage;
    }

    private VBox createLabeledFieldWithIcon(String labelText, Label icon, Control control) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        HBox fieldBox = new HBox(5, icon, label);
        VBox box = new VBox(5, fieldBox, control);
        return box;
    }

    private void refreshUI(ChoiceBox<String> select1, ChoiceBox<String> select2, Button decollageButton, Button atterrissageButton) {
        try {
            ResultSet resultSet = null;
            PreparedStatement preparedStatement;

            String selectedCompagnie = select1.getValue();
            String selectedAvion = select2.getValue();

            preparedStatement = connection.prepareStatement("SELECT DISTINCT compagnie FROM allocation");
            resultSet = preparedStatement.executeQuery();
            ObservableList<String> compagnies = FXCollections.observableArrayList();
            while (resultSet.next()) {
                compagnies.add(resultSet.getString("compagnie"));
            }

            for (String compagnie : compagnies.toArray(new String[0])) {
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) as count FROM allocation WHERE compagnie = ?");
                preparedStatement.setString(1, compagnie);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    if (count == 0) {
                        compagnies.remove(compagnie);
                    }
                }
            }
            select1.setItems(compagnies);

            if (selectedCompagnie != null && compagnies.contains(selectedCompagnie)) {
                select1.setValue(selectedCompagnie);
            }

            if (selectedCompagnie != null) {
                preparedStatement = connection.prepareStatement("SELECT avion FROM allocation WHERE compagnie = ?");
                preparedStatement.setString(1, selectedCompagnie);
                resultSet = preparedStatement.executeQuery();
                ObservableList<String> avions = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    avions.add(resultSet.getString("avion"));
                }
                select2.setItems(avions);

                if (selectedAvion != null && avions.contains(selectedAvion)) {
                    select2.setValue(selectedAvion);
                }
            }

            if (selectedCompagnie != null && selectedAvion != null) {
                preparedStatement = connection.prepareStatement("SELECT arrive FROM allocation WHERE compagnie = ? AND avion = ?");
                preparedStatement.setString(1, selectedCompagnie);
                preparedStatement.setString(2, selectedAvion);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String arriveStatus = resultSet.getString("arrive");
                    if (arriveStatus.equals("Arrivée")) {
                        decollageButton.setDisable(false);
                        atterrissageButton.setDisable(true);
                    } else if (arriveStatus.equals("Départ")) {
                        decollageButton.setDisable(true);
                        atterrissageButton.setDisable(false);
                    } else {
                        decollageButton.setDisable(true);
                        atterrissageButton.setDisable(true);
                    }
                }
            }
        } catch (SQLException e) {
            logMessage("Erreur lors du rafraîchissement de l'UI : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void StartContainer() throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profileImpl);
        AgentController agentController = container.createNewAgent("Avion", "Avion.AvionAgent", new Object[]{this});
        agentController.start();
    }

    public void setAvionAgent(AvionAgent avionAgent) {
        this.avionAgent = avionAgent;
    }

    public void logMessage(String aclMessage) {
        Platform.runLater(() -> {
            observableList.add(aclMessage);
        });
    }
}