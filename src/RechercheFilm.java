import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RechercheFilm {

    private ArrayList<ArrayList<String>> tab_final = new ArrayList<>();    //contient tableau final (tableau de temp_tab)
    private Connection conn = null;

    /**
     * @return Retourne la ligne saisie par l'utilisateur après lui avoir demandé
     */
    public String demandeUtilisateur(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Rechercher un film :");
        String user_line = scan.nextLine().toLowerCase().replaceAll(" ou "," * ");
        return user_line;
    }

    /**
     * Permet de créer un tableau pour separer les éléments saisies par l'utilisateur
     */
    public void lectureLigneUtilisateur() {

        String TypedLine = demandeUtilisateur();
        ArrayList<String> temp_tab_nonnull = new ArrayList<>();          //contient split en fonction d'un espace des lignes de typedLineArray sans valeur null
        String[] temp_tab_null;   //contient split en fonction d'un espace des lignes de typedLineArray avec valeur null
        //Séparation par virgule, ajout dans tableau
        ArrayList<String> typedLineArray = new ArrayList<String>(Arrays.asList(TypedLine.trim().split(",|\\*")));

        for (String aTypedLineArray : typedLineArray) {
            temp_tab_null = aTypedLineArray.trim().split(" ");
            for (String aTemp_tab_null : temp_tab_null) {
                if (!aTemp_tab_null.equals(" ")) temp_tab_nonnull.add(aTemp_tab_null);
            }
            tab_final.add(temp_tab_nonnull);
            temp_tab_nonnull = new ArrayList<>();
        }
        recupSeparateur(TypedLine);
    }

    public void actualiser_tab(){
        String var = "";
        for(int i = 0; i<tab_final.size();i++){
            if(tab_final.get(i).get(0).equals("titre")||
                    (tab_final.get(i).get(0).equals("pays"))||
                    (tab_final.get(i).get(0).equals("en"))||
                    (tab_final.get(i).get(0).equals("avant"))||
                    (tab_final.get(i).get(0).equals("apres"))||
                    (tab_final.get(i).get(0).equals("après"))||
                    (tab_final.get(i).get(0).equals("de"))||
                    (tab_final.get(i).get(0).equals("avec"))){
                var = tab_final.get(i).get(0);
            }else{
                tab_final.get(i).add(0,var);
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
        String SQL = "with filtre as(\n";

        //System.out.println("sep size = " + sep.size());
        String bleu = "\u001B[34m";
        String jaune = "\u001B[33m";
        String vert = "\u001B[32m";
        String blanc = "\u001B[0m";

        for (int i = 0; i<sep.size();i++){
            cond1 = tab_final.get(j).get(0);
            cond2 = tab_final.get(j+1).get(0);

            if(!cond2.equals("avec") && !cond2.equals("avant") && !cond2.equals("de") && !cond2.equals("pays")
                    && !cond2.equals("titre") && !cond2.equals("en") && !cond2.equals("apres") )  cond2 = tab_final.get(j).get(0);

            if(!cond1.equals("avec") && !cond1.equals("avant") && !cond1.equals("de") && !cond1.equals("pays")
                    && !cond1.equals("titre") && !cond1.equals("en") && !cond1.equals("apres") && j > 0 )  cond1 = tab_final.get(j-1).get(0);

            // debug

            //System.out.println(bleu + cond1);
            //System.out.println(jaune + cond2 + blanc);

            if(sep.get(i).equals(",")){
                if((i == 0 && sep.size() > 1) || (i < sep.size()-1 )){ // Si il est le premier et qu'il n'est pas le seul séparateur OU qu'il est au milieu
                    et = vert + etudeParametre(cond1);
                    if(i != sep.size()-1) et  += "\nINTERSECT\n" + blanc; // si il est vraiment au milieu on rajoute INTERSECT
                }else if(sep.size() == 1 || ((i == sep.size()-1) && sep.get(i).equals(sep.get(i - 1)))){ // sinon si il est le seul ou le dernier sachant que le separateur precedent etait ","
                    et = bleu + etudeParametre(cond1)+ "\nINTERSECT\n" + etudeParametre(cond2) + blanc;
                } else { // sinon si il est le dernier
                    if(!sep.get(i).equals(sep.get(i - 1))) et = jaune + "\nINTERSECT\n" + etudeParametre(cond2) + blanc; // si avant c'était pas une ","
                    else et = jaune + etudeParametre(cond1) + blanc;
                }
                SQL += et;
            } else {
                if (i == 0 || !sep.get(i).equals(sep.get(i - 1))){ // si c'est le premier ou que le precedent était ","
                    ou = bleu + etudeParametre(cond1) + "\nUNION\n" + etudeParametre(cond2) + blanc ;
                } else {
                    ou = jaune + "\nUNION\n" + etudeParametre(cond2) + blanc;
                }
                SQL += ou;
            }

            if(j < tab_final.size())j++;
        }

        SQL+= blanc + ")\n" +
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
                "    group by f.id_film, f.titre , py.nom, f.annee, f.duree, p.prenom, p.nom, g.role\n" +
                "    LIMIT 100;";
        return SQL;
    }

    public String etudeParametre(String param){
        String SQL_req = new String();
        //Recherche titre
        String TITRE_SQL = "\tselect id_film\n" +
                "\tfrom recherche_titre\n" +
                "\twhere titre match ?";
        //Recherche acteur
        String AVEC_SQL = "select g.id_film\n" +
                "from personnes p\n" +
                "    join generique g\n" +
                "    on g.id_personne = p.id_personne\n" +
                "    WHERE (nom_sans_accent LIKE 'kajol' AND (prenom_sans_accent LIKE '' || '%' OR prenom_sans_accent IS NULL))\n" +
                "    OR (nom_sans_accent LIKE '' AND (prenom_sans_accent LIKE 'kajol'|| '%' OR prenom_sans_accent IS NULL))\n" +
                "    AND role = 'A'";
        //Recherche réalisateur
        String DE_SQL = "select g.id_film\n" +
                "from personnes p\n" +
                "    join generique g\n" +
                "    on g.id_personne = p.id_personne\n" +
                "    WHERE (nom_sans_accent LIKE 'kajol' AND (prenom_sans_accent LIKE '' || '%' OR prenom_sans_accent IS NULL))\n" +
                "    OR (nom_sans_accent LIKE '' AND (prenom_sans_accent LIKE 'kajol'|| '%' OR prenom_sans_accent IS NULL))\n" +
                "    AND role = 'R'";
        //Recherche pays
        String PAYS_SQL = "\tselect f.id_film\n" +
                "\tfrom films f\n" +
                "\t\tjoin pays p\n" +
                "\t\ton f.pays = p.code\n" +
                "\t\tWHERE f.pays LIKE ?\n" +
                "\t\tOR p.nom LIKE ?";
        //Recherche année
        String EN_SQL = "\tselect id_film\n" +
                "\tfrom films\n" +
                "\tWHERE annee LIKE ?";
        //Recherche année AVANT
        String AVANT_SQL = "\tselect id_film" +
                "\tfrom films\n" +
                "\tWHERE annee < ?";
        //Recherche année APRES
        String APRES_SQL = "\tselect id_film" +
                "\tfrom films\n" +
                "\tWHERE annee > ? ";

        switch (param){
            case "titre" :
                SQL_req = TITRE_SQL;
                break;
            case "de" :
                SQL_req = DE_SQL;
                break;
            case "avec" :
                SQL_req = AVEC_SQL;
                break;
            case "pays" :
                SQL_req = PAYS_SQL;
                break;
            case "en" :
                SQL_req = EN_SQL;
                break;
            case "avant" :
                SQL_req = AVANT_SQL;
                break;
            case "apres" :
                SQL_req = APRES_SQL;
                break;
        }
        return SQL_req;
    }

    public void selectAll(){
        String sql = "select g.id_film, p.prenom, p.nom\n" +
                "from personnes p\n" +
                "    join generique g\n" +
                "    on g.id_personne = p.id_personne\n" +
                "    WHERE (nom_sans_accent LIKE ? AND (prenom_sans_accent LIKE ? || '%' OR prenom_sans_accent IS NULL))\n";
                //"    OR (nom_sans_accent LIKE ? AND (prenom_sans_accent LIKE ? || '%' OR prenom_sans_accent IS NULL));";


        String a ="" , b ="" ;
        //String test = demandeUtilisateur();
        String rien = "";
        String tmp = "";
        int i, j = 0;
        ResultSet rs = null;
        String youhou = "sanjay leela bhansali";
        ArrayList<String> ligne = new ArrayList<>(Arrays.asList(youhou.split(" ")));

        try(PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            do {
                for (i = 0; i < ligne.size(); i++) {
                    for (j = 0; j < ligne.size(); j++) {
                        if (j <= i) a = (a + " " + ligne.get(j)).trim();
                        if (j > i) b = (b + " " + ligne.get(j)).trim();
                    }
                    pstmt.setString(1, b);
                    pstmt.setString(2, a);
                    System.out.println("a = " + a + "\t\t\t\t\tb = " + b);
                    rs = pstmt.executeQuery();
                    a = "";
                    b = "";
                }
            //} while (!rs.next());

            }while (!rs.wasNull());


            System.out.println(rs.getString("id_film"));
            //System.out.println(rs.absolute(1));

            while (rs.next()){
                System.out.println(rs.getInt("id_film") + "\t" +
                        rs.getString("nom") +  "\t" +
                        rs.getString("prenom") + "\t");
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}