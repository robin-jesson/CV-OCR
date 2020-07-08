package recognition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextProcessing {
    private String fullText;
    private String[] words;

    public TextProcessing(String text){
        fullText = text;
        words = text.split("[ \n]");
    }

    private String findPhone2(){
        List<String> matches = Pattern.compile("\\+?([0-9]\\s?){6,15}").matcher(fullText).results()
                .map(MatchResult::group).collect(Collectors.toList());
        return matches.size()==1 ? matches.get(0) : "";
    }

    private String findEmail2(){
        List<String> matches = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]{2,3}").matcher(fullText).results()
                .map(MatchResult::group).collect(Collectors.toList());
        return matches.size()==1 ? matches.get(0) : "";
    }

    private HashMap<String ,String> findNames(){
        HashMap<String, String> names = new HashMap<>();
        String email = findEmail2();
        if(email.isEmpty()){
            names.put("lastname","");
            names.put("firstname","");
        }
        else{
            String identity = email.split("@")[0];
            String[] identities = identity.split("[.]");
            if(identities.length==2){
                names.put("lastname",identities[0]);
                names.put("firstname",identities[1]);
            }
            else if(identities.length==1){
                names.put("lastname",identities[0]);
                names.put("firstname","");
            }
            else{
                names.put("lastname","");
                names.put("firstname","");
            }
        }
        return names;
    }

    public HashMap<String ,String> getCvInfo(){
        HashMap<String, String> cvElts = new HashMap<>();
        cvElts.put("lastname",findNames().get("lastname"));
        cvElts.put("firstname",findNames().get("firstname"));
        cvElts.put("email",findEmail2());
        cvElts.put("phone",findPhone2());
        return cvElts;
    }

    public static void main(String[] args) {
        String s = "1\n" +
                "CENTRES D'INTÉRÊTS\n" +
                "Cinéma reconstitution historique ; Voyage et visite patrimoine historique ; Suivi de la Formule 1 ,Ï (âZQW\n" +
                "— 4\n" +
                " Févier 2017 — Juin 2017 {5 mois)\n" +
                "Projet universitaire en groupe d’étudiants informaticiens et un ingénieur mécanique au Danemark.\n" +
                "Réalisation d’un outil pour aider à jouer des morceaux au piano avec un appareil sous Raspberry Pi\n" +
                "et un jeu de LED vertes et blanches qui montrent les touches à jouer. ‘\n" +
                " Mars 2019 < Juin 2019 (4 mois)\n" +
                "Projet en groupe de synthèse d'images : création de formes 3D avec OpenGL\n" +
                " PROJETS RÉALISÉS\n" +
                "\n" +
                "Mars 2019 — Juin 2019 (4 mois)\n" +
                "Réalisation en groupe d’une simulation multi-agents en Sar!, représentation de conducteurs dans\n" +
                "une ville avec deux comnortements différents.\n" +
                " Été 2016 / Été 2017 {3 mois) : SOFRESCO {Fresnoy-le-Grand) :\n" +
                "Opérateur de conditionnement durant les étés, en charge de certaines lignes notamment le sucre et\n" +
                "étiquetsce RFID\n" +
                " Noël 2017 / Août 2018 / Août 2019 {2 mois) : LCL {Saint-Quentin}\n" +
                "Auxiliaire de vacances à l’accueil de l’agence pour renseigner les clients et donner les chéquiers,\n" +
                "rartes naniers rénondre aux demandes.\n" +
                " EXPÉRIENCES PROFESSIONNELLES\n" +
                "\n" +
                "Septembre 2018 — Février 2019 (5 mois) : Groupe Blondel (Saint-Quentin)\n" +
                "Stage d’assistant ingénieur informatique pour le Groupe Blondel (Transports). Recherche pour un\n" +
                "projet de dématérialisation ; Création d'outils en C#/SQL avec web services Transics pour faciliter des\n" +
                "process manuels ; Aides pour les outils informatiques ; rapports de Business Intelligence pour Airbus.\n" +
                " lanqgues étrongères\n" +
                " COMPÉTENCES\n" +
                "\n" +
                "Langages Java, C++, Cä, R, MATLAB, SARL, HTML/CSS, PHP, SQL\n" +
                "\n" +
                "logiciels intellil/Eclipse, Visual Studio, RStudio, PowerBi & Office, SQL Server\n" +
                "\n" +
                "En cours d'apprentissage = Unity {réalité virtuelle}, OpenCV {traitement des images}, Java EF, Machine\n" +
                "Learning (reconnaissance de formes}, Swift (Développement iOS}\n" +
                "\n" +
                "lanques étrangères Anglais courant {BULATS 85/100), Allemand scolaire\n" +
                " 2012 - 2015 : Espace Scolaire Condorcet Saint-Quentin\n" +
                "Baccalauréat scientifique SVT — option Informatique Sciences Numériques — Section Européenne.\n" +
                " 2015 — 2017 :IUT de Reims\n" +
                "DUT Informatique — 4° semestre réalisé au VIA University College d’Horsens au Danemark,\n" +
                " FORMATION\n" +
                "2017 — (2020) : Université de Technologie de Beifort-Montbéliard\n" +
                "3* année de diplôme d’ingénieur en informatique — filière Image Interaction et Réalité Virtuelle.\n" +
                " 16 rue du tanguedorc — 07100 Lesdins\n" +
                "+33 6 20 86 38 93 robin jesson@utbm.fr\n" +
                "19 juin 1597 — 72 ans\n" +
                " imagerie et réalité virtuelle\n" +
                " Robin JESSON\n" +
                " Étudiant ingénieur informatique,\n" +
                " -—\"";
        TextProcessing tp = new TextProcessing(s);
        System.out.println(tp.getCvInfo());
    }
}
