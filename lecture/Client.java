public class Client{
    public String nom;
    public String motDePasse;
    public String courriel;
    public String numTel;
    public String adresse;
    public int clientId;
    public String anniv;
    public String carte;
    public String noCarte;
    public int expMois;
    public int expAnnee;
    public String forfait;


    public Client(String nom, String motDePasse, String courriel, String numTel, String adresse, String province, String codePostal, int clientId,
                  String anniv, String carte, String noCarte, int expMois, int expAnnee, String forfait) {
        this.nom = nom;
        this.motDePasse = motDePasse;
        this.courriel = courriel;
        this.numTel = numTel;
        this.adresse = adresse + ", " + province + ", " + codePostal;
        this.clientId = clientId;
        this.anniv = anniv;
        this.carte = carte;
        this.noCarte = noCarte;
        this.expMois = expMois;
        this.expAnnee = expAnnee;
        this.forfait = forfait;
    }

}
