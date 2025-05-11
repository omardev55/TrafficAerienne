import gui.MainApp;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;

public class Main {
    public static ContainerController containerController;

    public static void main(String[] args) {
        try {
            // Démarrer JADE
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            containerController = rt.createMainContainer(p);

            // Lancer l'interface graphique
            MainApp.launchApp(app -> {
                System.out.println("Interface lancée avec succès !");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}