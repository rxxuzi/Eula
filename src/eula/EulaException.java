package eula;

public class EulaException extends Exception{
    public EulaException(String message, Throwable throwable) {
        super(message, throwable);
    }
    public EulaException(String message) {
        super(message);
    }
    public EulaException(Throwable throwable){
        super(throwable);
    }
}
