import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RechercheFilm {

    public ArrayList<ArrayList<String>> tab_final = new ArrayList<>();    //contient tableau final (tableau de temp_tab)
    private Connection conn = null;

    //Recherche acteur
    String AVEC_SQL = "SELECT g.id_film\n" +
            "\tFROM personnes p\n" +
            "\tJOIN generique g \n" +
            "\tON g.id_personne = p.id_personne\n" +
            "\tWHERE nom_sans_accent LIKE ? AND (prenom_sans_accent LIKE ? ||'%' OR prenom_sans_accent IS NULL)\n" +
            "\tOR nom_sans_accent LIKE ? AND (prenom_sans_accent LIKE ? ||'%' OR prenom_sans_accent IS NULL)\n" +
            "\tOR nom_sans_accent LIKE ?\n" +
            "\tOR prenom_sans_accent LIKE ? || '%'\n" +
            "\tAND role = 'A'";
    //Recherche réalisateur
    String DE_SQL = "SELECT g.id_film\n" +
            "        FROM personnes p\n" +
            "        JOIN generique g \n" +
            "        ON g.id_personne = p.id_personne\n" +
            "        WHERE nom_sans_accent LIKE ? AND (prenom_sans_accent LIKE ? ||'%' OR prenom_sans_accent IS NULL)\n" +
            "        OR nom_sans_accent LIKE ? AND (prenom_sans_accent LIKE ? ||'%' OR prenom_sans_accent IS NULL)\n" +
            "        OR nom_sans_accent LIKE ?\n" +
            "        OR prenom_sans_accent LIKE ? || '%'\n" +
            "        AND role = 'R'";

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
        String user_line = scan.nextLine().toLowerCase().replaceAll(" ou ", " * ");
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
        Retrouve(constructionSQL());
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

        if(sep.size() == 0){
            SQL += etudeParametre(tab_final.get(0).get(0));
        } else {
            for (int i = 0; i < sep.size(); i++) {
                cond1 = tab_final.get(j).get(0);
                cond2 = tab_final.get(j + 1).get(0);

                if (!cond2.equals("avec") && !cond2.equals("avant") && !cond2.equals("de") && !cond2.equals("pays")
                        && !cond2.equals("titre") && !cond2.equals("en") && !cond2.equals("apres"))
                    cond2 = tab_final.get(j).get(0);

                if (!cond1.equals("avec") && !cond1.equals("avant") && !cond1.equals("de") && !cond1.equals("pays")
                        && !cond1.equals("titre") && !cond1.equals("en") && !cond1.equals("apres") && j > 0)
                    cond1 = tab_final.get(j - 1).get(0);

                // debug

                //System.out.println(bleu + cond1);
                //System.out.println(jaune + cond2 + blanc);

                if (sep.get(i).equals(",")) {
                    if ((i == 0 && sep.size() > 1) || (i < sep.size() - 1)) { // Si il est le premier et qu'il n'est pas le seul séparateur OU qu'il est au milieu
                        et = etudeParametre(cond1);
                        if (i != sep.size() - 1)
                            et += "\nINTERSECT\n"; // si il est vraiment au milieu on rajoute INTERSECT
                    } else if (sep.size() == 1 || ((i == sep.size() - 1) && sep.get(i).equals(sep.get(i - 1)))) { // sinon si il est le seul ou le dernier sachant que le separateur precedent etait ","
                        et = etudeParametre(cond1) + "\nINTERSECT\n" + etudeParametre(cond2);
                    } else { // sinon si il est le dernier
                        if (!sep.get(i).equals(sep.get(i - 1)))
                            et ="\nINTERSECT\n" + etudeParametre(cond2); // si avant c'était pas une ","
                        else et =etudeParametre(cond1);
                    }
                    SQL += et;
                } else {
                    if (i == 0 || !sep.get(i).equals(sep.get(i - 1))) { // si c'est le premier ou que le precedent était ","
                        ou = etudeParametre(cond1) + "\nUNION\n" + etudeParametre(cond2);
                    } else {
                        ou = "\nUNION\n" + etudeParametre(cond2);
                    }
                    SQL += ou;
                }
                if (j < tab_final.size()) j++;
            }
        }

        SQL+=   ")\n" +
                "select f.id_film, f.titre , f.pays, f.annee, f.duree, \n" +
                "group_concat(a.titre) as autres_titres,\n" +
                "p.prenom, p.nom, g.role\n" +
                "from filtre\n" +
                "\tjoin films f\n" +
                "\ton f.id_film = filtre.id_film\n" +
                "\tjoin pays py\n" +
                "\ton py.code = f.pays\n" +
                "\tleft join autres_titres a\n" +
                "\ton a.id_film = f.id_film\n" +
                "\tjoin generique g\n" +
                "\ton g.id_film = f.id_film\n" +
                "\tjoin personnes p\n" +
                "\ton p.id_personne = g.id_personne\n" +
                "\tgroup by f.id_film, f.titre , f.pays, f.annee, f.duree, p.prenom, p.nom, g.role\n" +
                "\tLIMIT 100;";

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



    public void Retrouve(String requete){


        String a ="" , b ="", param1="", param2="", param3="";
        int i=0, j, k = 1;
        ResultSet rs = null;
        //String youhou = user_line;
        actualiser_tab();
        ArrayList<String> ligne = new ArrayList<>();
            for(int aa = 1 ; aa < tab_final.get(0).size() ; aa++){
                ligne.add(tab_final.get(0).get(aa));
            }
        //System.out.println(sql);

        //System.out.println(tab_final.size());
        //System.out.println(tab_final);

        try(PreparedStatement pstmt  = conn.prepareStatement(requete)) {
            for(int tab=0; tab<tab_final.size();tab++){
                //System.out.println("tab" + tab);
                if(tab_final.get(tab).get(0).equals("titre")||
                        (tab_final.get(tab).get(0).equals("pays"))||
                        (tab_final.get(tab).get(0).equals("en"))||
                        (tab_final.get(tab).get(0).equals("avant"))||
                        (tab_final.get(tab).get(0).equals("apres"))||
                        (tab_final.get(tab).get(0).equals("après"))){
                    //System.out.println("ici");

                    for(int sous_tab=1;sous_tab<tab_final.get(tab).size();sous_tab++){
                        param1 = (param1 + tab_final.get(tab).get(sous_tab)).trim();
                    }
                   // System.out.println("parametre 1 :"+param1);
                    pstmt.setString(k, param1);


                    param1 = "";
                    k++;

                    if(tab < tab_final.size() -1) {
                        if (tab_final.get(tab + 1).get(0).equals("avec") || tab_final.get(tab + 1).get(0).equals("de")) {
                            pstmt.setString(k, "");
                            pstmt.setString(k + 1, "");
                            pstmt.setString(k + 2, "");
                            pstmt.setString(k + 3, "");
                            pstmt.setString(k + 4, "");
                            pstmt.setString(k + 5, "");
                        } else {
                            pstmt.setString(k, "");
                        }
                    }
                }else{
                    //System.out.println("je passe");
                    do{
                        for (j = 0; j < ligne.size(); j++) {
                            if (j <= i) param1 = (param1 + " " + ligne.get(j)).trim();
                            if (j > i) param2 = (param2 + " " + ligne.get(j)).trim();
                            param3 += ligne.get(j).trim();
                        }
                        pstmt.setString(k, param1);
                        k++;
                        pstmt.setString(k, param2);
                        k++;
                        pstmt.setString(k, param2);
                        k++;
                        pstmt.setString(k, param1);
                        k++;
                        pstmt.setString(k, param3);
                        k++;
                        pstmt.setString(k, param3);
                        k++;


                        if(tab < tab_final.size() -1) {
                            if (tab_final.get(tab + 1).get(0).equals("avec") || tab_final.get(tab + 1).get(0).equals("de")) {
                                pstmt.setString(k, "");
                                pstmt.setString(k + 1, "");
                                pstmt.setString(k + 2, "");
                                pstmt.setString(k + 3, "");
                                pstmt.setString(k + 4, "");
                                pstmt.setString(k + 5, "");
                            } else {
                                pstmt.setString(k, "");
                            }
                        }
                        /*
                        System.out.println("a = " + param1 + "\t\t\t\t\tb = " + param2);
                        System.out.println("c = " + param2 + "\t\t\t\t\td = " + param1 + "\n");
                        System.out.println("k = " + k);
                        System.out.println("param 1" + param1);
                        System.out.println("param 2" + param2);
                        */
                        rs = pstmt.executeQuery();
                        param1 = "";
                        param2 = "";
                        if (i<ligne.size()) i++;
                        //System.out.println("i= "+ i);
                        //System.out.println("size = " + ligne.size());
                    } while (!rs.next() && i < ligne.size());

                }
                rs = pstmt.executeQuery();
            }

            int id_prec = rs.getInt("id_film");
            ArrayList<NomPersonne> real = new ArrayList<>();
            ArrayList<NomPersonne> act = new ArrayList<>();
            ArrayList<String> autre_titres = new ArrayList<>();

            //System.out.println(id);

            while (rs.next()){
               /* System.out.println(rs.getInt("id_film") + "\t" +
                        rs.getString("pays") +  "\t" +
                        rs.getString("titre") +  "\t" +
                        rs.getString("prenom") + "\t" +
                        rs.getString("nom") + "\t" +
                        rs.getString("role") + "\t" +
                        rs.getString("autres_titres") + "\t");*/

                if("R".equals(rs.getString("role"))){
                    NomPersonne nomPersonne = new NomPersonne(rs.getString("nom"), rs.getString("prenom"));
                    real.add(nomPersonne);
                }
                if("A".equals(rs.getString("role"))){
                    NomPersonne nomPersonne = new NomPersonne(rs.getString("nom"), rs.getString("prenom"));
                    act.add(nomPersonne);
                }
                autre_titres.add(rs.getString("autres_titres"));
            InfoFilm infoFilm = new InfoFilm(rs.getString("titre"), real, act, rs.getString("pays") ,rs.getInt("annee"), rs.getInt("duree"), autre_titres);
            //System.out.println("je passe");
            System.out.println(infoFilm.toString());

            }


        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}