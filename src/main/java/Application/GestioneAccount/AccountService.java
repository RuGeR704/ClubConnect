package Application.GestioneAccount;

import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.ConPool;
import Storage.PagamentoDAO;
import Storage.UtenteDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountService {

    private UtenteDAO utenteDAO;
    private PagamentoDAO pagamentoDAO;

    // Costruttore standard
    public AccountService() {
        this.utenteDAO = new UtenteDAO();
        this.pagamentoDAO = new PagamentoDAO();
    }

    // Setters per Dependency Injection (Test)
    public void setUtenteDAO(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    public void setPagamentoDAO(PagamentoDAO pagamentoDAO) {
        this.pagamentoDAO = pagamentoDAO;
    }

    // ============================================================
    //  SEZIONE GESTIONE PAGAMENTI (Nuovi metodi)
    // ============================================================

    public void aggiungiMetodoPagamento(MetodoPagamentoBean metodo) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            pagamentoDAO.doSaveMetodoPagamento(con, metodo);
        }
    }

    public void rimuoviMetodoPagamento(int idMetodo, int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            pagamentoDAO.doDeleteMetodoPagamento(con, idMetodo, idUtente);
        }
    }

    // ============================================================
    //  SEZIONE DATI UTENTE (Metodi con Overload per i Test)
    // ============================================================

    // --- 1. Gruppi Iscritto ---
    public List<GruppoBean> getGruppiIscritto(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getGruppiIscritto(con, idUtente);
        }
    }
    // OVERLOAD PER I TEST (Risolve il primo errore)
    public List<GruppoBean> getGruppiIscritto(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveGruppiIscritto(con, idUtente);
    }

    // --- 2. Gruppi Admin ---
    public List<GruppoBean> getGruppiAdmin(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getGruppiAdmin(con, idUtente);
        }
    }
    // OVERLOAD PER I TEST (Risolve il secondo errore)
    public List<GruppoBean> getGruppiAdmin(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveGruppiAdmin(con, idUtente);
    }

    // --- 3. Metodi Pagamento (Read) ---
    public List<MetodoPagamentoBean> getMetodiPagamento(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getMetodiPagamento(con, idUtente);
        }
    }
    // OVERLOAD PER I TEST (Risolve il terzo errore)
    public List<MetodoPagamentoBean> getMetodiPagamento(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveAllMetodiPagamento(con, idUtente);
    }

    // --- 4. Storico Pagamenti ---
    public List<DettagliPagamentoBean> getStoricoPagamenti(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getStoricoPagamenti(con, idUtente);
        }
    }
    // OVERLOAD PER I TEST (Risolve il quarto errore)
    public List<DettagliPagamentoBean> getStoricoPagamenti(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrievePagamenti(con, idUtente);
    }

    // --- 5. Modifica Dati ---
    public void modificaDatiUtente(UtenteBean utente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            modificaDatiUtente(con, utente);
        }
    }
    // OVERLOAD PER I TEST (Risolve l'ultimo errore)
    public void modificaDatiUtente(Connection con, UtenteBean utente) throws SQLException {
        utenteDAO.doUpdate(con, utente);
    }
}