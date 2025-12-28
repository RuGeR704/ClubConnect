package Application.GestioneGruppo;
import java.io.Serializable;

public class ClubBean extends GruppoBean implements Serializable {
    private double importo_retta;
    private int frequenza;

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
