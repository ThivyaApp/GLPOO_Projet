import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        RechercheFilm rechercheFilm = new RechercheFilm("C:\\Users\\Thivya A\\Desktop\\Projet_GLPOO\\bdfilm.sqlite\\bdfilm.sqlite");
        /*Scanner scan = new Scanner(System.in);
        System.out.println("Rechercher un film :");
        String user_line = scan.nextLine();
        rechercheFilm.readLineFromUser(user_line);*/

        rechercheFilm.selectAll("Om");

    }
}
