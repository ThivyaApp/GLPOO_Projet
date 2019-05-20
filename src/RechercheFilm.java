import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class RechercheFilm {

    public ArrayList<ArrayList<String>> tab_final = new ArrayList<>();    //contient tableau final (tableau de temp_tab)
    Connection conn = null;

    //Recherche acteur
    public String AVEC_SQL = "select g.id_film, p.id_personne, p.nom, p.prenom, g.role\n" +
            "from personnes p\n" +
            "    join generique g \n" +
            "    on g.id_personne = p.id_personne\n" +
            "        WHERE (nom_sans_accent LIKE 'Kajol' AND prenom_sans_accent LIKE '%')\n" +
            "        OR (prenom_sans_accent LIKE 'Kajol%' AND nom_sans_accent LIKE '')\n" +
            "        OR (prenom_sans_accent LIKE 'Kajol%')\n" +
            "        OR (nom_sans_accent LIKE 'Kajol')\n" +
            "        AND role = 'A'";

    //Recherche réalisateur
    public String DE_SQL = "select g.id_film, p.id_personne, p.nom, p.prenom, g.role\n" +
            "from personnes p\n" +
            "    join generique g \n" +
            "    on g.id_personne = p.id_personne\n" +
            "        WHERE (nom_sans_accent LIKE 'Kajol' AND prenom_sans_accent LIKE '%')\n" +
            "        OR (prenom_sans_accent LIKE 'Kajol%' AND nom_sans_accent LIKE '')\n" +
            "        OR (prenom_sans_accent LIKE 'Kajol%')\n" +
            "        OR (nom_sans_accent LIKE 'Kajol')\n" +
            "        AND role = 'R'";

    //Recherche titre
    public String TITRE_SQL = "select id_film\n"+
                            "from recherche_titre\n"+
                            "where titre match '?'";

    //Recherche pays
    public String PAYS_SQL = "select f.id_film\n"+
                            "from films f\n"+
                            "join pays p\n"+
                            "on f.pays = p.code\n"+
                            "WHERE f.pays LIKE '?'\n"+
                            "OR p.nom LIKE '?'";

    //Recherche année
    public String EN_SQL =  "select id_film\n"+
                            "from films\n"+
                            "WHERE annee LIKE '?'";

    //Recherche année AVANT
    public String AVANT_SQL = "select id_film, titre, annee\n" +
            "from films\n" +
            "    WHERE annee < '?'";

    //Recherche année APRES
    public String APRES_SQL = "select id_film, titre, annee\n" +
            "from films\n" +
            "    WHERE annee > '?'";

    /**
     * @param nomFichierSQLite emplacement de la base de donnée dans l'ordinateur (chemin)
     */
    public RechercheFilm(String nomFichierSQLite){
        String url = "jdbc:sqlite:"+nomFichierSQLite;
        // create a connection to the database
        try { conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAll(String id_film_val){
        String sql = "SELECT id_film, titre FROM films WHERE titre LIKE '?%' LIMIT 100;";
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
        ArrayList<String> typedLineArray = new ArrayList<String>();   //contient split de la ligne tapée par l'utilisateur
        ArrayList<String> temp_tab = new ArrayList<>();          //contient split en fonction d'un espace des lignes de typedLineArray sans valeur null
        String[] hey;   //contient split en fonction d'un espace des lignes de typedLineArray avec valeur null
        //POSSIBILITE : transformer le OU en point
        //typedLineArray.addAll(Arrays.asList(TypedLine.split(",|\\.")));
        typedLineArray.addAll(Arrays.asList(TypedLine.trim().split(",")));     //Séparation par virgule, ajout dans tableau

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


