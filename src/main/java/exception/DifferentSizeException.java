package exception;

public class DifferentSizeException extends Exception{
    public DifferentSizeException(){
        super("Both lists are of different sizes.");
    }
}
