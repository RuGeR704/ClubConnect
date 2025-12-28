package Application.GestioneGruppo;

import java.io.Serializable;

public class ClubBean extends GruppoBean implements Serializable {
    private int id_gruppo;
    private double importo_retta;
    private int frequenza;

    public int getId_gruppo() {
        return id_gruppo;
    }

    public void setId_gruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
    }

    public double getImporto_retta() {
        return importo_retta;
    }

    public void setImporto_retta(double importo_retta) {
        this.importo_retta = importo_retta;
    }

    public int getFrequenza() {
        return frequenza;
    }

    public void setFrequenza(int frequenza) {
        this.frequenza = frequenza;
    }

}
