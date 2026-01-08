package Application.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import java.util.List;

public class GestioneSistemaProxy implements GestioneSistemaInterface {

    private GestioneSistemaBean realSubject;
    private UtenteBean utenteCorrente;

    public GestioneSistemaProxy(UtenteBean utente) {
        this.utenteCorrente = utente;
        this.realSubject = new GestioneSistemaBean();
    }

    private void checkAdmin() {
        // Controllo basato su UtenteDAO (isAdmin)
        if (utenteCorrente == null || !utenteCorrente.isAdmin()) {
            throw new SecurityException("Accesso Negato: Richiesti privilegi di Amministratore.");
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
        // La lettura dello stato è pubblica, ma se vogliamo restringerla: checkAdmin()
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