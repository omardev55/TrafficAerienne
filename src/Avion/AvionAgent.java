package Avion;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AvionAgent extends GuiAgent {

    //gui: objet pour loger les messages de l'agent Pilote
    private transient AvionUI gui;

    @Override
    protected void setup() {
        if (getArguments().length==1){
            gui= (AvionUI) getArguments()[0];
            gui.setAvionAgent(this);
        }

        // donner un comportement au agent
        ParallelBehaviour parallelBehaviour =new ParallelBehaviour();
        addBehaviour(parallelBehaviour);


        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.MatchAll();
                ACLMessage aclMessage = receive(messageTemplate);

                if (aclMessage!=null){
                    switch(aclMessage.getPerformative()){
                        case ACLMessage.INFORM:
                            String content = aclMessage.getContent();
                            String[] parts = content.split(";");
                            String originalcontent = parts[0];
                            gui.logMessage(originalcontent);
                            break;



                        default:
                            break;
                    }
                }
            }
        });



    }

    @Override
    public void onGuiEvent(GuiEvent message) {
        int messageType = message.getType();
        String content = message.getParameter(0).toString();

        ACLMessage msgToDir = new ACLMessage(ACLMessage.REQUEST);
        msgToDir.setContent(content);
        msgToDir.addReceiver(new AID("DIRECTEURFLUX", AID.ISLOCALNAME));
        if (messageType == 1){
            msgToDir.setPerformative(1);
            gui.logMessage(content);
            int nbrpiste = (int) message.getParameter(1);
            String Compagnie =  message.getParameter(2).toString();
            String Avion =  message.getParameter(3).toString();
            String contentpiste = content +";"+nbrpiste+";"+Compagnie+";"+Avion;
            msgToDir.setContent(contentpiste);
            send(msgToDir);
        }
        if (messageType == 2){
            msgToDir.setPerformative(2);
            gui.logMessage(content);
            String Compagnie =  message.getParameter(1).toString();
            String Avion =  message.getParameter(2).toString();
            String contentpiste = content +";"+Compagnie+";"+Avion;
            msgToDir.setContent(contentpiste);
            send(msgToDir);
        }




    }
}