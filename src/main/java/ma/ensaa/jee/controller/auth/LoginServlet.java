package ma.ensaa.jee.controller.auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.service.AuthService;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init(){
        this.authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //si deja connecter on le redirige vers son layout
        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute("user") != null){
            Utilisateur user = (Utilisateur) session.getAttribute("user");
            redirigerSelonRole(user,request,response);
            return;
        }
        //sinon on affiche le login
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        try{
            Utilisateur user = authService.authentifier(email,motDePasse);
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            redirigerSelonRole(user,request,response);
        }catch(IllegalArgumentException e){
            request.setAttribute("erreur",e.getMessage());
            request.setAttribute("email",email);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request,response);
        }
    }

    private void redirigerSelonRole(Utilisateur user, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(user.getRole() == Role.ADMIN){
            response.sendRedirect(request.getContextPath() + "/admin/personnel");
        }else if(user.getRole() == Role.RECEPTIONNISTE){
            response.sendRedirect(request.getContextPath() + "/reservations");
        }else {
            response.sendRedirect(request.getContextPath() + "/carte");
        }

    }
}
