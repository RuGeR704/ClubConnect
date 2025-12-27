package Application.GestioneGruppo;

import java.io.Serializable;

public abstract class GruppoBean implements Serializable {
    private int id_gruppo;
    private String nome;
    private String descrizione;
    private String logo;
    private String sede;
    private String settore;
    private String regole;
    private String slogan;
    private boolean stato;
    private boolean tipoGruppo; //true=club

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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getSettore() {
        return settore;
    }

    public void setSettore(String settore) {
        this.settore = settore;
    }

    public String getRegole() {
        return regole;
    }

    public void setRegole(String regole) {
        this.regole = regole;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public boolean isStato() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public boolean isTipoGruppo() {
        return tipoGruppo;
    }

    public void setTipoGruppo(boolean tipoGruppo) {
        this.tipoGruppo = tipoGruppo;
    }
}
