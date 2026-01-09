package Application.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import java.util.List;

public class GestioneSistemaProxy implements GestioneSistemaInterface {

    private GestioneSistemaInterface realSubject; // Cambiato tipo per flessibilità
    private UtenteBean utenteCorrente;

    public GestioneSistemaProxy(UtenteBean utente) {
        this.utenteCorrente = utente;
        this.realSubject = new GestioneSistemaBean();
    }

    // Setter per i TEST
    public void setRealSubject(GestioneSistemaInterface realSubject) {
        this.realSubject = realSubject;
    }

    private void checkAdmin() {
        if (utenteCorrente == null || !utenteCorrente.isAdmin()) {
            throw new SecurityException("Accesso Negato");
        }
    }

    @Override
    public void bannaUtente(int idUtente) {
        checkAdmin();
        realSubject.bannaUtente(idUtente);
    }

    @Override
    public void sbannaUtente(int idUtente) {
        checkAdmin();
        realSubject.sbannaUtente(idUtente);
    }

    @Override
    public void attivaManutenzione() {
        checkAdmin();
        realSubject.attivaManutenzione();
    }

    @Override
    public void disattivaManutenzione() {
        checkAdmin();
        realSubject.disattivaManutenzione();
    }

    @Override
    public boolean isManutenzioneAttiva() {
        return realSubject.isManutenzioneAttiva();
    }

    @Override
    public void sciogliGruppo(int idGruppo) {
        checkAdmin();
        realSubject.sciogliGruppo(idGruppo);
    }

    @Override
    public List<UtenteBean> visualizzaListaClienti() {
        checkAdmin();
        return realSubject.visualizzaListaClienti();
    }
}