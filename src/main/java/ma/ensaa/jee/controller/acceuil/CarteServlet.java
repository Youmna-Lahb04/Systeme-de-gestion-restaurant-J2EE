package ma.ensaa.jee.controller.acceuil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ma.ensaa.jee.entity.Menu;
import ma.ensaa.jee.entity.Plat;
import ma.ensaa.jee.entity.enums.Categorie;
import ma.ensaa.jee.service.MenuService;
import ma.ensaa.jee.service.PlatService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet("/carte")
public class CarteServlet extends HttpServlet{

    private MenuService menuService;
    private PlatService platService;

    @Override
    public void init() {
        this.menuService = new MenuService();
        this.platService = new PlatService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String categorieParam = request.getParameter("categorie");
        try {
            Menu menuGeneral = menuService.getMenuGeneral();
            List<Plat> platsDisponibles = platService.getPlatsDisponibles();
            if (categorieParam != null && !categorieParam.isBlank() && !categorieParam.equalsIgnoreCase("ALL")) {
                try {
                    Categorie cat = Categorie.valueOf(categorieParam.toUpperCase());
                    platsDisponibles = platsDisponibles.stream()
                            .filter(p -> p.getCategorie() == cat)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException ignored) {
                }
            }
            request.setAttribute("menuGeneral", menuGeneral);
            request.setAttribute("plats", platsDisponibles);
            request.setAttribute("categories", Categorie.values());
            request.setAttribute("selectedCategorie", categorieParam != null ? categorieParam : "ALL");
            request.getRequestDispatcher("/WEB-INF/views/acceuil/carte.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("erreur", "Erreur lors du chargement de la carte : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/acceuil/carte.jsp").forward(request, response);
        }
    }
}
