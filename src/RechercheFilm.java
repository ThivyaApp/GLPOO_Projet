import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class RechercheFilm {

    public ArrayList<String> typedLineArray = new ArrayList<String>();   //contient split de la ligne tapée par l'utilisateur
    public ArrayList<String> temp_tab = new ArrayList<>();          //contient split en fonction d'un espace des lignes de typedLineArray sans valeur null
    public ArrayList<ArrayList<String>> tab_final = new ArrayList<>();    //contient tableau final (tableau de temp_tab)
    Connection conn = null;

    /**
     * @param nomFichierSQLite emplacement de la base de donnée dans l'ordinateur (chemin)
     */
    public RechercheFilm(String nomFichierSQLite){
        String url = "jdbc:sqlite:"+nomFichierSQLite;
        try {
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void selectAll(String id_film_val){
        String sql = "SELECT id_film, titre FROM films WHERE titre LIKE '?%' LIMIT 100;";
        System.out.println("String vaut = " + id_film_val);
        try(PreparedStatement pstmt  = conn.prepareStatement(sql)){

            //pstmt.setString(1,id_film_val);
            pstmt.setString(1,id_film_val);
            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id_film") +  "\t" +
                        rs.getString("titre") + "\t");
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void etudeParametre(String param){
        switch (param){
            case "TITRE" :
                System.out.println("methode titre");
                break;
            case "DE" :
                System.out.println("methode de");
                break;
            case "AVEC" :
                System.out.println("methode avec");
                break;
            case "PAYS" :
                System.out.println("methode pays");
                break;
            case "EN" :
                System.out.println("methode en");
                break;
            case "AVANT" :
                System.out.println("methode avant");
                break;
            case "APRES" :
                System.out.println("methode apres");
                break;

        }

    }

    /**
     * Permet de créer un tableau pour separer les éléments saisies par l'utilisateur
     * @param TypedLine Ligne entrée par l'utilisateur
     */
    public void readLineFromUser(String TypedLine) {
        //POSSIBILITE : transformer le OU en point
        //typedLineArray.addAll(Arrays.asList(TypedLine.split(",|\\.")));
        typedLineArray.addAll(Arrays.asList(TypedLine.trim().split(",")));     //Séparation par virgule, ajout dans tableau

        String[] hey;
        for (int i = 0; i < typedLineArray.size(); i++) {
            hey = typedLineArray.get(i).trim().split(" ");
            for(int w=0; w<hey.length;w++){
                if(hey[w].equals("")){}
                else temp_tab.add(hey[w]);
            }
            tab_final.add(temp_tab);
            temp_tab = new ArrayList<>();
        }
        System.out.println(tab_final);


        /*for (int i = 0; i < tab.size(); i++) {
            if (tab.get(i).contains("TITRE")) {
                etudeParametre("TITRE");
            }
            if (tab.get(i).contains("DE")) {
                etudeParametre("DE");
            }
            if (tab.get(i).contains("AVEC")) {
                etudeParametre("AVEC");
            }
            if (tab.get(i).contains("PAYS")) {
                etudeParametre("PAYS");
            }
            if (tab.get(i).contains("EN")) {
                etudeParametre("EN");
            }
            if (tab.get(i).contains("AVANT")) {
                etudeParametre("AVANT");
            }
            if (tab.get(i).contains("APRES") || tab.get(i).contains("APRÈS ")) {
                etudeParametre("APRES");
            }
            System.out.println("i=" + i + " : " + tab.get(i));
        }*/

    }


}


