package Application.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestioneSistemaProxyTest {

    @Mock GestioneSistemaInterface realSubjectMock;

    @Test
    void testAdmin_AccessoConsentito() {
        // GIVEN: Un utente Admin
        UtenteBean admin = mock(UtenteBean.class);
        when(admin.isAdmin()).thenReturn(true);

        GestioneSistemaProxy proxy = new GestioneSistemaProxy(admin);
        proxy.setRealSubject(realSubjectMock); // Iniettiamo il mock

        // WHEN: L'admin prova a bannare
        proxy.bannaUtente(1);

        // THEN: Il realSubject viene chiamato
        verify(realSubjectMock).bannaUtente(1);
    }

    @Test
    void testUtenteNormale_AccessoNegato() {
        // GIVEN: Un utente normale
        UtenteBean user = mock(UtenteBean.class);
        when(user.isAdmin()).thenReturn(false);

        GestioneSistemaProxy proxy = new GestioneSistemaProxy(user);
        proxy.setRealSubject(realSubjectMock);

        // WHEN & THEN: Deve lanciare eccezione
        assertThrows(SecurityException.class, () -> proxy.bannaUtente(1));

        // Il realSubject non viene chiamato
        verifyNoInteractions(realSubjectMock);
    }

    @Test
    void testUtenteNull_AccessoNegato() {
        // GIVEN: Nessun utente in sessione
        GestioneSistemaProxy proxy = new GestioneSistemaProxy(null);
        proxy.setRealSubject(realSubjectMock);

        assertThrows(SecurityException.class, () -> proxy.visualizzaListaClienti());
        verifyNoInteractions(realSubjectMock);
    }
}