package Application.GestioneComunicazioni;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;

public class ComunicazioniBean implements Serializable {
    private int id_comunicazione;
    private int id_gruppo;
    private int id_autore;
    private String contenuto;
    private String foto;
    private Date dataPubblicazione;
    private boolean isglobal;

    public int getId_comunicazione() {
        return id_comunicazione;
    }

    public void setId_comunicazione(int id_comunicazione) {
        this.id_comunicazione = id_comunicazione;
    }

    public int getId_gruppo() {
        return id_gruppo;
    }

    public void setId_gruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
    }

    public int getId_autore() {
        return id_autore;
    }

    public void setId_autore(int id_autore) {
        this.id_autore = id_autore;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Date getDataPubblicazione() { return dataPubblicazione; }

    public void setDataPubblicazione(Date dataPubblicazione) {
        this.dataPubblicazione = dataPubblicazione;
    }

    public boolean isIsglobal() {
        return isglobal;
    }

    public void setIsglobal(boolean isglobal) {
        this.isglobal = isglobal;
    }
}
