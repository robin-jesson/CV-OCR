package train;

public enum Special {
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
    sharp('#'),

    agrave('à'),
    eacute('é'),
    ecirc('ê'),
    egrave('è'),
    euml('ë'),
    i('i'),
    icirc('î'),
    iuml('ï'),
    j('j'),
    ocirc('ô'),
    ouml('ö'),
    ucirc('û'),
    uuml('ü');

    private char c;
    private Special(char c){
        this.c = c;
    }


    public char getC() {
        return c;
    }
}
