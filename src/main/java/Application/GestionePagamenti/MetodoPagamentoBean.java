package Application.GestionePagamenti;

import java.io.Serializable;

public class MetodoPagamentoBean implements Serializable {
    private int id_metodo;
    private int id_utente;
    private String nome_intestatario;
    private String cognome_intestatario;
    private String numero_carta;
    private String scadenza_carta;

    public int getId_metodo() {
        return id_metodo;
    }

    public void setId_metodo(int id_metodo) {
        this.id_metodo = id_metodo;
    }

    public int getId_utente() {
        return id_utente;
    }

    public void setId_utente(int id_utente) {
        this.id_utente = id_utente;
    }

    public String getNome_intestatario() {
        return nome_intestatario;
    }

    public void setNome_intestatario(String nome_intestatario) {
        this.nome_intestatario = nome_intestatario;
    }

    public String getCognome_intestatario() {
        return cognome_intestatario;
    }

    public void setCognome_intestatario(String cognome_intestatario) {
        this.cognome_intestatario = cognome_intestatario;
    }

    public String getNumero_carta() {
        return numero_carta;
    }

    public void setNumero_carta(String numero_carta) {
        this.numero_carta = numero_carta;
    }

    public String getScadenza_carta() {
        return scadenza_carta;
    }

    public void setScadenza_carta(String scadenza_carta) {
        this.scadenza_carta = scadenza_carta;
    }

}
