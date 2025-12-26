package Application.GestioneGruppo;

import java.io.Serializable;

public class AssociazioneBean extends GruppoBean implements Serializable{
    private int id_gruppo;

    public int getId_gruppo() {
        return id_gruppo;
    }

    public void setId_gruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
    }
}
