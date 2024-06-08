package Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLQuery {

    public Connection _connection;
    public static final String REQUETE_INSERTION_ADDRESSE = "INSERT INTO Addresse(idAdresse, rue, ville, province, codePostal) VALUES (?, ?, ?, ?, ?)";
    public static final String REQUETE_INSERTION_CARTECREDIT = "INSERT INTO CarteCredit(numeroCC, type, dateExpiration, cvv) VALUES (?, ?, ?, ?)";
    public static final String REQUETE_INSERTION_CLIENT = "INSERT INTO Client(idClient, email, numeroCC, numeroTelephone, motDePasse, idAdresse, codeForfait, dateNaissance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public SQLQuery(Connection connection) throws SQLException {
        this._connection = connection;
        this.statement_insertion_addresse = this._connection.prepareStatement(REQUETE_INSERTION_ADDRESSE);
        this.statement_insertation_carteCredit = this._connection.prepareStatement(REQUETE_INSERTION_CARTECREDIT);
        this.statement_insertion_client = this._connection.prepareStatement(REQUETE_INSERTION_CLIENT);
    }

    public PreparedStatement statement_insertion_addresse;
    public PreparedStatement statement_insertation_carteCredit;
    public PreparedStatement statement_insertion_client;
}
