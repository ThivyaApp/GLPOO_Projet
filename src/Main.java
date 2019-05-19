import java.util.Scanner;

public class Main {
    public static void main(String[] args){


        RechercheFilm rechercheFilm = new RechercheFilm("C:\\Users\\Thivya A\\Desktop\\Projet_GLPOO\\bdfilm.sqlite\\bdfilm.sqlite");


        /*Scanner sc = new Scanner(System.in);
        System.out.println("Rechercher un film :");
        String str = sc.nextLine();
        System.out.println("Vous avez saisi : " + str);

        rechercheFilm.readLineFromUser(str);*/

        rechercheFilm.selectAll();

    }
}
