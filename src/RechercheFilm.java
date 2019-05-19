import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class RechercheFilm {

    public ArrayList<String> typedLineArray = new ArrayList<String>();
    public ArrayList<ArrayList<String>> tab = new ArrayList<>();
    public ArrayList<String> temp_tab = new ArrayList<>();
    Connection conn = null;

    /**
     * @param nomFichierSQLite emplacement de la base de donnée dans l'ordinateur
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

    public void selectAll(){
        String sql = "SELECT id_film, titre FROM films";

        try (
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                //System.out.println(rs.getInt("id_film") +  "\t" + rs.getString("titre") + "\t");
            }
        }catch (SQLException e) {
            System.out.println("yooo");
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
     * Permet de mettre chaque mot entré par l'utilisateur dans des tableaux
     * @param TypedLine Ligne entrée par l'utilisateur
     */
    public void readLineFromUser(String TypedLine){
            //typedLineArray.addAll(Arrays.asList(TypedLine.split(",|\\.")));
            StringBuilder sb = new StringBuilder();
            typedLineArray.addAll(Arrays.asList(TypedLine.trim().split(",")));     //Séparation par virgule, ajout dans tableau

            //System.out.println("size: "+ typedLineArray.size());
            for (int i = 0; i< typedLineArray.size();i++) {
                for(int j =0; j < typedLineArray.get(i).length(); j++){
                    if(Character.isSpaceChar(typedLineArray.get(i).charAt(j))){
                        if (sb.length()!=0){
                            temp_tab.add(String.valueOf(sb));
                        }
                        sb = new StringBuilder();
                        sb.setLength(0);

                    }else{
                        sb.append(typedLineArray.get(i).charAt(j));
                        if(j==typedLineArray.get(i).length()-1){
                            temp_tab.add(String.valueOf(sb));
                            sb = new StringBuilder();
                            sb.setLength(0);
                        }
                    }
                }
                tab.add(temp_tab);
                temp_tab = new ArrayList<>();
            }

            for (int i = 0; i < tab.size(); i++) {
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
                System.out.println("i="+i+" : " + tab.get(i));
            }

    }


}


