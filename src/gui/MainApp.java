package gui;

import Avion.AvionUI;
import Directeur_Flux.DirecteurFluxUI;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class MainApp extends Application {
    private static CompagnieUI compagnieUI;
    private static DirecteurFluxUI directeurFluxUI;
    private static AvionUI avionUI;
    private static Consumer<MainApp> onInitializationComplete;

    @Override
    public void start(Stage primaryStage) throws ControllerException, StaleProxyException {
        compagnieUI = new CompagnieUI();
        directeurFluxUI = new DirecteurFluxUI();
        avionUI = new AvionUI();

        // Afficher les interfaces
        Stage compagnieStage = compagnieUI.createStage();
        compagnieStage.show();

        Stage directeurFluxStage = directeurFluxUI.createStage();
        directeurFluxStage.show();

        Stage avionStage = avionUI.createStage();
        avionStage.show();

        // Appeler le callback une fois que tout est initialisé
        if (onInitializationComplete != null) {
            onInitializationComplete.accept(this);
        }
    }

    public static void launchApp(Consumer<MainApp> callback) {
        onInitializationComplete = callback;
        new Thread(() -> Application.launch(MainApp.class)).start();
    }

    // Getters pour accéder aux instances des interfaces
    public CompagnieUI getCompagnieUI() {
        return compagnieUI;
    }

    public DirecteurFluxUI getDirecteurFluxUI() {
        return directeurFluxUI;
    }

    public AvionUI getAvionUI() {
        return avionUI;
    }
}