package Presentation.GestionePagamenti;

import Application.GestioneAccount.AccountService;
import Application.GestionePagamenti.MetodoPagamentoBean;
import Application.GestioneAccount.UtenteBean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AggiungiMetodoPagamentoServletTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock AccountService serviceMock;

    AggiungiMetodoPagamentoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new AggiungiMetodoPagamentoServlet();
        servlet.setAccountService(serviceMock);

        when(request.getSession(false)).thenReturn(session);
        UtenteBean utente = new UtenteBean();
        utente.setId_utente(1);
        when(session.getAttribute("utente")).thenReturn(utente);

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    /**
     * Test Parametrico per Validazione Input (Category Partition).
     * Nota: Il parametro data ha il typo "scandenza_carta" come nella tua Servlet.
     */
    @ParameterizedTest(name = "{0}: {5}")
    @CsvSource(delimiter = ';', value = {
            // TC1: Nome vuoto
            "TC_AddPay_1; ; Rossi; 1234567812345678; 12/2030; Nome intestatario obbligatorio",
            // TC2: Numero Carta formato errato
            "TC_AddPay_2; Mario; Rossi; 123; 12/2030; Numero carta non valido",
            // TC3: Scadenza formato errato
            "TC_AddPay_3; Mario; Rossi; 1234567812345678; 2030-12; Formato scadenza non valido",
            // TC4: Carta Scaduta (Data passata)
            "TC_AddPay_4; Mario; Rossi; 1234567812345678; 01/2020; La carta è scaduta"
    })
    void testAggiungiMetodo_ErroriValidazione(String id, String nome, String cogn, String num, String scad, String errMsg) throws Exception {
        when(request.getParameter("nome_intestatario")).thenReturn(nome);
        when(request.getParameter("cognome_intestatario")).thenReturn(cogn);
        when(request.getParameter("numero_carta")).thenReturn(num);
        when(request.getParameter("scandenza_carta")).thenReturn(scad);

        servlet.doPost(request, response);

        // Verifica che venga settato l'attributo di errore e fatto il forward
        verify(request).setAttribute(eq("errorMsg"), contains(errMsg));
        verify(request.getRequestDispatcher("aggiungi_metodo.jsp")).forward(request, response);

        // Verifica che il service NON venga chiamato
        verify(serviceMock, never()).aggiungiMetodoPagamento(any());
    }

    @Test
    void testAggiungiMetodo_Successo() throws Exception {
        // Generiamo una data futura valida (es. tra 2 anni)
        String annoFuturo = String.valueOf(LocalDate.now().getYear() + 2);
        String dataFutura = "12/" + annoFuturo;

        when(request.getParameter("nome_intestatario")).thenReturn("Mario");
        when(request.getParameter("cognome_intestatario")).thenReturn("Rossi");
        when(request.getParameter("numero_carta")).thenReturn("1234567812345678");
        when(request.getParameter("scandenza_carta")).thenReturn(dataFutura);

        servlet.doPost(request, response);

        // Verifica chiamata al service e redirect
        verify(serviceMock).aggiungiMetodoPagamento(any(MetodoPagamentoBean.class));
        verify(response).sendRedirect("VisualizzaMetodidiPagamentoServlet");
    }
}