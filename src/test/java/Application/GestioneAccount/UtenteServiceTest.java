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
    Connection connection; // 2. Simuliamo la connessione

    @InjectMocks
    UtenteService service; // 3. Iniettiamo il DAO finto nel Service

    @Test
    void testLogin_Successo() throws SQLException {
        // Prepariamo i dati
        String email = "mario@test.it";
        String password = "password";
        UtenteBean mario = new UtenteBean();
        mario.setEmail(email);

        // Istruiamo il DAO
        Mockito.when(utenteDAO.DoRetrieveEmailPassword(any(), eq(email), eq(password)))
                .thenReturn(mario);

        // WHEN: Chiamiamo il service
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
        Mockito.verify(utenteDAO).doSave(any(), eq(nuovoUtente));
    }
}