import java.util.ArrayList;
import java.util.List;


public class Film {
    public int filmId;
    public String titre;
    public int anneeSortie;
    public String langue;
    public String Resume;
    public int realisteurId;
    public int duree;
    public String affiche;
    public List<Integer> acteurs = new ArrayList<>();
    public List<String> scenaristesNom = new ArrayList<>();
    public List<String> pays = new ArrayList<>();
    public List<String> genres = new ArrayList<>();
    public List<String> bandeAnnonces = new ArrayList<>();
    public List<Role> roles = new ArrayList<>();

    public Film(int filmId, String titre, int anneeSortie, String langue, String resume, int duree, String affiche,
                int realisteurId, List<Integer> acteurs, List<String> scenaristesNom, List<String> pays,
                List<String> genres, List<String> bandeAnnonces, List<Role> roles) {
        this.filmId = filmId;
        this.titre = titre;
        this.anneeSortie = anneeSortie;
        this.langue = langue;
        this.duree = duree;
        this.affiche = affiche;
        this.Resume = resume;
        this.realisteurId = realisteurId;
        if (acteurs != null)
            this.acteurs.addAll(acteurs);
        if (scenaristesNom != null)
            this.scenaristesNom.addAll(scenaristesNom);
        if (pays != null)
            this.pays.addAll(pays);
        if (genres != null)
            this.genres.addAll(genres);
        if  (bandeAnnonces != null)
            this.bandeAnnonces.addAll(bandeAnnonces);
        if (roles != null)
            this.roles.addAll(roles);
    }
}