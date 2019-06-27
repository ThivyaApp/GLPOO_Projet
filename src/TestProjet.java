import java.io.IOException;
import java.io.InputStreamReader;

public class TestProjet {

    public static void main(String[] args) throws IOException {
      /*if (args.length > 0) {
         StringBuilder requete = new StringBuilder();
         for (int i = 0; i < args.length; i++) {
            if (i > 0) {
               requete.append(' ');
            }
            requete.append(args[i]);
         }*/

         try {
              RechercheFilm rf = new RechercheFilm("C:\\Users\\Thivya A\\Desktop\\ProjetGLPOO2\\bdfilm.sqlite\\bdfilm.sqlite");
              System.out.println(rf.retrouve("avant 2018"));
              rf.fermeBase();
          } catch (Exception e) {
             System.err.println("Exception caught:" + e.toString());
             System.exit(1);
          }

      //}
      System.exit(0);
    }
}
