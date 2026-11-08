package ma.ensaa.jee.service;

import ma.ensaa.jee.dao.ReservationDAO;
import ma.ensaa.jee.dao.TableDAO;
import ma.ensaa.jee.entity.Reservation;
import ma.ensaa.jee.entity.TableRestaurant;
import ma.ensaa.jee.entity.enums.Role;
import ma.ensaa.jee.entity.enums.StatutReservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TableService {

    private static final int DUREE_CRENEAU_HEURES = 3;

    private final TableDAO tableDAO;
    private final ReservationDAO reservationDAO;
    private final AuthorizationService authorizationService;

    public TableService(){
        this.tableDAO = new TableDAO();
        this.reservationDAO = new ReservationDAO();
        this.authorizationService = new AuthorizationService();
    }

    public TableRestaurant ajouterTable(TableRestaurant table, Long utilisateurConnecteId){
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        validerDonneesTable(table);
        TableRestaurant existante = tableDAO.trouverParNumero(table.getNumero());
        if (existante != null) {
            throw new IllegalArgumentException("Une table avec le numéro " + table.getNumero() + " existe déjà.");
        }
        tableDAO.ajouterTable(table);
        return table;
    }

    public TableRestaurant modifierTableRestaurant (TableRestaurant table, Long utilisateurConnecteId){
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if (table == null || table.getId() == null) {
            throw new IllegalArgumentException("La table et son identifiant sont obligatoires.");
        }
        validerDonneesTable(table);
        TableRestaurant existante = tableDAO.trouverParId(table.getId());
        if (existante == null) {
            throw new IllegalArgumentException("Table introuvable avec l'identifiant : " + table.getId());
        }
        // Vérifier que le numéro n'est pas utilisé par une AUTRE table
        TableRestaurant tableAvecMemeNumero = tableDAO.trouverParNumero(table.getNumero());
        if (tableAvecMemeNumero != null && !tableAvecMemeNumero.getId().equals(table.getId())) {
            throw new IllegalArgumentException("Le numéro " + table.getNumero() + " est déjà utilisé par une autre table.");
        }
        return tableDAO.modifierTable(table);
    }

    public void supprimerTable(Long id, Long utilisateurConnecteId){
        authorizationService.verifierRole(utilisateurConnecteId, Role.ADMIN);
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant de la table est obligatoire.");
        }

        tableDAO.supprimerTable(id);
    }

    public TableRestaurant getTableById(Long id){
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant de la table est obligatoire.");
        }
        TableRestaurant table = tableDAO.trouverParId(id);
        if (table == null) {
            throw new IllegalArgumentException("Table introuvable avec l'identifiant : " + id);
        }
        return table;
    }

    public List<TableRestaurant> getAllTables(){
        return tableDAO.trouverToutes();
    }

    public List<TableRestaurant> getTablesDisponibles(LocalDate date, LocalTime heure, int nombrePersonnes){
        if (date == null || heure == null) {
            throw new IllegalArgumentException("La date et l'heure sont obligatoires.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date ne peut pas être dans le passé.");
        }
        if (date.isEqual(LocalDate.now()) && heure.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("L'heure demandée est déjà passée.");
        }
        if (nombrePersonnes <= 0) {
            throw new IllegalArgumentException("Le nombre de personnes doit être supérieur à zéro.");
        }
        LocalTime heureDebut = heure;
        LocalTime heureFin = heure.plusHours(DUREE_CRENEAU_HEURES);
        if (heureFin.isBefore(heureDebut)) {
            heureFin = LocalTime.MAX;
        }
        List<TableRestaurant> toutesLesTables = tableDAO.trouverToutes();
        List<TableRestaurant> tablesDisponibles = new ArrayList<>();
        for (TableRestaurant table : toutesLesTables) {
            //verifier capacite
            if (table.getCapacite() < nombrePersonnes) {
                continue;
            }
            // Vérifier les réservations du jour de cette table
            List<Reservation> reservationsJour = reservationDAO.trouverParTableEtDate(table.getId(), date);
            boolean disponible = true;
            for (Reservation r : reservationsJour) {
                if (r.getStatutReservation() == StatutReservation.ANNULEE ||
                        r.getStatutReservation() == StatutReservation.REFUSEE) {
                    continue;
                }
                // Chevauchement horaire
                if (heureDebut.isBefore(r.getHeureFin()) && heureFin.isAfter(r.getHeureDebut())) {
                    disponible = false;
                    //table est occuppee dans pas la paine de verifier les autres reser
                    break;
                }
            }
            if (disponible) {
                tablesDisponibles.add(table);
            }
        }
        // Tri par capacité croissante
        tablesDisponibles.sort(Comparator.comparingInt(TableRestaurant::getCapacite));
        return tablesDisponibles;
    }

    //methode prive de verification des infos de table
    private void validerDonneesTable(TableRestaurant table) {
        if (table == null) {
            throw new IllegalArgumentException("Les informations de la table sont obligatoires.");
        }
        if (table.getNumero() == null || table.getNumero() <= 0) {
            throw new IllegalArgumentException("Le numéro de table doit être un entier positif.");
        }
        if (table.getCapacite() == null || table.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité de la table doit être supérieure à zéro.");
        }
    }
}
