package ma.ensaa.jee.filter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ma.ensaa.jee.entity.Utilisateur;
import ma.ensaa.jee.entity.enums.Role;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI().substring(contextPath.length());
        // laisser les ressources statiques passer (CSS, JS, images, icônes)
        if (isStaticResource(uri)) {
            chain.doFilter(request, response);
            return;
        }
        //laisser les pages publiques passer(accessibles à tous sans connexion)
        if (isPublicUrl(uri)) {
            chain.doFilter(request, response);
            return;
        }
        //vérifier si l'utilisateur est connecté en session
        HttpSession session = httpRequest.getSession(false);
        Utilisateur user = (session != null) ? (Utilisateur) session.getAttribute("user") : null;
        if (user == null) {
            //sinon enregistrer rediriger vers login
            HttpSession newSession = httpRequest.getSession(true);
            newSession.setAttribute("erreur", "Veuillez vous connecter pour accéder à cette page.");
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        //contrôle d'accès par rôle : ADMIN
        if (uri.startsWith("/admin/")) {
            if (user.getRole() != Role.ADMIN) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé : espace réservé aux administrateurs.");
                return;
            }
        }
        //si tout est valide, autoriser l'accès à la Servlet demandée
        chain.doFilter(request, response);
    }

    private boolean isPublicUrl(String uri) {
        return uri.equals("/") ||
                uri.equals("/index.jsp") ||
                uri.equals("/login") ||
                uri.equals("/auth/login.jsp") ||
                uri.equals("/inscription") ||
                uri.equals("/auth/inscription.jsp") ||
                uri.equals("/carte") ||
                uri.equals("/public/carte.jsp");
    }

    private boolean isStaticResource(String uri) {
        return uri.endsWith(".css") ||
                uri.endsWith(".js") ||
                uri.endsWith(".png") ||
                uri.endsWith(".jpg") ||
                uri.endsWith(".jpeg") ||
                uri.endsWith(".svg") ||
                uri.endsWith(".ico") ||
                uri.endsWith(".woff") ||
                uri.endsWith(".woff2") ||
                uri.endsWith(".ttf");
    }
}
