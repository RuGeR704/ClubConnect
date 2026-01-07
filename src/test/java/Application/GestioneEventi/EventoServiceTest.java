package Application.GestioneEventi;

import Storage.EventoDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock EventoDAO daoMock;
    @Mock Connection connection;
    @InjectMocks EventoService service;

    @Test
    void testCreaEvento() throws SQLException {
        EventoBean evento = new EventoBean();
        service.creaEvento(connection, evento);
        verify(daoMock).DoSave(eq(connection), eq(evento));
    }

    @Test
    void testDiminuisciPosti() throws SQLException {
        EventoBean evento = new EventoBean();
        evento.setPosti_disponibili(10);

        service.diminuisciPosti(connection, evento);

        // Verifica che abbia chiamato l'update e che i posti siano scesi
        assert(evento.getPosti_disponibili() == 9);
        verify(daoMock).DoUpdate(eq(connection), eq(evento));
    }
}