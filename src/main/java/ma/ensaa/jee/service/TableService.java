package ma.ensaa.jee.service;

import ma.ensaa.jee.entity.TableRestaurant;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TableService {
    public TableRestaurant ajouterTable(
            TableRestaurant table,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public TableRestaurant modifierTable(
            TableRestaurant table,
            Long utilisateurConnecteId
    ){
        return null;
    };

    public void supprimerTable(
            Long id,
            Long utilisateurConnecteId
    ){};

    public TableRestaurant getTableById(Long id){
        return null;
    };

    public List<TableRestaurant> getAllTables(){
        return null;
    };

    public List<TableRestaurant> getTablesDisponibles(
            LocalDate date,
            LocalTime heure,
            int nombrePersonnes
    ){
        return null;
    };
}
