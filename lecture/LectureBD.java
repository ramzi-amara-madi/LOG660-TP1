import java.io.*;

import java.sql.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LectureBD {

   // Indiquer les chemins des fichiers xml
   final static String PERSONNE_PATH = ""
           , CLIENTS_PATH = ""
           , FILMS_PATH = "lecture/Donnees/films_latin1.xml";

   static Connection connection;
   public class Role {
      public Role(int i, String n, String p) {
         id = i;
         nom = n;
         personnage = p;
      }
      protected int id;
      protected String nom;
      protected String personnage;
   }

   private ArrayList<Integer> realisateurs = new ArrayList<Integer>();
   private ArrayList<Integer> acteurs = new ArrayList<Integer>();
   private ArrayList<String> scenaristes = new ArrayList<String>();
   private ArrayList<String> genres = new ArrayList<String>();
   private ArrayList<String> pays = new ArrayList<String>();

   public LectureBD() {
      connectionBD();                     
   }
   
   
   public void lecturePersonnes(String nomFichier){      
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null, 
                nom = null,
                anniversaire = null,
                lieu = null,
                photo = null,
                bio = null;
         
         int id = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("personne") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("personne") && id >= 0)
               {
                  insertionPersonne(id,nom,anniversaire,lieu,photo,bio);
                                    
                  id = -1;
                  nom = null;
                  anniversaire = null;
                  lieu = null;
                  photo = null;
                  bio = null;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {
               if (tag != null)
               {                                    
                  if (tag.equals("nom"))
                     nom = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniversaire = parser.getText();
                  else if (tag.equals("lieu"))
                     lieu = parser.getText();
                  else if (tag.equals("photo"))
                     photo = parser.getText();
                  else if (tag.equals("bio"))
                     bio = parser.getText();
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
       }
       catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
       }
   }   
   
   public void lectureFilms(){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

            InputStream is = new FileInputStream(FILMS_PATH);
            parser.setInput(is, null);

         int eventType = parser.getEventType();

         String tag = null, 
                titre = null,
                langue = null,
                poster = null,
                roleNom = null,
                rolePersonnage = null,
                realisateurNom = null,
                resume = null;
         
         ArrayList<String> pays = new ArrayList<String>();
         ArrayList<String> genres = new ArrayList<String>();
         ArrayList<String> scenaristes = new ArrayList<String>();
         ArrayList<Role> roles = new ArrayList<Role>();         
         ArrayList<String> annonces = new ArrayList<String>();
         
         int id = -1,
             annee = -1,
             duree = -1,
             roleId = -1,
             realisateurId = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("film") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("realisateur") && parser.getAttributeCount() == 1)
                  realisateurId = Integer.parseInt(parser.getAttributeValue(0));
               else if (tag.equals("acteur") && parser.getAttributeCount() == 1)
                  roleId = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("film") && id >= 0)
               {
                  insertionFilm(id,titre,annee,pays,langue,
                             duree,resume,genres,realisateurNom,
                             realisateurId, scenaristes,
                             roles,poster,annonces);
                                    
                  id = -1;
                  annee = -1;
                  duree = -1;
                  titre = null;                                 
                  langue = null;                  
                  poster = null;
                  resume = null;
                  realisateurNom = null;
                  roleNom = null;
                  rolePersonnage = null;
                  realisateurId = -1;
                  roleId = -1;
                  
                  genres.clear();
                  scenaristes.clear();
                  roles.clear();
                  annonces.clear();  
                  pays.clear();
               }
               if (parser.getName().equals("role") && roleId >= 0) 
               {              
                  roles.add(new Role(roleId, roleNom, rolePersonnage));
                  roleId = -1;
                  roleNom = null;
                  rolePersonnage = null;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {
               if (tag != null)
               {                                    
                  if (tag.equals("titre"))
                     titre = parser.getText();
                  else if (tag.equals("annee"))
                     annee = Integer.parseInt(parser.getText());
                  else if (tag.equals("pays"))
                     pays.add(parser.getText());
                  else if (tag.equals("langue"))
                     langue = parser.getText();
                  else if (tag.equals("duree"))                 
                     duree = Integer.parseInt(parser.getText());
                  else if (tag.equals("resume"))                 
                     resume = parser.getText();
                  else if (tag.equals("genre"))
                     genres.add(parser.getText());
                  else if (tag.equals("realisateur"))
                     realisateurNom = parser.getText();
                  else if (tag.equals("scenariste"))
                     scenaristes.add(parser.getText());
                  else if (tag.equals("acteur"))
                     roleNom = parser.getText();
                  else if (tag.equals("personnage"))
                     rolePersonnage = parser.getText();
                  else if (tag.equals("poster"))
                     poster = parser.getText();
                  else if (tag.equals("annonce"))
                     annonces.add(parser.getText());                  
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + FILMS_PATH);
      }
   }
   
   public void lectureClients(String nomFichier){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(nomFichier);
         parser.setInput(is, null);

         int eventType = parser.getEventType();               

         String tag = null, 
                nomFamille = null,
                prenom = null,
                courriel = null,
                tel = null,
                anniv = null,
                adresse = null,
                ville = null,
                province = null,
                codePostal = null,
                carte = null,
                noCarte = null,
                motDePasse = null,
                forfait = null;                                 
         
         int id = -1,
             expMois = -1,
             expAnnee = -1;
         
         while (eventType != XmlPullParser.END_DOCUMENT) 
         {
            if(eventType == XmlPullParser.START_TAG) 
            {
               tag = parser.getName();
               
               if (tag.equals("client") && parser.getAttributeCount() == 1)
                  id = Integer.parseInt(parser.getAttributeValue(0));
            } 
            else if (eventType == XmlPullParser.END_TAG) 
            {                              
               tag = null;
               
               if (parser.getName().equals("client") && id >= 0)
               {
                  insertionClient(id,nomFamille,prenom,courriel,tel,
                             anniv,adresse,ville,province,
                             codePostal,carte,noCarte, 
                             expMois,expAnnee,motDePasse,forfait);               
                                    
                  nomFamille = null;
                  prenom = null;
                  courriel = null;               
                  tel = null;
                  anniv = null;
                  adresse = null;
                  ville = null;
                  province = null;
                  codePostal = null;
                  carte = null;
                  noCarte = null;
                  motDePasse = null; 
                  forfait = null;
                  
                  id = -1;
                  expMois = -1;
                  expAnnee = -1;
               }
            }
            else if (eventType == XmlPullParser.TEXT && id >= 0) 
            {         
               if (tag != null)
               {                                    
                  if (tag.equals("nom-famille"))
                     nomFamille = parser.getText();
                  else if (tag.equals("prenom"))
                     prenom = parser.getText();
                  else if (tag.equals("courriel"))
                     courriel = parser.getText();
                  else if (tag.equals("tel"))
                     tel = parser.getText();
                  else if (tag.equals("anniversaire"))
                     anniv = parser.getText();
                  else if (tag.equals("adresse"))
                     adresse = parser.getText();
                  else if (tag.equals("ville"))
                     ville = parser.getText();
                  else if (tag.equals("province"))
                     province = parser.getText();
                  else if (tag.equals("code-postal"))
                     codePostal = parser.getText();
                  else if (tag.equals("carte"))
                     carte = parser.getText();
                  else if (tag.equals("no"))
                     noCarte = parser.getText();
                  else if (tag.equals("exp-mois"))                 
                     expMois = Integer.parseInt(parser.getText());
                  else if (tag.equals("exp-annee"))                 
                     expAnnee = Integer.parseInt(parser.getText());
                  else if (tag.equals("mot-de-passe"))                 
                     motDePasse = parser.getText();  
                  else if (tag.equals("forfait"))                 
                     forfait = parser.getText(); 
               }              
            }
            
            eventType = parser.next();            
         }
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
      }
   }   
   
   private void insertionPersonne(int id, String nom, String anniv, String lieu, String photo, String bio) {      
      // On insere la personne dans la BD
   }
   
   private void insertionFilm(int id, String titre, int annee,
                           ArrayList<String> pays, String langue, int duree, String resume,
                           ArrayList<String> genres, String realisateurNom, int realisateurId,
                           ArrayList<String> scenaristes,
                           ArrayList<Role> roles, String poster,
                           ArrayList<String> annonces) {

      String requeteInsertionRealisateur = "INSERT INTO Realisateur(idRealisateur, Nom) VALUES (?,?)";
      String requeteInsertionActeur = "INSERT INTO Acteur(idActeur) VALUES (?)";
      String requeteInsertionPersonnage = "INSERT INTO Personnage(idActeur, idFilm, Nom) VALUES (?,?,?)";
      String requeteInsertionFilm = "INSERT INTO Film(idFilm, titre, annee, langueOriginale, dureeFilm, resumeFilm, affiche, nombreCopie, idrealisateur) VALUES (?,?, ?, ?, ?, ? ,? ,? ,?)";
      String requeteInsertionScenariste = "INSERT INTO Scenariste(nom) VALUES (?)";
      String requeteInsertionGenre = "INSERT INTO Genre(nom) VALUES (?)";
      String requeteInsertionPays = "INSERT INTO Pays(nom) VALUES (?)";
      String requeteInsertionScenaristeFilm = "INSERT INTO ScenaristeFilm(idScenariste, idFilm) VALUES (?, ?)";
      String requeteInsertionGenreFilm = "INSERT INTO GenreFilm(idGenre, idFilm) VALUES (?, ?)";
      String requeteInsertionPaysProductionFilm = "INSERT INTO PaysProductionFilm(idPays, idFilm) VALUES (?, ?)";

        try {
           PreparedStatement ps1 = connection.prepareStatement(requeteInsertionRealisateur);
           PreparedStatement ps2 = connection.prepareStatement(requeteInsertionActeur);
           PreparedStatement ps3 = connection.prepareStatement(requeteInsertionPersonnage);
           PreparedStatement ps4 = connection.prepareStatement(requeteInsertionFilm);
           PreparedStatement ps5 = connection.prepareStatement(requeteInsertionScenariste);
           PreparedStatement ps6 = connection.prepareStatement(requeteInsertionGenre);
           PreparedStatement ps7 = connection.prepareStatement(requeteInsertionPays);
           PreparedStatement ps8 = connection.prepareStatement(requeteInsertionScenaristeFilm);
           PreparedStatement ps9 = connection.prepareStatement(requeteInsertionGenreFilm);
           PreparedStatement ps10 = connection.prepareStatement(requeteInsertionPaysProductionFilm);


              if(!realisateurs.contains(realisateurId)){
                 ps1.setInt(1, realisateurId);
                 ps1.setString(2, realisateurNom);
                 ps1.addBatch();
                 ps1.executeBatch();
                 ps1.close();
                 connection.commit();
                 realisateurs.add(realisateurId);
              }

              for (Role r : roles){
                 if(!acteurs.contains(r.id)){
                    System.out.println(r.id);
                    // ajout des acteurs
                    ps2.setInt(1, r.id);
                    ps2.addBatch();
                    ps2.executeBatch();

                    // ajout des roles
                    ps3.setInt(1, r.id);
                    ps3.setInt(2, id);
                    ps3.setString(3, r.nom);
                    ps3.executeBatch();
                    acteurs.add(r.id);
                 }
              }

              ps2.close();
              ps3.close();
              connection.commit();

           Random random = new Random();
           int nbCopies = random.nextInt(100) + 1;

           // insertion des films
           ps4.setInt(1, id);
           ps4.setString(2, titre);
           ps4.setInt(3, annee);
           ps4.setString(4, langue);
           ps4.setInt(5, duree);
           ps4.setString(6, resume);
           ps4.setString(7, poster);
           ps4.setInt(8, nbCopies);
           ps4.setInt(9, realisateurId);
           ps4.addBatch();

           ps4.executeBatch();
           ps4.close();
           connection.commit();
           System.out.println("Films insérés");

              // ajout des scenaristes
              for(String s : scenaristes){
                 if(!this.scenaristes.contains(s)){
                    ps5.setString(1, s);
                    ps5.addBatch();
                    ps5.executeBatch();
                    this.scenaristes.add(s);
                 }
                 ps8.setInt(1, this.scenaristes.indexOf(s)+1);
                 ps8.setInt(2, id);
                 ps8.addBatch();
                 ps8.executeBatch();
              }



              // ajout des genres
              for(String g : genres){
                 if(!this.genres.contains(g)){
                    ps6.setString(1, g);
                    ps6.addBatch();
                    this.genres.add(g);
                 }
                 ps9.setInt(1, this.genres.indexOf(g)+1);
                 ps9.setInt(2, id);
                 ps9.addBatch();
                 ps9.executeBatch();
              }

              // ajout des pays
              for(String p : pays){
                 if(!this.pays.contains(p)){
                    ps7.setString(1, p);
                    ps7.addBatch();
                    this.pays.add(p);
                 }
                 ps10.setInt(1,this.pays.indexOf(p)+1);
                 ps10.setInt(2, id);
                 ps10.addBatch();
                 ps10.executeBatch();
              }

              ps5.close();
              ps6.close();
              ps7.close();
              ps8.close();
              ps9.close();
              ps10.close();
              connection.commit();

        } catch (SQLException e) {
             System.out.println("Erreur lors de l'insertion du film dans la BD!");
             e.printStackTrace();
        }
   }

   private static void insererForfaits() {
      String insertForfaits = "INSERT INTO Forfait(codeForfait, nomForfait, coutMensuel, locationMax, dureeMax) VALUES (?, ?, ?, ?, ?)";
      try {
         try (PreparedStatement ps = connection.prepareStatement(insertForfaits)) {
            // Forfait debutant
            ps.setString(1, "D");
            ps.setString(2, "Debutant");
            ps.setInt(3, 5);
            ps.setInt(4, 1);
            ps.setString(5, "10 jours");
            ps.addBatch();

            // Forfait intermediaire
            ps.setString(1, "I");
            ps.setString(2, "Intermediaire");
            ps.setInt(3, 10);
            ps.setInt(4, 5);
            ps.setString(5, "30 jours");
            ps.addBatch();

            // Forfait avance
            ps.setString(1, "A");
            ps.setString(2, "Avance");
            ps.setInt(3, 15);
            ps.setInt(4, 10);
            ps.setString(5, "illimite");
            ps.addBatch();

            // execute batch
            ps.executeBatch();
            connection.commit();

            System.out.println("Forfaits insérés");
         } catch (Exception e) {
            e.printStackTrace();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   private void insertionClient(int id, String nomFamille, String prenom,
                             String courriel, String tel, String anniv,
                             String adresse, String ville, String province,
                             String codePostal, String carte, String noCarte,
                             int expMois, int expAnnee, String motDePasse,
                             String forfait) {
      // On le client dans la BD
   }
   
   private void connectionBD() {
      // On se connecte a la BD
      try {
         connection = DriverManager.getConnection("jdbc:oracle:thin:@//bdlog660.ens.ad.etsmtl.ca:1521/ORCLPDB.ens.ad.etsmtl.ca","EQUIPE213","ccnO9DXU");
         connection.setAutoCommit(false);
         System.out.println("Connexion reussie!");
      } catch (SQLException e) {
         System.out.println("Erreur de connexion a la BD!");
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      LectureBD lecture = new LectureBD();
      //lecture.insererForfaits();

      //lecture.lecturePersonnes(args[0]);
      lecture.lectureFilms();

      //String basePath = System.getProperty("user.dir") + File.separator + "Donnees" + File.separator;
      //lecture.lecturePersonnes(args[0]);
      //lecture.lectureFilms(basePath + "films_latin1.xml" );
      //lecture.lectureClients(args[2]);
   }
}
