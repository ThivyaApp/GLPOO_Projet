import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RechercheFilm {

    public ArrayList<ArrayList<String>> tab_final = new ArrayList<>();    //contient tableau final (tableau de temp_tab)
    Connection conn = null;

    //Recherche acteur
    public String AVEC_SQL = "select g.id_film\n" +
            "from personnes p\n" +
            "    join generique g \n" +
            "    on g.id_personne = p.id_personne\n" +
            "        WHERE (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "        OR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "        OR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "        AND role = 'A'\n"+
            "        LIMIT 100;";

    //Recherche réalisateur
    public String DE_SQL = "select g.id_film\n" +
            "from personnes p\n" +
            "    join generique g \n" +
            "    on g.id_personne = p.id_personne\n" +
            "        WHERE (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "        OR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "        OR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "        AND role = 'R'\n"+
            "        LIMIT 100;";

    //Recherche titre
    public String TITRE_SQL = "select id_film\n"+
                            "from recherche_titre\n"+
                            "where titre match '?'\n"+
                            "LIMIT 100;";

    //Recherche pays
    public String PAYS_SQL = "select f.id_film\n"+
                            "from films f\n"+
                            "join pays p\n"+
                            "on f.pays = p.code\n"+
                            "WHERE f.pays LIKE '?'\n"+
                            "OR p.nom LIKE '?'\n"+
                             "LIMIT 100;";

    //Recherche année
    public String EN_SQL =  "select id_film\n"+
                            "from films\n"+
                            "WHERE annee LIKE '?'\n"+
                            "LIMIT 100;";

    //Recherche année AVANT
    public String AVANT_SQL = "select id_film\n" +
            "from films\n" +
            "    WHERE annee < '?'\n"+
            "    LIMIT 100;";

    //Recherche année APRES
    public String APRES_SQL = "select id_film\n" +
            "from films\n" +
            "    WHERE annee > '?'\n"+
            "    LIMIT 100;";

    /**
     * Permet de créer un tableau pour separer les éléments saisies par l'utilisateur
     * @param TypedLine Ligne entrée par l'utilisateur
     */


    public void lectureLigneUtilisateur(String TypedLine) {

        ArrayList<String> typedLineArray = new ArrayList<String>();   //contient split de la ligne tapée par l'utilisateur
        ArrayList<String> temp_tab_nonnull = new ArrayList<>();          //contient split en fonction d'un espace des lignes de typedLineArray sans valeur null
        String[] temp_tab_null;   //contient split en fonction d'un espace des lignes de typedLineArray avec valeur null
        typedLineArray.addAll(Arrays.asList(TypedLine.trim().split(",|\\*")));     //Séparation par virgule, ajout dans tableau

        for (int i = 0; i < typedLineArray.size(); i++) {
            temp_tab_null = typedLineArray.get(i).trim().split(" ");
            for(int w=0; w<temp_tab_null.length;w++){
                if(temp_tab_null[w].equals("")){}
                else temp_tab_nonnull.add(temp_tab_null[w]);
            }
            tab_final.add(temp_tab_nonnull);
            temp_tab_nonnull = new ArrayList<>();
        }

        for (int i = 0; i < tab_final.size(); i++) {
            if (tab_final.get(i).get(0).toUpperCase().contains("TITRE")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("TITRE");
            }
            if (tab_final.get(i).get(0).toUpperCase().contains("DE")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("DE");
            }
            if (tab_final.get(i).get(0).toUpperCase().contains("AVEC")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("AVEC");
            }
            if (tab_final.get(i).get(0).toUpperCase().contains("PAYS")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("PAYS");
            }
            if (tab_final.get(i).get(0).toUpperCase().contains("EN")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("EN");
            }
            if (tab_final.get(i).get(0).toUpperCase().contains("AVANT")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("AVANT");
            }
            if (tab_final.get(i).get(0).toUpperCase().contains("APRES") || tab_final.get(i).contains("APRÈS ")) {
                System.out.println(tab_final.get(i).get(0));
                etudeParametre("APRES");
            }
        }
    }

    /**
     * @param nomFichierSQLite emplacement de la base de données dans l'ordinateur (chemin)
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

    public void selectAll(){
        String sql = "select id_film,titre " +
                "from films " +
                "WHERE titre LIKE ? ";

        String test = "agora";
        try(PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1,test);

            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            while (rs.next()){
                System.out.println(rs.getInt("id_film") +  "\t" +
                        rs.getString("titre") + "\t");
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String etudeParametre(String param){
        String SQL_req = new String();
        switch (param){
            case "TITRE" :
                SQL_req = TITRE_SQL;
                break;
            case "DE" :
                SQL_req = DE_SQL;
                break;
            case "AVEC" :
                SQL_req = AVEC_SQL;
                break;
            case "PAYS" :
                SQL_req = PAYS_SQL;
                break;
            case "EN" :
                SQL_req = EN_SQL;
                break;
            case "AVANT" :
                SQL_req = AVANT_SQL;
                break;
            case "APRES" :
                SQL_req = APRES_SQL;
                break;
        }
        return SQL_req;
    }


    public ArrayList<String> sep = new ArrayList<>();   //tableau comportant les virgules(et), étoiles(ou)

    public void recupSeparateur(String chaine){
        for(int s=0;s<chaine.length(); s++){
            if(chaine.charAt(s)==',') sep.add(",");
            else if(chaine.charAt(s)=='*') sep.add("*");
        }
    }

    public String constructionSQL(){
        String et ,ou ,cond1,cond2 ;
        int j = 0;
        String SQL = "with filtre as(";

        for (int i = 0; i<sep.size();i++){
            cond1 = tab_final.get(j).get(0);
            cond2 = tab_final.get(j+1).get(0);
            if(sep.get(i)==","){
                if((i==0 && sep.size() != 1) || (sep.get(i-1).equals(sep.get(i)))){
                    et = etudeParametre(cond1) + "\nINTERSECT\n";
                }else if(sep.size() == 1){
                    et = etudeParametre(cond1)+ "\nINTERSECT\n" + etudeParametre(cond2);
                } else {
                    et = "INTERSECT\n" + etudeParametre(cond1);
                }
            SQL += et;
            } else {
                if (i == 0 || !sep.get(i).equals(sep.get(i - 1))){
                    ou = etudeParametre(cond1) + "\nUNION\n" + etudeParametre(cond2);
                    if(j < tab_final.size())j++;
                } else {
                    ou = "\nUNION\n" + etudeParametre(cond1);
                }
            SQL += ou;
            }

            if(j < tab_final.size())j++;
        }

        SQL+= ")\n" +
                "select f.id_film, f.titre , py.nom, f.annee, f.duree, \n" +
                "group_concat(a.titre, ' | ') as autres_titres,\n" +
                "p.prenom, p.nom, g.role\n" +
                "from filtre\n" +
                "    join films f\n" +
                "    on f.id_film = filtre.id_film\n" +
                "    join pays py\n" +
                "    on py.code = f.pays\n" +
                "    left join autres_titres a\n" +
                "    on a.id_film = f.id_film\n" +
                "    join generique g\n" +
                "    on g.id_film = f.id_film\n" +
                "    join personnes p\n" +
                "    on p.id_personne = g.id_personne\n" +
                "    group by f.id_film, f.titre , py.nom, f.annee, f.duree, p.prenom, p.nom, g.role";
        return SQL;
    }
}