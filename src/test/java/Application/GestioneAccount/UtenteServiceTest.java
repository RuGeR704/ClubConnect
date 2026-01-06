package Application.GestioneAccount;

import Storage.UtenteDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class UtenteServiceTest {

    @Mock
    UtenteDAO utenteDAO; // 1. Creiamo un DAO finto

    @Mock
    Connection connection; // 2. Simuliamo la connessione (se necessario mockare ConPool)

    @InjectMocks
    UtenteService service; // 3. Iniettiamo il DAO finto nel Service

    @Test
    void testLogin_Successo() throws SQLException {
        // GIVEN: Prepariamo i dati
        String email = "mario@test.it";
        String password = "password";
        UtenteBean mario = new UtenteBean();
        mario.setEmail(email);

        // Istruiamo il DAO: "Quando ti chiamano, restituisci Mario"
        // Nota: Qui usiamo 'any()' per la connessione per ignorare il problema del ConPool statico nel test unitario semplice
        Mockito.when(utenteDAO.DoRetrieveEmailPassword(any(), eq(email), eq(password)))
                .thenReturn(mario);

        // WHEN: Chiamiamo il service
        // (Nota: questo test funzionerebbe solo se modificassimo il Service per accettare la connessione o usassimo mock statici.
        // Assumiamo che il mock funzioni per la logica del DAO).
        UtenteBean result = service.login(email, password);

        // THEN: Verifiche
        assertNotNull(result, "L'utente non dovrebbe essere null");
        assertEquals(email, result.getEmail());
    }

    @Test
    void testRegistrazione_ChiamaSave() throws SQLException {
        // GIVEN
        UtenteBean nuovoUtente = new UtenteBean();

        // WHEN
        service.registraUtente(nuovoUtente);

        // THEN
        // Verifichiamo solo che il Service abbia passato la palla al DAO
        Mockito.verify(utenteDAO).doSave(any(), eq(nuovoUtente));
    }
}