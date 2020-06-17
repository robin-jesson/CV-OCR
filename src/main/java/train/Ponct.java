package train;

public enum Ponct {
    at('@'),
    brack_l('('),
    brack_r(')'),
    coma(','),
    equa('='),
    exclam('!'),
    guill_l('«'),
    guill_r('»'),
    interrog('?'),
    hyphen('-'),
    plus('+'),
    point('.'),
    semic(';'),
    sharp('#');

    private char c;
    private Ponct(char c){
        this.c = c;
    }


    public char getC() {
        return c;
    }
}
