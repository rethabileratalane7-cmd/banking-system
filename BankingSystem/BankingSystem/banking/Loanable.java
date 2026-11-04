package banking;
//interface implementation
public interface Loanable
{
    double getLoanLimit();
    void requestLoan(double amount) throws InvalidTransactionException;
}
