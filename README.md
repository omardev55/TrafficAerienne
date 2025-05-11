Projet SMA : Système Multi-Agents pour la Gestion Aérienne
Description
Ce projet, nommé Projet SMA, est une application Java développée dans le cadre d'un système multi-agents (SMA) pour la gestion du trafic aérien. Il simule les interactions entre différentes entités d'un aéroport (compagnie aérienne, avion, directeur de flux) pour coordonner les décollages et atterrissages. L'application utilise JavaFX pour l'interface graphique, JADE pour la gestion des agents, et une base de données MySQL pour stocker les données.
Fonctionnalités principales

CompagnieUI : Permet à une compagnie aérienne de soumettre des demandes de vol (départ ou arrivée) avec des informations comme la date, l'heure, le pilote, et l'avion.
AvionUI : Interface pour les avions, permettant de demander des pistes pour le décollage ou l'atterrissage.
DirecteurFluxUI : Interface pour le directeur de flux, affichant un tableau des atterrissages et un journal des communications.

Prérequis
Avant de lancer le projet, assurez-vous d'avoir les éléments suivants installés :

Java : JDK 8 ou supérieur (JavaFX est inclus dans JDK 8, sinon il faut l'ajouter séparément pour les versions plus récentes).
MySQL : Une instance MySQL en cours d'exécution.
Maven (optionnel) : Si vous utilisez Maven pour gérer les dépendances.
Git : Pour cloner le projet depuis GitHub.
IDE : Un IDE comme IntelliJ IDEA ou Eclipse pour exécuter le projet.
JADE : Framework pour les systèmes multi-agents (inclus dans les dépendances).

Installation

Cloner le dépôt :
git clone https://github.com/votre-username/Projet-SMA.git
cd Projet-SMA


Configurer la base de données MySQL :

Créez une base de données nommée Allocation :CREATE DATABASE Allocation;


Créez la table allocation avec la structure suivante :CREATE TABLE allocation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compagnie VARCHAR(255),
    avion VARCHAR(255),
    pilote VARCHAR(255),
    date VARCHAR(255),
    heure INT,
    minute INT,
    arrive VARCHAR(255),
    piste VARCHAR(255)
);


Mettez à jour les identifiants de connexion dans les fichiers CompagnieUI.java, AvionUI.java, et DirecteurFluxUI.java (par défaut, utilisateur root et mot de passe vide).


Configurer les ressources :

Assurez-vous que les images suivantes sont dans le dossier src/main/resources/images/ :
compagnie-aerienne.png
pistesoleil.jpg
avionpiste.jpg
plane.jpg


Si elles manquent, remplacez-les par des images de votre choix ou supprimez les références dans le code.


Ouvrir le projet dans un IDE :

Importez le projet dans votre IDE (IntelliJ IDEA, Eclipse, etc.).
Configurez le projet pour utiliser JavaFX et JADE :
Ajoutez les dépendances JADE (par exemple, via Maven ou en ajoutant les JARs).
Assurez-vous que JavaFX est correctement configuré dans votre environnement.




Compiler et exécuter :

Lancez chaque interface séparément :
CompagnieUI pour la gestion des demandes de vol.
AvionUI pour les opérations de décollage et d'atterrissage.
DirecteurFluxUI pour le suivi des atterrissages.





Utilisation

Lancer l'application :

Exécutez les trois classes principales (CompagnieUI, AvionUI, DirecteurFluxUI) dans des processus séparés.
Les agents JADE communiqueront entre eux pour coordonner les actions.


Interface CompagnieUI :

Remplissez les champs (nom de la compagnie, modèle de l'avion, pilote, date, heure, type de vol).
Cliquez sur "Soumettre la Demande" pour envoyer une demande de vol.


Interface AvionUI :

Sélectionnez une compagnie et un avion dans les menus déroulants.
Cliquez sur "Décollage" ou "Atterrissage" selon l'état de l'avion.


Interface DirecteurFluxUI :

Visualisez les atterrissages dans le tableau.
Consultez le journal des communications en bas.



Structure du projet
Projet-SMA/
│
├── src/
│   ├── Avion/
│   │   ├── AvionUI.java
│   │   └── AvionAgent.java
│   ├── Directeur_Flux/
│   │   ├── DirecteurFluxUI.java
│   │   └── DirecteurFluxAgent.java
│   ├── gui/
│   │   └── CompagnieUI.java
│   ├── Database/
│   │   └── Allocation.java
│   └── resources/
│       └── images/
│           ├── compagnie-aerienne.png
│           ├── pistesoleil.jpg
│           ├── avionpiste.jpg
│           └── plane.jpg
└── README.md

Dépendances

JavaFX : Pour les interfaces graphiques.
JADE : Pour la gestion des agents.
MySQL Connector/J : Pour la connexion à la base de données MySQL.

Problèmes connus

Assurez-vous que MySQL est en cours d'exécution avant de lancer l'application.
Les images doivent être présentes dans les ressources, sinon des erreurs de chargement peuvent survenir.
Les identifiants de la base de données doivent être configurés correctement.

Contributions
Les contributions sont les bienvenues ! Si vous souhaitez contribuer :

Forkez le dépôt.
Créez une branche (git checkout -b feature/votre-fonctionnalite).
Faites vos modifications et testez-les.
Commitez vos changements (git commit -m "Ajout de votre-fonctionnalite").
Poussez votre branche (git push origin feature/votre-fonctionnalite).
Créez une Pull Request sur GitHub.

Licence
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails (à créer si nécessaire).
Contact
Pour toute question, contactez-moi via GitHub ou par email à [votre-email@example.com].
