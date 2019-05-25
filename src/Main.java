import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        RechercheFilm rechercheFilm = new RechercheFilm("C:\\Users\\Thivya A\\Desktop\\Projet_GLPOO\\bdfilm.sqlite\\bdfilm.sqlite");

        rechercheFilm.lectureLigneUtilisateur();
        //rechercheFilm.actualiser_tab();
        //rechercheFilm.selectAll();
        System.out.println(rechercheFilm.constructionSQL());


    }
}
