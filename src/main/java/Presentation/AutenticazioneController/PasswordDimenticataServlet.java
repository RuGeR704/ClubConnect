package Presentation.AutenticazioneController;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/PasswordDimenticataServlet")
public class PasswordDimenticataServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }
}
