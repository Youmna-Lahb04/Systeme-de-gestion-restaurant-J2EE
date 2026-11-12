package ma.ensaa.jee.controller.auth;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.service.AuthService;
import java.io.IOException;

@WebServlet("/inscription")
public class SignInServlet  extends HttpServlet{
    private AuthService authService;

    @Override
    public void init() {
        this.authService = new AuthService();
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //verifier si user n'est pas deja inscrit
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/carte");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/inscription.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String motDePasse = request.getParameter("motDePasse");
        String confirmationMotDePasse = request.getParameter("confirmationMotDePasse");

        try{
            if (motDePasse == null || !motDePasse.equals(confirmationMotDePasse)) {
                throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
            }
            Utilisateur nouveauClient = new Utilisateur();
            nouveauClient.setNom(nom);
            nouveauClient.setPrenom(prenom);
            nouveauClient.setEmail(email);
            nouveauClient.setTelephone(telephone);
            nouveauClient.setMotDePasse(motDePasse);
            //inscrire le user
            authService.inscrireClient(nouveauClient);
            //msg succes
            HttpSession session = request.getSession();
            session.setAttribute("succes", "Votre compte a été créé avec succès ! Connectez-vous dès maintenant.");
            response.sendRedirect(request.getContextPath() + "/login");
        }catch (IllegalArgumentException e){
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("nom", nom);
            request.setAttribute("prenom", prenom);
            request.setAttribute("email", email);
            request.setAttribute("telephone", telephone);
            request.getRequestDispatcher("/WEB-INF/views/auth/inscription.jsp").forward(request, response);
        }
    }
}
