public class Personne {
    public String Nom;
    public String DateNaissance;
    public int id;
    public String lieu;
    public String photo;
    public String bio;
    public Personne(String nom, String dateNaissance, int id, String lieu, String photo, String bio) {
        Nom = nom;
        DateNaissance = dateNaissance;
        this.id = id;
        this.lieu = lieu;
        this.photo = photo;
        this.bio = bio;
    }


}
