package Application.GestioneAccount;

import Storage.ConPool;
import Storage.UtenteDAO;

import java.sql.Connection;
import java.sql.SQLException;
public class UtenteService {
        private UtenteDAO utenteDAO;

        // Costruttore vuoto (usato dalla Servlet in produzione)
        public UtenteService() {
            this.utenteDAO = new UtenteDAO();
        }

        // Costruttore per i TEST (Dependency Injection)
        // Ci permette di passare un DAO finto (Mock) quando testiamo
        public UtenteService(UtenteDAO utenteDAO) {
            this.utenteDAO = utenteDAO;
        }

        /**
         * Gestisce il login dell'utente.
         * @return UtenteBean se login ok, null altrimenti.
         */
        public UtenteBean login(String email, String password) {
            try (Connection con = ConPool.getConnection()) {
                return utenteDAO.DoRetrieveEmailPassword(con, email, password);
            } catch (SQLException e) {
                e.printStackTrace(); // In futuro usa un Logger
                return null;
            }
        }

        /**
         * Registra un nuovo utente.
         * @return true se successo, false se fallisce (es. email duplicata)
         * @throws SQLException se c'è un errore grave di DB che la servlet deve gestire (opzionale)
         */
        public void registraUtente(UtenteBean utente) throws SQLException {
            try (Connection con = ConPool.getConnection()) {

                // 1. Controllo Username Duplicato
                // Nota: Assicurati che il tuo DAO abbia il metodo che accetta la Connection!
                if (utenteDAO.doRetrieveByUsername(con, utente.getUsername()) != null) {
                    throw new IllegalArgumentException("username risulta già registrato");
                }

                // 2. Controllo Email Duplicata
                if (utenteDAO.doRetrieveByEmail(con, utente.getEmail()) != null) {
                    throw new IllegalArgumentException("email risulta già registrata");
                }

                // 3. Salvataggio
                utenteDAO.doSave(con, utente);
            }
        }
    }
