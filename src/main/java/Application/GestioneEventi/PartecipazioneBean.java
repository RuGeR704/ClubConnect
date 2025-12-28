package Application.GestioneEventi;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PartecipazioneBean implements Serializable {
    private int id_partecipazione;
    private int id_evento;
    private int id_utente;
    private LocalDateTime data_registrazione;

    public int getId_partecipazione() {
        return id_partecipazione;
    }

    public void setId_partecipazione(int id_partecipazione) {
        this.id_partecipazione = id_partecipazione;
    }

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public int getId_utente() {
        return id_utente;
    }

    public void setId_utente(int id_utente) {
        this.id_utente = id_utente;
    }

    public LocalDateTime getData_registrazione() {
        return data_registrazione;
    }

    public void setData_registrazione(LocalDateTime data_registrazione) {
        this.data_registrazione = data_registrazione;
    }

}
