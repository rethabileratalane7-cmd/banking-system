package banking;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
//abstract class
public abstract class BankAccount
{
    //encapsulation..
    private String accountNumber;
    private String holderName;
    private double balance;

    public BankAccount(String accountNumber, String holderName, double openingBalance)
            throws InvalidTransactionException
    {
        if (openingBalance < 0)
        {
            throw new InvalidTransactionException("Opening balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = openingBalance;
    }
  //setters and getters
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    protected void log(String type, double amount, String result)
    {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("transactions.txt", true)))
        {
            String logEntry = String.format(
                    "%s | %s | %s | Amount: M%.2f | Balance: M%.2f | %s",
                    new Date(), accountNumber, type, amount, balance, result);
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e)
        {
            System.err.println("Transaction logging failed: " + e.getMessage());
        }
    }
  //abstract method
    public abstract void deposit(double amount) throws InvalidTransactionException;

    public abstract void withdraw(double amount)
            throws InvalidTransactionException, InsufficientFundsException;

    public abstract double calculateInterest();

    public abstract String getAccountType();

    public void transfer(BankAccount target, double amount)
            throws InvalidTransactionException, InsufficientFundsException
    {
        if (target == this)
        {
            log("TRANSFER", amount, "REJECTED - Same account target");
            throw new InvalidTransactionException("Cannot transfer to the same account.");
        }
        if (amount <= 0)
        {
            log("TRANSFER", amount, "REJECTED - Zero or negative amount");
            throw new InvalidTransactionException("Transfer must be greater than zero.");
        }
        withdraw(amount);
        target.deposit(amount);
        log("TRANSFER", amount, "SUCCESS - Sent to " + target.getAccountNumber());
    }

    public void displayDetails()
    {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Holder: " + holderName);
        System.out.println("Type: " + getAccountType());
        System.out.println("Balance: M" + balance);
    }
}
