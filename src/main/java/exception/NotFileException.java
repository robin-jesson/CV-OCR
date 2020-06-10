package exception;

public class NotFileException extends Exception{
    public NotFileException(){
        super("File is not detected.");
    }
}
