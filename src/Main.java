import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        RechercheFilm rechercheFilm = new RechercheFilm("/Users/douglaslopeze/IdeaProjects/GLPOO_Projet/bdfilm.sqlite");

        //rechercheFilm.lectureLigneUtilisateur();
        rechercheFilm.selectAll();
        //System.out.println(rechercheFilm.constructionSQL());


    }
}
