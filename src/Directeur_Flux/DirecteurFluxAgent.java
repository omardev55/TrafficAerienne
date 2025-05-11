package Directeur_Flux;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class DirecteurFluxAgent extends GuiAgent {

    //gui: objet pour loger les messages de l'agent Controlleur
    private transient DirecteurFluxUI gui;


    @Override
    protected void setup() {
        // Initialize the GUI
        gui = (DirecteurFluxUI) getArguments()[0];

        // Add a behavior to receive messages from CompagnieAgent
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Create a template to match messages of type REQUEST
                MessageTemplate template = MessageTemplate.MatchAll();
                ACLMessage msg = receive(template);
                if (msg != null) {
                    // Handle different message types

                    handleRequestMessage(msg);

                    // Add cases for other message types if needed

                } else {
                    // If no message received, block and wait for incoming messages
                    block();
                }
            }
        });


    }
    private void handleRequestMessage(ACLMessage msg) {


        // Formulate a reply based on the message type
        String informergest;
        String replyContent;

        if(msg.getPerformative()==1){
            String content = msg.getContent();
            String[] parts = content.split(";");
            String originalcontent = parts[0];
            String nbrPiste = parts[1];
            String Compagnie = parts[2];
            String Avion  = parts[3];


            gui.logMessage(originalcontent);
            replyContent = String.format("DirecteurFlux : D'accord, la piste N° %s sera libre  , Bon atterissage ! ",nbrPiste);
            informergest = String.format("DirecteurFlux : %s de %s vient d'atterrir ! ",Avion,Compagnie);
            // Create a reply message
            ACLMessage reply = msg.createReply();
            ACLMessage msgToGest = new ACLMessage();
            reply.setPerformative(ACLMessage.INFORM);
            msgToGest.setPerformative(2);
            reply.setContent(replyContent);
            msgToGest.setContent(informergest);
            msgToGest.addReceiver(new AID("GESTIONNAIRE",AID.ISLOCALNAME));
            gui.logMessage(replyContent);
            send(reply);
            gui.logMessage(informergest);
            send(msgToGest);

            // Send the reply
        }
        if(msg.getPerformative()==2){
            String content = msg.getContent();
            String[] parts = content.split(";");
            String originalcontent = parts[0];
            String Compagnie = parts[1];
            String Avion = parts[2];
            gui.logMessage(originalcontent);
            replyContent = "DirecteurFlux : D'accord, votre piste sera libre, Bon Voyage !";
            informergest = String.format("DirecteurFlux : %s de %s vient de décoller ! ",Avion,Compagnie);
            // Create a reply message
            ACLMessage reply = msg.createReply();
            ACLMessage msgToGest = new ACLMessage();
            reply.setPerformative(ACLMessage.INFORM);
            msgToGest.setPerformative(2);
            reply.setContent(replyContent);
            msgToGest.setContent(informergest);
            msgToGest.addReceiver(new AID("GESTIONNAIRE",AID.ISLOCALNAME));
            gui.logMessage(replyContent);
            send(reply);

            gui.logMessage(informergest);
            send(msgToGest);
            // Send the reply

        }
        if(msg.getPerformative()==3){
            String content = msg.getContent();
            gui.logMessage(content);
        }
    }
    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}