import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("Rechercher un film :");
        String str = sc.nextLine();
        System.out.println("Vous avez saisi : " + str);

        RechercheFilm rechercheFilm = new RechercheFilm();
        rechercheFilm.readLineFromUser(str);
    }
}
