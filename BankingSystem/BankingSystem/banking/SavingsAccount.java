package banking;
//inheritance
public final class SavingsAccount extends BankAccount implements Loanable
{

    private static final double MINIMUM_BALANCE = 100.00;
    private static final double INTEREST_RATE = 0.05;
    private static final double LOAN_LIMIT = 5000.00;

    public SavingsAccount(String accountNumber, String holderName, double openingBalance)
            throws InvalidTransactionException
    {
        super(accountNumber, holderName, openingBalance);
    }
//overriding
    @Override
    public void deposit(double amount) throws InvalidTransactionException
    {
        if (amount <= 0)
        {
            throw new InvalidTransactionException("Deposit must be greater than zero.");
        }
        setBalance(getBalance() + amount);
    }

    @Override
    public void withdraw(double amount)
            throws InvalidTransactionException, InsufficientFundsException
    {
        if (amount <= 0)
        {
            throw new InvalidTransactionException("Withdrawal must be greater than zero.");
        }
        if (getBalance() - amount < MINIMUM_BALANCE)
        {
            throw new InsufficientFundsException(
                    "Savings account must keep a minimum balance of M100.00.");
        }
        setBalance(getBalance() - amount);
    }

    @Override
    public double calculateInterest()
    {
        double interest = getBalance() * INTEREST_RATE;
        // Add 5% of the current balance to the account.
        setBalance(getBalance() + interest);
        log("INTEREST_ADDED", interest, "SUCCESS");
        return interest;
    }

    @Override
    public String getAccountType() {
        return "Savings Account";
    }

    @Override
    public double getLoanLimit() {
        return LOAN_LIMIT;
    }

    @Override
    //interface implementation
    public void requestLoan(double amount) throws InvalidTransactionException
    {
        if (amount <= 0 || amount > LOAN_LIMIT)
        {
            throw new InvalidTransactionException("Loan request outside valid limits.");
        }
        setBalance(getBalance() + amount);
        log("LOAN_CREDIT", amount, "APPROVED");
    }

    @Override
    public void displayDetails()
    {
        super.displayDetails();
        System.out.println("Minimum Balance: M" + MINIMUM_BALANCE);
    }
}
