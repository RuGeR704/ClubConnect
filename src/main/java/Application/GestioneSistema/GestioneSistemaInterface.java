//definizione interfaccia (il SUBJECT) che garantisce che Proxy e Bean siano intercambiabili
package Application.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import java.util.List;

public interface GestioneSistemaInterface {
    void bannaUtente(int idUtente);
    void sbannaUtente(int idUtente);
    void attivaManutenzione();
    void disattivaManutenzione();
    boolean isManutenzioneAttiva();
    void sciogliGruppo(int idGruppo);
    List<UtenteBean> visualizzaListaClienti();
}