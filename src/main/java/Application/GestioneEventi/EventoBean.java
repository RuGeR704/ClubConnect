package Application.GestioneEventi;

import java.io.Serializable;
import java.time.LocalDate;

public class EventoBean implements Serializable {
    private int id_evento;
    private int id_gruppo;
    private String nome;
    private String descrizione;
    private String foto;
    private double costo;
    private int posti_disponibili;
    private int capienza_massima;
    private LocalDate data_ora;

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public int getId_gruppo() {
        return id_gruppo;
    }

    public void setId_gruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public int getPosti_disponibili() {
        return posti_disponibili;
    }

    public void setPosti_disponibili(int posti_disponibili) {
        this.posti_disponibili = posti_disponibili;
    }

    public int getCapienza_massima() {
        return capienza_massima;
    }

    public void setCapienza_massima(int capienza_massima) {
        this.capienza_massima = capienza_massima;
    }

    public LocalDate getData_ora() {
        return data_ora;
    }

    public void setData_ora(LocalDate data_ora) {
        this.data_ora = data_ora;
    }
}
