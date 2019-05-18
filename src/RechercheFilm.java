import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RechercheFilm {

    public ArrayList<String> typedLineArray = new ArrayList<String>();
    public ArrayList<ArrayList<String>> tab = new ArrayList<>();
    public ArrayList<String> temp_tab = new ArrayList<>();

    /**
     * Permet de diviser la recherche entrée par l'utilisateur afin de mettre les differents mots dans des tableaux différents
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
                System.out.println("i="+i+" : " + tab.get(i)); }
    }

}


