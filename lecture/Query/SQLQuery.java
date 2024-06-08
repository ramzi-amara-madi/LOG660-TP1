package Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLQuery {

    public Connection _connection;

    public static final String REQUETE_INSERTION_PERSONNE = "INSERT INTO Personne(idPersonne, nom, lieunaissance, datenaissance, photo, biographie) VALUES (?, ?, ?, ?, ? ,?)";
    public static final String REQUETE_INSERTION_Realisateur = "INSERT INTO Realisateur(idRealisateur) VALUES (?)";
    public static final String REQUETE_INSERTION_Acteur = "INSERT INTO Acteur(idActeur) VALUES (?)";
    public static final String REQUETE_INSERTION_Personnage = "INSERT INTO Personnage(idActeur, idFilm, Nom) VALUES (?,?,?)";
    public static final String REQUETE_INSERTION_Film = "INSERT INTO Film(idFilm, titre, annee, langueOriginale, dureeFilm, resumeFilm, affiche, nombreCopie, idrealisateur) VALUES (?,?, ?, ?, ?, ? ,? ,? ,?)";
    public static final String REQUETE_INSERTION_Scenariste = "INSERT INTO Scenariste(idScenariste,nom) VALUES (?,?)";
    public static final String REQUETE_INSERTION_Genre = "INSERT INTO Genre(idGenre,nom) VALUES (?,?)";
    public static final String REQUETE_INSERTION_Pays = "INSERT INTO Pays(idPays,nom) VALUES (?,?)";
    public static final String REQUETE_INSERTION_ScenaristeFilm = "INSERT INTO ScenaristeFilm(idScenariste, idFilm) VALUES (?, ?)";
    public static final String REQUETE_INSERTION_GenreFilm = "INSERT INTO GenreFilm(idGenre, idFilm) VALUES (?, ?)";
    public static final String REQUETE_INSERTION_PaysProductionFilm = "INSERT INTO PaysProductionFilm(idPays, idFilm) VALUES (?, ?)";
    public static final String REQUETE_INSERTION_BandeAnnonce = "INSERT INTO BandeAnnonce(titre, idFilm) VALUES (?, ?)";
    public static final String REQUETE_INSERTION_CopieFilm = "INSERT INTO CopieFilm(codeCopie, disponible, idFilm) VALUES (?, ?, ?)";

    public static final String REQUETE_INSERTION_ADDRESSE = "INSERT INTO Addresse(idAdresse, rue, ville, province, codePostal) VALUES (?, ?, ?, ?, ?)";
    public static final String REQUETE_INSERTION_CARTECREDIT = "INSERT INTO CarteCredit(numeroCC, type, dateExpiration, cvv) VALUES (?, ?, ?, ?)";
    public static final String REQUETE_INSERTION_CLIENT = "INSERT INTO Client(idClient, email, numeroCC, numeroTelephone, motDePasse, idAdresse, codeForfait, dateNaissance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public SQLQuery(Connection connection) throws SQLException {
        this._connection = connection;
        this.statement_insertion_Personne = this._connection.prepareStatement(REQUETE_INSERTION_PERSONNE);
        this.statement_insertion_Realisateur = this._connection.prepareStatement(REQUETE_INSERTION_Realisateur);
        this.statement_insertion_Acteur = this._connection.prepareStatement(REQUETE_INSERTION_Acteur);
        this.statement_insertion_Personnage = this._connection.prepareStatement(REQUETE_INSERTION_Personnage);
        this.statement_insertion_Film = this._connection.prepareStatement(REQUETE_INSERTION_Film);
        this.statement_insertion_Scenariste = this._connection.prepareStatement(REQUETE_INSERTION_Scenariste);
        this.statement_insertion_Genre = this._connection.prepareStatement(REQUETE_INSERTION_Genre);
        this.statement_insertion_Pays = this._connection.prepareStatement(REQUETE_INSERTION_Pays);
        this.statement_insertion_ScenaristeFilm = this._connection.prepareStatement(REQUETE_INSERTION_ScenaristeFilm);
        this.statement_insertion_GenreFilm = this._connection.prepareStatement(REQUETE_INSERTION_GenreFilm);
        this.statement_insertion_PaysProductionFilm = this._connection.prepareStatement(REQUETE_INSERTION_PaysProductionFilm);
        this.statement_insertion_BandeAnnonce = this._connection.prepareStatement(REQUETE_INSERTION_BandeAnnonce);
        this.statement_insertion_CopieFilm = this._connection.prepareStatement(REQUETE_INSERTION_CopieFilm);
        this.statement_insertion_addresse = this._connection.prepareStatement(REQUETE_INSERTION_ADDRESSE);
        this.statement_insertation_carteCredit = this._connection.prepareStatement(REQUETE_INSERTION_CARTECREDIT);
        this.statement_insertion_client = this._connection.prepareStatement(REQUETE_INSERTION_CLIENT);
    }

    public PreparedStatement statement_insertion_Personne;
    public PreparedStatement statement_insertion_Realisateur;
    public PreparedStatement statement_insertion_Acteur;
    public PreparedStatement statement_insertion_Personnage;
    public PreparedStatement statement_insertion_Film;
    public PreparedStatement statement_insertion_Scenariste;
    public PreparedStatement statement_insertion_Genre;
    public PreparedStatement statement_insertion_Pays;
    public PreparedStatement statement_insertion_ScenaristeFilm;
    public PreparedStatement statement_insertion_GenreFilm;
    public PreparedStatement statement_insertion_PaysProductionFilm;
    public PreparedStatement statement_insertion_BandeAnnonce;
    public PreparedStatement statement_insertion_CopieFilm;


    public PreparedStatement statement_insertion_addresse;
    public PreparedStatement statement_insertation_carteCredit;
    public PreparedStatement statement_insertion_client;
}
