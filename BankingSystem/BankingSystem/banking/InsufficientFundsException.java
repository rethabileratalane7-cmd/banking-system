package banking;
//implementation of inheritance
public class InsufficientFundsException extends Exception
{
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
