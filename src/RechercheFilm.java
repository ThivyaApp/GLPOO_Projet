import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RechercheFilm {

    public ArrayList<ArrayList<String>> tab_final = new ArrayList<>();    //contient tableau final (tableau de temp_tab)
    Connection conn = null;

    //Recherche acteur
    public String AVEC_SQL = "\tselect g.id_film\n" +
            "\tfrom personnes p\n" +
            "\t\tjoin generique g \n" +
            "\t\ton g.id_personne = p.id_personne\n" +
            "\t\t\tWHERE (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "\t\t\tOR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "\t\t\tOR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "\t\t\tAND role = 'A'";

    //Recherche réalisateur
    public String DE_SQL = "\tselect g.id_film\n" +
            "\tfrom personnes p\n" +
            "\t\tjoin generique g \n" +
            "\t\ton g.id_personne = p.id_personne\n" +
            "\t\t\tWHERE (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "\t\t\tOR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "\t\t\tOR (nom_sans_accent LIKE ? AND prenom_sans_accent LIKE ? || '%')\n" +
            "\t\t\tAND role = 'R'";

    //Recherche titre
    public String TITRE_SQL = "\tselect id_film\n"+
                            "\tfrom recherche_titre\n"+
                            "\twhere titre match ?";

    //Recherche pays
    public String PAYS_SQL = "\tselect f.id_film\n"+
                            "\tfrom films f\n"+
                            "\t\tjoin pays p\n"+
                            "\t\ton f.pays = p.code\n"+
                            "\t\tWHERE f.pays LIKE ?\n"+
                            "\t\tOR p.nom LIKE ?";

    //Recherche année
    public String EN_SQL =  "\tselect id_film\n"+
                            "\tfrom films\n"+
                            "\tWHERE annee LIKE ?";

    //Recherche année AVANT
    public String AVANT_SQL = "\tselect id_film" +
            "\tfrom films\n" +
            "\tWHERE annee < ?";

    //Recherche année APRES
    public String APRES_SQL = "\tselect id_film" +
            "\tfrom films\n" +
            "\tWHERE annee > ? ";

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
            //System.out.println(tab_final.get(i));
        }
        recupSeparateur(TypedLine);
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

        System.out.println("sep size = " + sep.size());
        String bleu = "\u001B[34m";
        String jaune = "\u001B[33m";
        String vert = "\u001B[32m";
        String blanc = "\u001B[0m";

        for (int i = 0; i<sep.size();i++){
            cond1 = tab_final.get(j).get(0);
            cond2 = tab_final.get(j+1).get(0);

            // debug

            System.out.println(bleu + cond1);
            System.out.println(jaune + cond2 + blanc);
            //System.out.println("\u001B[34m etude parametre cond1 :" + etudeParametre(cond1));
            //System.out.println("\u001B[33m etude parametre cond2 :" + etudeParametre(cond2) + "\u001B[0m\n");

            if(sep.get(i)==","){
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
                    //if(j < tab_final.size())j++;
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
        String sql = "select id_film,titre " +
                "from films " +
                "WHERE titre LIKE ? ";

        String test = "agora";
        try(PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1,test);

            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()){
                System.out.println(rs.getInt("id_film") +  "\t" +
                        rs.getString("titre") + "\t");
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}