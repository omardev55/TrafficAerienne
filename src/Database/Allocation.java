package Database;

public class Allocation {
    private String compagnie;
    private String avion;
    private String pilote;
    private String date;
    private int heure;
    private int minute;
    private String arrive;
    private String piste;
    public Allocation(String compagnie, String avion, String pilote, String date, int heure, int minute, String arrive, String piste) {
        this.compagnie = compagnie;
        this.avion = avion;
        this.pilote = pilote;
        this.date = date;
        this.heure = heure;
        this.minute = minute;
        this.arrive = arrive;
        this.piste = piste;
    }
    // Getters
    public String getCompagnie() {
        return compagnie;
    }

    public String getAvion() {
        return avion;
    }

    public String getPilote() {
        return pilote;
    }

    public String getDate() {
        return date;
    }

    public int getHeure() {
        return heure;
    }
    public String getHeureAtterissage() {
        return heure + " : " + minute; // Concatenate heure and minute with a colon separator
    }

    public int getMinute() {
        return minute;
    }

    public String getArrive() {
        return arrive;
    }

    public String getPiste() {
        return piste;
    }
    // Setters
    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }

    public void setAvion(String avion) {
        this.avion = avion;
    }

    public void setPilote(String pilote) {
        this.pilote = pilote;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeure(int heure) {
        this.heure = heure;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }
    public void setPiste(String piste) {
        this.piste = piste;
    }
}