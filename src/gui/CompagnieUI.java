package gui;

import javafx.animation.FadeTransition;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class CompagnieUI {
    private ObservableList<String> logList = FXCollections.observableArrayList();
    private ListView<String> conversationList;
    private Consumer<String> onDemandeSubmitted;
    private Connection connection;

    // Initialiser la connexion à la base de données
    private boolean initializeConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/Allocation?useSSL=false&serverTimezone=UTC";
            String user = "root"; // À remplacer par vos identifiants
            String password = ""; // À remplacer par vos identifiants
            connection = DriverManager.getConnection(url, user, password);
            logMessage("Connexion à la base de données établie.");
            return true;
        } catch (SQLException e) {
            logMessage("Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Stage createStage() {
        Stage stage = new Stage();
        BorderPane mainPane = new BorderPane();

        // Ajouter une icône à la fenêtre
        try {
            Image iconstage = new Image(getClass().getResourceAsStream("/images/compagnie-aerienne.png"));
            stage.getIcons().add(iconstage);
        } catch (Exception e) {
            logMessage("Icône non trouvée, veuillez vérifier le chemin de 'compagnie-aerienne.png'.");
        }

        // Initialiser la connexion à la base de données
        initializeConnection();

        // Barre de navigation supérieure
        Label title = new Label("Contrôle Aérien - Compagnie");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Bouton de retour/déconnexion
        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        backButton.setOnAction(e -> stage.close()); // Ferme la fenêtre pour l'exemple

        HBox navBar = new HBox(20, title, backButton);
        navBar.setStyle("-fx-background-color: #1E3A8A; -fx-padding: 15px;");
        navBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);
        mainPane.setTop(navBar);

        // Zone centrale : Formulaire à gauche, Image et Journal à droite
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(15));

        // Formulaire à gauche
        VBox formContent = new VBox(10);
        formContent.setPadding(new Insets(15));
        formContent.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");

        // Envelopper le formulaire dans un ScrollPane
        ScrollPane formScrollPane = new ScrollPane(formContent);
        formScrollPane.setFitToWidth(true);
        formScrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
        formScrollPane.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.55)); // 55% de la largeur

        // Titre du formulaire
        Label formTitle = new Label("Nouvelle Demande de Vol");
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        // Séparateur
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: #4B5563;");

        // Section "Informations de vol"
        Label infoSection = new Label("Informations de Vol");
        infoSection.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        TextField nomCompagnieField = createStyledTextField("Nom de la compagnie");
        TextField avionField = createStyledTextField("Modèle de l'avion");
        TextField piloteField = createStyledTextField("Nom du pilote");

        // Section "Horaire"
        Label scheduleSection = new Label("Horaire");
        scheduleSection.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Sélectionnez une date");
        datePicker.setStyle("-fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 0);
        hourSpinner.setEditable(true);
        hourSpinner.setStyle("-fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setEditable(true);
        minuteSpinner.setStyle("-fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        ComboBox<String> arriveComboBox = new ComboBox<>();
        arriveComboBox.getItems().addAll("Départ", "Arrivée");
        arriveComboBox.setValue("Départ");
        arriveComboBox.setStyle("-fx-font-size: 14px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Bouton Soumettre
        Button confirmButton = new Button("Soumettre la Demande");
        confirmButton.setStyle("-fx-background-color: #F97316; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        // Effets sur le bouton
        DropShadow shadow = new DropShadow(10, Color.gray(0.4));
        confirmButton.setOnMouseEntered(e -> {
            confirmButton.setCursor(Cursor.HAND);
            confirmButton.setEffect(shadow);
            confirmButton.setScaleX(1.05);
            confirmButton.setScaleY(1.05);
        });
        confirmButton.setOnMouseExited(e -> {
            confirmButton.setCursor(Cursor.DEFAULT);
            confirmButton.setEffect(null);
            confirmButton.setScaleX(1.0);
            confirmButton.setScaleY(1.0);
        });

        // Ajouter des "icônes" (simulées par des labels pour l'instant)
        Label compagnieIcon = new Label("✈️ ");
        Label avionIcon = new Label("🛩️ ");
        Label piloteIcon = new Label("👨‍✈️ ");
        Label dateIcon = new Label("📅 ");
        Label heureIcon = new Label("⏰ ");
        Label minutesIcon = new Label("⏳ ");
        Label typeIcon = new Label("↔️ ");

        // Ajouter les champs au formulaire avec icônes
        formContent.getChildren().addAll(
                formTitle,
                separator1,
                infoSection,
                createLabeledFieldWithIcon("Compagnie", compagnieIcon, nomCompagnieField),
                createLabeledFieldWithIcon("Avion", avionIcon, avionField),
                createLabeledFieldWithIcon("Pilote", piloteIcon, piloteField),
                new Separator(),
                scheduleSection,
                createLabeledFieldWithIcon("Date", dateIcon, datePicker),
                createLabeledFieldWithIcon("Heure", heureIcon, hourSpinner),
                createLabeledFieldWithIcon("Minutes", minutesIcon, minuteSpinner),
                createLabeledFieldWithIcon("Type de vol", typeIcon, arriveComboBox),
                confirmButton
        );
        formContent.setAlignment(Pos.TOP_CENTER);

        // Zone de droite : Image en haut, Journal en bas
        VBox rightBox = new VBox(10);
        rightBox.prefWidthProperty().bind(mainPane.widthProperty().multiply(0.4)); // 40% de la largeur

        // Image en haut
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(rightBox.widthProperty().multiply(0.9)); // 90% de la largeur de rightBox
        imageView.setFitHeight(250); // Augmenté de 200 à 250 pixels
        try {
            Image planeImage = new Image(getClass().getResourceAsStream("/images/plane.jpg"));
            imageView.setImage(planeImage);
        } catch (Exception e) {
            logMessage("Image non trouvée, veuillez vérifier le chemin de 'plane.jpg'.");
        }
        StackPane imagePane = new StackPane(imageView);
        imagePane.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        imagePane.setPadding(new Insets(10));
        imagePane.setEffect(new DropShadow(5, Color.gray(0.3)));

        // Journal en bas
        logList = FXCollections.observableArrayList();
        conversationList = new ListView<>(logList);
        conversationList.setStyle("-fx-background-color: white; -fx-border-color: #4B5563; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        conversationList.prefHeightProperty().bind(mainPane.heightProperty().multiply(0.4)); // 40% de la hauteur pour le journal

        // Personnaliser les cellules du ListView avec des fonds colorés
        conversationList.setCellFactory(listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 15px; -fx-padding: 8px;"); // Police augmentée à 15px
                    if (item.contains("Erreur")) {
                        setTextFill(Color.RED);
                        setStyle(getStyle() + "-fx-background-color: #FFEBEE;"); // Fond rouge pâle
                    } else if (item.contains("succès")) {
                        setTextFill(Color.web("#10B981"));
                        setStyle(getStyle() + "-fx-background-color: #E6FFFA;"); // Fond vert pâle
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

        // Faire défiler automatiquement vers le dernier message
        logList.addListener((ListChangeListener<String>) change -> {
            conversationList.scrollTo(logList.size() - 1);
        });

        VBox journalBox = new VBox(5, new Label("Journal des Activités"), conversationList);
        journalBox.setStyle("-fx-background-color: #F3F4F6; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-border-color: #4B5563; -fx-border-width: 1px;");
        journalBox.setPadding(new Insets(10));
        journalBox.setEffect(new DropShadow(5, Color.gray(0.3)));

        // Ajouter l'image et le journal à rightBox
        rightBox.getChildren().addAll(imagePane, journalBox);
        rightBox.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(journalBox, Priority.ALWAYS);

        // Ajouter formulaire et zone de droite à la zone centrale
        centerBox.getChildren().addAll(formScrollPane, rightBox);
        HBox.setHgrow(formScrollPane, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        mainPane.setCenter(centerBox);

        // Action du bouton Soumettre
        confirmButton.setOnAction(e -> {
            String compagnie = nomCompagnieField.getText().trim();
            String avion = avionField.getText().trim();
            String pilote = piloteField.getText().trim();
            LocalDate date = datePicker.getValue();
            int heure = hourSpinner.getValue();
            int minutes = minuteSpinner.getValue();
            String arrive = arriveComboBox.getValue();

            // Validation des champs
            if (compagnie.isEmpty() || avion.isEmpty() || date == null || arrive == null) {
                logMessage("Erreur : Veuillez remplir tous les champs obligatoires.");
                return;
            }

            String heureFormatted = String.format("%02d:%02d", heure, minutes);
            String demande = compagnie + "," + heureFormatted + ",Piste1," + avion + "," + arrive;

            // Vérifier les conflits
            boolean alreadyExists = checkIfExists(date.toString(), heure, minutes);
            if (alreadyExists) {
                logMessage("Erreur : Une demande existe déjà pour cette date et heure.");
                return;
            }

            boolean validTimeDifference = checkTimeDifference(date.toString(), heure, minutes);
            if (!validTimeDifference) {
                LocalDateTime proposedTime = LocalDateTime.of(date, java.time.LocalTime.of(heure, minutes)).plusMinutes(20);
                String proposedHeure = proposedTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                logMessage("Conflit détecté : Moins de 20 minutes d'écart avec une autre demande.");
                logMessage("Alternative proposée : " + proposedHeure + " sur Piste1 le " + date + ".");
                return;
            }

            // Enregistrer dans la base de données
            saveToDatabase(compagnie, pilote, avion, date.toString(), heure, minutes, arrive);

            // Ajouter le log
            logMessage("Demande soumise : " + demande);

            // Formater la demande pour l'agent
            String messageForAgent = String.format("DemandeCréneau(%s, Piste1, %s)", heureFormatted.replace(":", "h"), avion);
            if (onDemandeSubmitted != null) {
                onDemandeSubmitted.accept(messageForAgent);
            }
        });

        // Fond dégradé inspiré des pistes d'aéroport
        Stop[] stops = new Stop[]{
                new Stop(0, Color.web("#2D3748")), // Gris anthracite (piste)
                new Stop(1, Color.web("#4A5568"))  // Gris plus clair
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        mainPane.setBackground(new Background(new BackgroundFill(gradient, null, null)));

        // Animation d'ouverture de la fenêtre
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), mainPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        Scene scene = new Scene(mainPane, 1200, 700);
        stage.setTitle("Contrôle Aérien - Compagnie");
        stage.setScene(scene);
        return stage;
    }

    // Créer un champ de texte stylé
    private TextField createStyledTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setStyle("-fx-font-size: 14px; -fx-border-color: #4B5563; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-padding: 8px;");
        Tooltip tooltip = new Tooltip(prompt);
        textField.setTooltip(tooltip);
        return textField;
    }

    // Créer un champ avec un label et une icône
    private VBox createLabeledFieldWithIcon(String labelText, Label icon, Control control) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E3A8A;");
        HBox fieldBox = new HBox(5, icon, label);
        VBox box = new VBox(5, fieldBox, control);
        return box;
    }

    // Vérifier si une demande existe déjà
    private boolean checkIfExists(String date, int heure, int minutes) {
        if (connection == null) {
            logMessage("Erreur : Aucune connexion à la base de données.");
            return false;
        }
        String query = "SELECT COUNT(*) FROM allocation WHERE date = ? AND heure = ? AND minute = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, date);
            statement.setInt(2, heure);
            statement.setInt(3, minutes);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            logMessage("Erreur lors de la vérification : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Vérifier la différence de temps
    private boolean checkTimeDifference(String date, int heure, int minutes) {
        if (connection == null) {
            logMessage("Erreur : Aucune connexion à la base de données.");
            return false;
        }
        String query = "SELECT heure, minute FROM allocation WHERE date = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            int currentMinutes = heure * 60 + minutes;
            while (resultSet.next()) {
                int existingHeure = resultSet.getInt("heure");
                int existingMinutes = resultSet.getInt("minute");
                int existingTotalMinutes = existingHeure * 60 + existingMinutes;
                if (Math.abs(currentMinutes - existingTotalMinutes) < 20) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            logMessage("Erreur lors de la vérification des conflits : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Enregistrer dans la base de données
    private void saveToDatabase(String nomCompagnie, String pilote, String avion, String date, int heure, int minutes, String arrive) {
        if (connection == null) {
            logMessage("Erreur : Aucune connexion à la base de données.");
            return;
        }
        String query = "INSERT INTO allocation (compagnie, avion, pilote, date, heure, minute, arrive, piste) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nomCompagnie);
            statement.setString(2, avion);
            statement.setString(3, pilote);
            statement.setString(4, date);
            statement.setInt(5, heure);
            statement.setInt(6, minutes);
            statement.setString(7, arrive);
            statement.setString(8, "Piste1");
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                logMessage("Données enregistrées avec succès.");
            } else {
                logMessage("Échec de l'enregistrement des données.");
            }
        } catch (SQLException e) {
            logMessage("Erreur lors de l'enregistrement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajouter un message au log
    public void logMessage(String message) {
        Platform.runLater(() -> logList.add(message));
    }

    // Setter pour le callback
    public void setOnDemandeSubmitted(Consumer<String> callback) {
        this.onDemandeSubmitted = callback;
    }
}