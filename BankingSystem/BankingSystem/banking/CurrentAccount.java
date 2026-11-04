package banking;

public final class CurrentAccount extends BankAccount implements Loanable
{

    private static final double OVERDRAFT_LIMIT = 2000.00;
    private static final double LOAN_LIMIT = 10000.00;

    public CurrentAccount(String accountNumber, String holderName, double openingBalance)
            throws InvalidTransactionException
    {
        super(accountNumber, holderName, openingBalance);
    }

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
        if (getBalance() - amount < -OVERDRAFT_LIMIT)
        {
            throw new InsufficientFundsException("Overdraft limit of M2000.00 exceeded.");
        }
        setBalance(getBalance() - amount);
    }

    @Override
    public double calculateInterest()
    {
        log("INTEREST", 0, "No interest for current accounts");
        return 0;
    }

    @Override
    public String getAccountType() {
        return "Current Account";
    }

    @Override
    public double getLoanLimit() {
        return LOAN_LIMIT;
    }

    @Override
    public void requestLoan(double amount) throws InvalidTransactionException
    {
        if (amount <= 0 || amount > LOAN_LIMIT)
        {
            log("LOAN", amount, "REJECTED - Loan must be positive and not above M10000.00");
            throw new InvalidTransactionException(
                    "Loan must be positive and not above M" + LOAN_LIMIT + ".");
        }
        setBalance(getBalance() + amount);
        log("LOAN", amount, "SUCCESS - Loan approved");
    }

    @Override
    public void displayDetails()
    {
        super.displayDetails();
        System.out.println("Overdraft Limit: M" + OVERDRAFT_LIMIT);
    }
}
