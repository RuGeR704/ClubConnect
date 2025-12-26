package Application.GestioneGruppo;

import java.io.Serializable;
import java.time.LocalDate;

public class IscrizioneBean implements Serializable {
    private int id_gruppo;
    private int id_utente;
    private LocalDate data_iscrizione;
    private boolean gestore;

    public int getId_gruppo() {
        return id_gruppo;
    }

    public void setId_gruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
    }

    public int getId_utente() {
        return id_utente;
    }

    public void setId_utente(int id_utente) {
        this.id_utente = id_utente;
    }

    public LocalDate getData_iscrizione() {
        return data_iscrizione;
    }

    public void setData_iscrizione(LocalDate data_iscrizione) {
        this.data_iscrizione = data_iscrizione;
    }

    public boolean isGestore() {
        return gestore;
    }

    public void setGestore(boolean gestore) {
        this.gestore = gestore;
    }
}
