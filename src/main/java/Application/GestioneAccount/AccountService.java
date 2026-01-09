package Application.GestioneAccount;

import Application.GestioneGruppo.GruppoBean;
import Application.GestionePagamenti.DettagliPagamentoBean;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Storage.ConPool;
import Storage.UtenteDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountService {

    private UtenteDAO utenteDAO;

    // Costruttore standard
    public AccountService() {
        this.utenteDAO = new UtenteDAO();
    }

    // Costruttore per  TEST (Dependency Injection)
    public AccountService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    // --- METODI DI RECUPERO DATI (READ) ---

    public List<GruppoBean> getGruppiIscritto(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getGruppiIscritto(con, idUtente);
        }
    }
    // Per i test
    public List<GruppoBean> getGruppiIscritto(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveGruppiIscritto(con,idUtente);
    }

    public List<GruppoBean> getGruppiAdmin(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return getGruppiAdmin(con, idUtente);
        }
    }
    // Per i test
    public List<GruppoBean> getGruppiAdmin(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveGruppiAdmin(con, idUtente);
    }

    public List<MetodoPagamentoBean> getMetodiPagamento(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {

            return utenteDAO.doRetrieveAllMetodiPagamento(con, idUtente);
        }
    }

    public List<MetodoPagamentoBean> getMetodiPagamento(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrieveAllMetodiPagamento(con, idUtente);
    }

    public List<DettagliPagamentoBean> getStoricoPagamenti(int idUtente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            return utenteDAO.doRetrievePagamenti(con, idUtente);
        }
    }

    public List<DettagliPagamentoBean> getStoricoPagamenti(Connection con, int idUtente) throws SQLException {
        return utenteDAO.doRetrievePagamenti(con, idUtente);
    }

    // --- METODI DI MODIFICA (UPDATE) ---

    public void modificaDatiUtente(UtenteBean utente) throws SQLException {
        try (Connection con = ConPool.getConnection()) {
            modificaDatiUtente(con, utente);
        }
    }

    // Per i test
    public void modificaDatiUtente(Connection con, UtenteBean utente) throws SQLException {
        // Qui potresti spostare la validazione (date, regex) se volessi pulire la Servlet
        utenteDAO.doUpdate(con, utente);
        // Commit manuale se necessario, ma col try-with-resources spesso basta il close o autocommit
        con.commit();
    }
}