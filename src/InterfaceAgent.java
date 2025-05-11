package gui;

import Avion.AvionUI;
import Directeur_Flux.DirecteurFluxUI;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;

public class InterfaceAgent extends Agent {
    private CompagnieUI compagnieUI;
    private DirecteurFluxUI directeurFluxUI;
    private AvionUI avionUI;

    protected void setup() {
        // Lancer l'application JavaFX et attendre que les interfaces soient prêtes
        MainApp.launchApp(mainApp -> {
            Platform.runLater(() -> {
                // Récupérer les instances des interfaces
                compagnieUI = mainApp.getCompagnieUI();
                directeurFluxUI = mainApp.getDirecteurFluxUI();
                avionUI = mainApp.getAvionUI();

                // Configurer les callbacks
                setupCallbacks();
            });
        });
    }

    private void setupCallbacks() {
        // Configurer le callback pour CompagnieUI
        compagnieUI.setOnDemandeSubmitted(input -> {
            // Le message est déjà au format "DemandeCréneau(heure, piste, avion)"
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("CompagnieAerienneA", AID.ISLOCALNAME));
            msg.setContent(input);
            send(msg);

            String logMessage = "Demande envoyée : " + input;
            Platform.runLater(() -> {
                compagnieUI.logMessage(logMessage);
                directeurFluxUI.logMessage("Demande reçue : " + input);
                avionUI.logMessage("Demande de créneau : " + input);
            });
        });

        // Comportement pour recevoir les messages
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.INFORM) {
                        if (msg.getContent().startsWith("CréneauAttribué")) {
                            String[] parts = msg.getContent().split(",");
                            String heure = parts[0].split("\\(")[1];
                            String piste = parts[1].trim();
                            String avion = parts[2].split("\\)")[0].trim();

                            String logMessage = "Créneau attribué pour " + avion + " à " + heure + " sur " + piste;
                            Platform.runLater(() -> {
                                compagnieUI.logMessage(logMessage);
                                directeurFluxUI.logMessage(logMessage);
                                avionUI.logMessage(logMessage);
                            });
                        } else if (msg.getContent().startsWith("Log")) {
                            String logMessage = msg.getContent().replace("Log", "");
                            Platform.runLater(() -> {
                                compagnieUI.logMessage(logMessage);
                                directeurFluxUI.logMessage(logMessage);
                                avionUI.logMessage(logMessage);
                            });
                        } else if (msg.getContent().startsWith("Atterrissage")) {
                            String[] parts = msg.getContent().split(",");
                            String message = parts[0].split(":")[1].trim();
                            String piste = parts.length > 1 ? parts[1].trim() : "N/A";
                            String compagnie = parts.length > 2 ? parts[2].trim() : "N/A";
                            String avion = parts.length > 3 ? parts[3].split("\\)")[0].trim() : "N/A";

                            String logMessage = message + " - Piste attribuée : " + piste;
                            Platform.runLater(() -> {
                                compagnieUI.logMessage("Atterrissage : " + logMessage);
                                directeurFluxUI.logMessage("Atterrissage : " + logMessage);
                                avionUI.logMessage(logMessage);
                            });
                        } else if (msg.getContent().startsWith("Décollage")) {
                            String[] parts = msg.getContent().split(",");
                            String message = parts[0].split(":")[1].trim();
                            String compagnie = parts.length > 1 ? parts[1].trim() : "N/A";
                            String avion = parts.length > 2 ? parts[2].split("\\)")[0].trim() : "N/A";

                            String logMessage = message;
                            Platform.runLater(() -> {
                                compagnieUI.logMessage("Décollage : " + logMessage);
                                directeurFluxUI.logMessage("Décollage : " + logMessage);
                                avionUI.logMessage(logMessage);
                            });
                        }
                    } else if (msg.getPerformative() == ACLMessage.PROPOSE && msg.getContent().startsWith("ProposerCréneauAlternatif")) {
                        String[] parts = msg.getContent().split(",");
                        String heure = parts[0].split("\\(")[1];
                        String piste = parts[1].trim();
                        String avion = parts[2].split("\\)")[0].trim();

                        String logMessage = "Proposition de créneau alternatif pour " + avion + " à " + heure + " sur " + piste;
                        Platform.runLater(() -> {
                            compagnieUI.logMessage(logMessage);
                            directeurFluxUI.logMessage(logMessage);
                            avionUI.logMessage(logMessage);
                        });
                    } else if (msg.getPerformative() == ACLMessage.REFUSE) {
                        String logMessage = "Demande refusée : " + msg.getContent();
                        Platform.runLater(() -> {
                            compagnieUI.logMessage(logMessage);
                            directeurFluxUI.logMessage(logMessage);
                            avionUI.logMessage(logMessage);
                        });
                    }
                } else {
                    block();
                }
            }
        });
    }
}