import java.io.*;

import java.sql.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import Algorithme.UniqueCVVGenerator;
import Query.SQLQuery;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class LectureBD {

   // Indiquer les chemins des fichiers xml
   final static String PERSONNE_PATH = "lecture/Donnees/personnes_latin1.xml"
           , CLIENTS_PATH = "lecture/Donnees/clients_latin1.xml"
           , FILMS_PATH = "lecture/Donnees/films_latin1.xml";

   static Connection connection;


   private ArrayList<Integer> realisateurs = new ArrayList<Integer>();
   private ArrayList<Integer> acteurs = new ArrayList<Integer>();
   private HashMap<String,Integer> scenaristes = new HashMap<>();
   private HashMap<String,Integer> genres = new HashMap<>();
   private HashMap<String,Integer> pays = new HashMap<>();

   private ArrayList<String> bandesAnnonce = new ArrayList<String>();

   private ArrayList<Film> Films = new ArrayList<>();
   private ArrayList<Personne> personnes = new ArrayList<>();

   private UniqueCVVGenerator cvvGenerator = new UniqueCVVGenerator();

   private SQLQuery sqlQuery;

   static int GENRE_COUNT = 0;
   static int PAYS_COUNT = 0;

   static int SCENARISTE_COUNT = 0;

   static int CODE_COPIE = 1;

   static int ADDRESS_ID_COUNT = 1;

   private static final int BATCH_SIZE = 1000;

   private static int NBR_BATCH_INSERTION_CLIENT = 0;

   public static int clientCounter = 0;

   public LectureBD() throws SQLException {
      connectionBD();
      this.sqlQuery = new SQLQuery(connection);
   }
   
   
   public void lecturePersonnes(){
      try {
         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
         XmlPullParser parser = factory.newPullParser();

         InputStream is = new FileInputStream(PERSONNE_PATH);
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
         System.out.println("IOException while parsing " + PERSONNE_PATH);
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
               {
                  realisateurId = Integer.parseInt(parser.getAttributeValue(0));
                  //System.out.println("réalisateur ajoutée" + " " + parser.getAttributeValue(0));
               }
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
                  else if (tag.equals("annonce")) {
                     annonces.add(parser.getText());
                  }
               }              
            }
            
            eventType = parser.next();            
         }
         System.out.println("Insertion des films terminé");
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + FILMS_PATH);
      }
   }
   
   public void lectureClients(String nomFichier){
      System.out.println("Début insertion client");
      System.out.println("");
      int clientCounter = 0;
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

                  clientCounter++;

                  //System.out.println("Client numéro " + clientCounter + " ajouté");
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
         // Executer les batch restante
         this.sqlQuery.statement_insertion_addresse.executeBatch();
         this.sqlQuery.statement_insertation_carteCredit.executeBatch();
         this.sqlQuery.statement_insertion_client.executeBatch();

         connection.commit();

         this.sqlQuery.statement_insertion_addresse.close();
         this.sqlQuery.statement_insertation_carteCredit.close();
         this.sqlQuery.statement_insertion_client.close();

         System.out.println("");
         System.out.println("Insertion client terminé");
      }
      catch (XmlPullParserException e) {
          System.out.println(e);   
      }
      catch (IOException e) {
         System.out.println("IOException while parsing " + nomFichier); 
      } catch (SQLException e) {
          throw new RuntimeException(e);
      }
   }   
   
   private void insertionPersonne(int id, String nom, String anniv, String lieu, String photo, String bio) {
      String insertAc = "INSERT INTO Personne(idPersonne, nom, lieunaissance, datenaissance, photo, biographie) VALUES (?, ?, ?, ?, ? ,?)";
      personnes.add(new Personne(nom, anniv, id, lieu, photo, bio));
      try {
         System.out.println("Insertion de la personne " + id + " dans la BD");
         PreparedStatement ps = connection.prepareStatement(insertAc);
         ps.setInt(1, id);
         ps.setString(2, nom);
         ps.setString(3, lieu);
         ps.setString(4, anniv);
         ps.setString(5, photo);
         ps.setString(6, bio);
         ps.addBatch();
         ps.executeBatch();
         ps.close();
         connection.commit();
      } catch (SQLException e) {
         System.out.println("Erreur lors de l'insertion de la personne dans la BD!");
         e.printStackTrace();
      }
   }

   private void insertionFilm(int id, String titre, int annee,
                              ArrayList<String> pays, String langue, int duree, String resume,
                              ArrayList<String> genres, String realisateurNom, int realisateurId,
                              ArrayList<String> scenaristes,
                              ArrayList<Role> roles, String poster,
                              ArrayList<String> annonces) {

      String requeteInsertionRealisateur = "INSERT INTO Realisateur(idRealisateur) VALUES (?)";
      String requeteInsertionActeur = "INSERT INTO Acteur(idActeur) VALUES (?)";
      String requeteInsertionPersonnage = "INSERT INTO Personnage(idActeur, idFilm, Nom) VALUES (?,?,?)";
      String requeteInsertionFilm = "INSERT INTO Film(idFilm, titre, annee, langueOriginale, dureeFilm, resumeFilm, affiche, nombreCopie, idrealisateur) VALUES (?,?, ?, ?, ?, ? ,? ,? ,?)";
      String requeteInsertionScenariste = "INSERT INTO Scenariste(idScenariste,nom) VALUES (?,?)";
      String requeteInsertionGenre = "INSERT INTO Genre(idGenre,nom) VALUES (?,?)";
      String requeteInsertionPays = "INSERT INTO Pays(idPays,nom) VALUES (?,?)";
      String requeteInsertionScenaristeFilm = "INSERT INTO ScenaristeFilm(idScenariste, idFilm) VALUES (?, ?)";
      String requeteInsertionGenreFilm = "INSERT INTO GenreFilm(idGenre, idFilm) VALUES (?, ?)";
      String requeteInsertionPaysProductionFilm = "INSERT INTO PaysProductionFilm(idPays, idFilm) VALUES (?, ?)";
      String requeteInsertionBandeAnnonce = "INSERT INTO BandeAnnonce(titre, idFilm) VALUES (?, ?)";
      String requeteInsertionCopieFilm = "INSERT INTO CopieFilm(codeCopie, disponible, idFilm) VALUES (?, ?, ?)";

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
         PreparedStatement ps11 = connection.prepareStatement(requeteInsertionBandeAnnonce);
         PreparedStatement ps12 = connection.prepareStatement(requeteInsertionCopieFilm);

         if(!realisateurs.contains(realisateurId)){
            if(realisateurId > 0) {
               ps1.setInt(1, realisateurId);
               ps1.addBatch();
               ps1.executeBatch();
               ps1.close();
               realisateurs.add(realisateurId);
            }
         }


         Random random = new Random();
         int nbCopies = random.nextInt(10) + 1;
         // insertion des films
         ps4.setInt(1, id);
         ps4.setString(2, titre);
         ps4.setInt(3, annee);
         ps4.setString(4, langue);
         ps4.setInt(5, duree);
         ps4.setString(6, resume);
         ps4.setString(7, poster);
         ps4.setInt(8, nbCopies);
         if(realisateurId > 0)
            ps4.setInt(9, realisateurId);
         else
            ps4.setNull(9, Types.NULL);

         ps4.addBatch();

         ps4.executeBatch();
         ps4.close();
         System.out.println("Films insérés: ");

         // Insertion copieFilm
         for(int i = 0; i < nbCopies; i++)
         {
            ps12.setInt(1, CODE_COPIE);
            ps12.setInt(2, 1);
            ps12.setInt(3, id);
            ps12.addBatch();
            ps12.executeBatch();
            CODE_COPIE++;
         }
         ps12.close();

         for (Role r : roles){
            if(!acteurs.contains(r.id)){

               // ajout des acteurs
               ps2.setInt(1, r.id);
               ps2.addBatch();
               ps2.executeBatch();

               acteurs.add(r.id);
            }
            if(acteurs.contains(r.id)){

               // ajout des roles
               ps3.setInt(1, r.id);
               ps3.setInt(2, id);
               ps3.setString(3, r.nom);
               ps3.addBatch();
               ps3.executeBatch();
            }

         }

         ps2.close();
         ps3.close();

         // ajout des scenaristes
         for(String s : scenaristes){
            if(!this.scenaristes.containsKey(s)){
               SCENARISTE_COUNT = this.scenaristes.size()+1;
               this.scenaristes.put(s,SCENARISTE_COUNT);

               ps5.setInt(1, SCENARISTE_COUNT);
               ps5.setString(2, s);
               ps5.addBatch();
               ps5.executeBatch();
            }
            if(this.scenaristes.containsKey(s)){
                ps8.setInt(1, this.scenaristes.get(s));
                ps8.setInt(2, id);
                ps8.addBatch();
                ps8.executeBatch();
            }

         }



         // ajout des genres
         for(String g : genres){
            if(!this.genres.containsKey(g)){
               GENRE_COUNT = this.genres.size()+1;
               this.genres.put(g,GENRE_COUNT);
               ps6.setInt(1, GENRE_COUNT);
               ps6.setString(2, g);
               ps6.addBatch();
               ps6.executeBatch();
            }
            if(this.genres.containsKey(g)) {
               ps9.setInt(1, this.genres.get(g));
               ps9.setInt(2, id);
               ps9.addBatch();
               ps9.executeBatch();
            }
         }

         // ajout des pays
         for(String p : pays){
            if(!this.pays.containsKey(p)){
               PAYS_COUNT = this.pays.size()+1;
               this.pays.put(p,PAYS_COUNT);
               ps7.setInt(1, PAYS_COUNT);
               ps7.setString(2, p);
               ps7.addBatch();
                ps7.executeBatch();
            }
            if(this.pays.containsKey(p)){
               ps10.setInt(1,this.pays.get(p));
               ps10.setInt(2, id);
               ps10.addBatch();
               ps10.executeBatch();
            }

         }

         for(String b : annonces){
            if(!this.bandesAnnonce.contains(b)){
               ps11.setString(1, b);
               ps11.setInt(2, id);
               ps11.addBatch();
               ps11.executeBatch();

               this.bandesAnnonce.add(b);
            }
         }

         ps5.close();
         ps6.close();
         ps7.close();
         ps8.close();
         ps9.close();
         ps10.close();
         ps11.close();
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
      try {
         // Ajout de l'adresse
         sqlQuery.statement_insertion_addresse.setInt(1, ADDRESS_ID_COUNT);
         sqlQuery.statement_insertion_addresse.setString(2, adresse);
         sqlQuery.statement_insertion_addresse.setString(3, ville);
         sqlQuery.statement_insertion_addresse.setString(4, province);
         // Retrait des espaces dans le code postale
         codePostal = codePostal.replaceAll("\\s", "");
         sqlQuery.statement_insertion_addresse.setString(5, codePostal);
         sqlQuery.statement_insertion_addresse.addBatch();
         NBR_BATCH_INSERTION_CLIENT++;

         //Ajout Carte de crédit
         // Retrait des espaces dans la carte de crédit
         noCarte = noCarte.replaceAll("\\s", "");
         sqlQuery.statement_insertation_carteCredit.setString(1, noCarte);
         sqlQuery.statement_insertation_carteCredit.setString(2, carte);
         sqlQuery.statement_insertation_carteCredit.setDate(3, new Date(expAnnee, expMois, 1));
         sqlQuery.statement_insertation_carteCredit.setString(4, this.cvvGenerator.generateCVV());
         sqlQuery.statement_insertation_carteCredit.addBatch();
         NBR_BATCH_INSERTION_CLIENT++;

         // Ajout client
         sqlQuery.statement_insertion_client.setInt(1, id);
         sqlQuery.statement_insertion_client.setString(2, courriel);
         sqlQuery.statement_insertion_client.setString(3, noCarte);
         // Retrait des - dans le numéros de téléphone
         tel = tel.replace("-", "");
         sqlQuery.statement_insertion_client.setString(4, tel);
         sqlQuery.statement_insertion_client.setString(5, motDePasse);
         sqlQuery.statement_insertion_client.setInt(6, ADDRESS_ID_COUNT);
         sqlQuery.statement_insertion_client.setString(7, forfait);
         // Convertion du string anniversaire en type Date
         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
         java.util.Date dateJava = dateFormat.parse(anniv);
         sqlQuery.statement_insertion_client.setDate(8, new Date(dateJava.getTime()));
         sqlQuery.statement_insertion_client.addBatch();
         NBR_BATCH_INSERTION_CLIENT++;

         clientCounter++;

         System.out.println("Client numéro " + clientCounter + " ajouté");

         // Executer toutes les baches et les fermer et incrémentation de la batch
         if(NBR_BATCH_INSERTION_CLIENT == 330)
         {
            sqlQuery.statement_insertion_addresse.executeBatch();
            sqlQuery.statement_insertation_carteCredit.executeBatch();
            sqlQuery.statement_insertion_client.executeBatch();
            NBR_BATCH_INSERTION_CLIENT = 0;
            System.out.println("Batch restart from 0");
         }

         ADDRESS_ID_COUNT++;
      }
      catch (SQLException e)
      {
         System.out.println("Erreur lors de l'insertion du client dans la BD!");
         e.printStackTrace();
      } catch (ParseException e) {
          throw new RuntimeException(e);
      }
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

   public static void main(String[] args) throws SQLException {
      LectureBD lecture = new LectureBD();
      //lecture.insererForfaits();
      //lecture.lecturePersonnes(args[0]);
      //lecture.lecturePersonnes();
      //lecture.lectureFilms();
      lecture.lectureClients(CLIENTS_PATH);

      //lecture.insertionFilm();

      //String basePath = System.getProperty("user.dir") + File.separator + "Donnees" + File.separator;
      //lecture.lecturePersonnes(args[0]);
      //lecture.lectureFilms(basePath + "films_latin1.xml" );
      //lecture.lectureClients(args[2]);
   }
}
