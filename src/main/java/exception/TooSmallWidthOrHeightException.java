package exception;

public class TooSmallWidthOrHeightException extends Exception{
    public TooSmallWidthOrHeightException(){
        super("The area is too small for letter detection.");
    }
}
