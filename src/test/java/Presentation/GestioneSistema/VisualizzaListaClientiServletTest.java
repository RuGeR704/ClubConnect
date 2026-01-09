package Presentation.GestioneSistema;

import Application.GestioneAccount.UtenteBean;
import Application.GestioneSistema.GestioneSistemaInterface;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisualizzaListaClientiServletTest {
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;
    @Mock GestioneSistemaInterface sistemaMock;

    VisualizzaListaClientiServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new VisualizzaListaClientiServlet();
        servlet.setSistemaTest(sistemaMock);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(new UtenteBean());
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testVisualizza_Successo() throws Exception {
        List<UtenteBean> lista = new ArrayList<>();
        when(sistemaMock.visualizzaListaClienti()).thenReturn(lista);

        servlet.doGet(request, response);

        verify(request).setAttribute("listaClienti", lista);
        verify(request).getRequestDispatcher("adminUsers.jsp");
        verify(dispatcher).forward(request, response);
    }
}