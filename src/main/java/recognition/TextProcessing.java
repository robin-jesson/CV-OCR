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

    public TextProcessing(String text){
        this.fullText = text;
    }

    private String findPhone(){
        List<String> matches = Pattern.compile("\\+?([0-9][ .]?){6,12}[0-9]")
                .matcher(this.fullText)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        return matches.size()>=1 ? matches.get(matches.size()-1) : "";
    }

    private String findEmail(){
        /*
        [a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+
        ([a-zA-Z0-9-_.]+)@([a-zA-Z0-9-_.]+)[.-_]([a-zA-Z.]+)
         */
        List<String> matches = Pattern.compile("[a-zA-Z0-9._-].+@.+[a-zA-Z0-9._-]")
                .matcher(this.fullText)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        System.out.println(matches);
        return matches.size()>=1 ? matches.get(matches.size()-1) : ""; // get first email that appears (bottom to top reading)
    }

    private HashMap<String ,String> findNames(){
        HashMap<String, String> names = new HashMap<>();
        String email = this.findEmail();
        if(email.isEmpty()){
            names.put("lastname","");
            names.put("firstname","");
        }
        else{
            String identity = email.split("@")[0];
            String[] identities = identity.split("[.]");
            if(identities.length==2){
                names.put("firstname",identities[0]);
                names.put("lastname",identities[1]);
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
        String lastname = this.findNames().get("lastname");
        String firstname = this.findNames().get("firstname");
        String email = this.findEmail();
        String phone = this.findPhone();
        if(!lastname.isEmpty()) cvElts.put("lastname",lastname);
        if(!firstname.isEmpty()) cvElts.put("firstname",firstname);
        if(!email.isEmpty()) cvElts.put("email",email);
        if(!phone.isEmpty()) cvElts.put("phone",phone);
        return cvElts;
    }

    public static void main(String[] args) {
        String s = "Février 2017 à juillet 2017\n" +
                " Humanité et\n" +
                " dement-michel1@utt-fr\n" +
                "+33 6.04.08.92.45\n" +
                "15 rue Lucien Morel Payen,\n" +
                "10000 Troyes,";
        TextProcessing tp = new TextProcessing(s);
        System.out.println(tp.getCvInfo());
    }
}
