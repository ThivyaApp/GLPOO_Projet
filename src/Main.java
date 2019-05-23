import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        RechercheFilm rechercheFilm = new RechercheFilm("C:\\Users\\Thivya A\\Desktop\\Projet_GLPOO\\bdfilm.sqlite\\bdfilm.sqlite");
        Scanner scan = new Scanner(System.in);
        System.out.println("Rechercher un film :");
        String user_line = scan.nextLine();
        rechercheFilm.lectureLigneUtilisateur(user_line.toLowerCase().replaceAll(" ou "," * "));
        //rechercheFilm.selectAll();
        rechercheFilm.recupSeparateur(user_line.toLowerCase().replaceAll(" ou "," * "));

        System.out.println(rechercheFilm.constructionSQL());
        /*String a = "Avec elle houhou";
        a = a + " yoo";
        System.out.println("a = " + a);*/

    }
}
