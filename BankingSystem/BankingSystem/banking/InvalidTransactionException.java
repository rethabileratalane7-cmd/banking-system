package banking;
//implementation of inheritance
public class InvalidTransactionException extends Exception
{
    public InvalidTransactionException(String msg) {
        super(msg);
    }
}
