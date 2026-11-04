import banking.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class BankingSystemMain
{
    private static final Path TRANSACTION_FILE =
            Paths.get(System.getProperty("user.dir"), "transactions.txt");

    public static void main(String[] args)
    {    //user input
        Scanner sc = new Scanner(System.in);
        System.out.println("Transaction history is being saved to: "
                + TRANSACTION_FILE.toAbsolutePath());

        BankAccount[] accounts = new BankAccount[4];
        //exception handling
        try
        {  //polymorphism
            accounts[0] = new SavingsAccount("901020408", "Rethabile Ratalane", 1950.00);
            accounts[1] = new SavingsAccount("901020409", "Bishop Mateu", 500.00);
            accounts[2] = new CurrentAccount("901020402", "Tlhokomelo Molise", 2500.00);
            accounts[3] = new CurrentAccount("901020412", "Karabelo Ponya", 300.00);
        } catch (InvalidTransactionException e)
        {
            System.out.println("Could not create accounts: " + e.getMessage());
            logTransaction("SYSTEM", "INIT", 0, 0, "FAILED: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running)
        {   //display menu
            System.out.println();
            System.out.println("Menu");
            System.out.println("1. Display all accounts");
            System.out.println("2. Deposit money");
            System.out.println("3. Withdraw money");
            System.out.println("4. Transfer money");
            System.out.println("5. Calculate interest");
            System.out.println("6. Request bank loan");
            System.out.println("7. Display account details");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");

            // Keep track of parameters for logging if exceptions hit
            String activeAccNum = "UNKNOWN";
            String transType = "UNKNOWN";
            double transAmount = 0.0;

            try
            {    // if else statement
                int choice;
                if (sc.hasNextInt())
                {
                    choice = sc.nextInt();
                    sc.nextLine();
                } else {
                    sc.nextLine();
                    throw new InvalidTransactionException("Invalid menu choice. Please enter a number.");
                }

                //switch statement
                switch (choice)
                {
                    case 1:
                        for (int i = 0; i < accounts.length; i++)
                        {
                            accounts[i].displayDetails();
                            System.out.println("------------------------");
                        }
                        break;
                    case 2:
                        transType = "DEPOSIT";
                        BankAccount depositAcc = null;
                        try
                        {
                            String requestedAccNum = readAccountNumber(sc, "Account number: ");
                            activeAccNum = requestedAccNum;
                            depositAcc = findAccount(accounts, requestedAccNum);
                            activeAccNum = depositAcc.getAccountNumber();
                            transAmount = readAmount(sc, "Amount to deposit: M");

                            depositAcc.deposit(transAmount);
                            System.out.println("Deposit successful.");
                            logTransaction(activeAccNum, transType, transAmount,
                                    depositAcc.getBalance(), "SUCCESS");
                        } catch (InvalidTransactionException e)
                        {
                            System.out.println("Error: " + e.getMessage());
                            logTransaction(activeAccNum, transType, transAmount,
                                    depositAcc == null ? 0.0 : depositAcc.getBalance(),
                                    "REJECTED: " + e.getMessage());
                        } catch (NumberFormatException e)
                        {
                            System.out.println("Error: Please enter a valid amount.");
                            logTransaction(activeAccNum, transType, transAmount,
                                    depositAcc == null ? 0.0 : depositAcc.getBalance(),
                                    "REJECTED: Invalid amount format");
                        }
                        break;
                    case 3:
                        transType = "WITHDRAW";
                        BankAccount withdrawAcc = null;
                        try
                        {
                            String requestedAccNum = readAccountNumber(sc, "Account number: ");
                            activeAccNum = requestedAccNum;
                            withdrawAcc = findAccount(accounts, requestedAccNum);
                            activeAccNum = withdrawAcc.getAccountNumber();
                            transAmount = readAmount(sc, "Amount to withdraw: M");

                            withdrawAcc.withdraw(transAmount);
                            System.out.println("Withdrawal successful.");
                            logTransaction(activeAccNum, transType, transAmount,
                                    withdrawAcc.getBalance(), "SUCCESS");
                        } catch (InsufficientFundsException e)
                        {
                            System.out.println("Error: " + e.getMessage());
                            logTransaction(activeAccNum, transType, transAmount,
                                    withdrawAcc == null ? 0.0 : withdrawAcc.getBalance(),
                                    "REJECTED: " + e.getMessage());
                        } catch (InvalidTransactionException e)
                        {
                            System.out.println("Error: " + e.getMessage());
                            logTransaction(activeAccNum, transType, transAmount,
                                    withdrawAcc == null ? 0.0 : withdrawAcc.getBalance(),
                                    "REJECTED: " + e.getMessage());
                        } catch (NumberFormatException e)
                        {
                            System.out.println("Error: Please enter a valid amount.");
                            logTransaction(activeAccNum, transType, transAmount,
                                    withdrawAcc == null ? 0.0 : withdrawAcc.getBalance(),
                                    "REJECTED: Invalid amount format");
                        }
                        break;
                    case 4:
                        transType = "TRANSFER";
                        BankAccount from = findAccount(accounts, sc, "Transfer from account: ");
                        activeAccNum = from.getAccountNumber();
                        BankAccount to = findAccount(accounts, sc, "Transfer to account: ");
                        transAmount = readAmount(sc, "Amount to transfer: M");

                        from.transfer(to, transAmount);
                        System.out.println("Transfer successful.");
                        logTransaction(activeAccNum, transType, transAmount, from.getBalance(), "SUCCESS to " + to.getAccountNumber());
                        break;
                    case 5:
                        transType = "INTEREST";
                        BankAccount interestAcc = findAccount(accounts, sc, "Account number: ");
                        activeAccNum = interestAcc.getAccountNumber();
                        double interest = interestAcc.calculateInterest();
                        System.out.println("Interest earned: M" + interest);
                        logTransaction(activeAccNum, transType, interest, interestAcc.getBalance(), "SUCCESS");
                        break;
                    case 6:
                        transType = "LOAN";
                        BankAccount loanAcc = findAccount(accounts, sc, "Account number: ");
                        activeAccNum = loanAcc.getAccountNumber();
                        Loanable loanable = (Loanable) loanAcc;
                        System.out.println("Loan limit: M" + loanable.getLoanLimit());
                        transAmount = readAmount(sc, "Loan amount: M");

                        loanable.requestLoan(transAmount);
                        System.out.println("Loan approved and added to balance.");
                        logTransaction(activeAccNum, transType, transAmount, loanAcc.getBalance(), "SUCCESS");
                        break;
                    case 7:
                        BankAccount detailsAcc = findAccount(accounts, sc, "Account number: ");
                        detailsAcc.displayDetails();
                        break;
                    case 8:
                        System.out.println("Thank you. Goodbye.");
                        running = false;
                        break;
                    default:
                        throw new InvalidTransactionException("Invalid menu choice. Enter 1 to 8.");
                }
            } catch (NumberFormatException e)
            {
                System.out.println("Error: Please enter a valid number.");
                logTransaction(activeAccNum, transType, 0, 0, "FAILED: Input Format Error");
            } catch (InsufficientFundsException e)
            {
                System.out.println("Error: " + e.getMessage());
                logTransaction(activeAccNum, transType, transAmount, 0, "FAILED: " + e.getMessage());
            } catch (InvalidTransactionException e)
            {
                System.out.println("Error: " + e.getMessage());
                logTransaction(activeAccNum, transType, transAmount, 0, "FAILED: " + e.getMessage());
            }
        }
        sc.close();
    }

    static double readAmount(Scanner sc, String prompt)
    {
        System.out.print(prompt);
        return Double.parseDouble(sc.nextLine().trim());
    }

    static BankAccount findAccount(BankAccount[] accounts, Scanner sc, String prompt)
            throws InvalidTransactionException
    {
        String number = readAccountNumber(sc, prompt);
        return findAccount(accounts, number);
    }

    private static String readAccountNumber(Scanner sc, String prompt)
    {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static BankAccount findAccount(BankAccount[] accounts, String number)
            throws InvalidTransactionException
    {
        for (int i = 0; i < accounts.length; i++)
        {
            String currentAccNum = accounts[i].getAccountNumber().toUpperCase();
            if (currentAccNum.equals(number.toUpperCase()))
            {
                return accounts[i];
            }
        }
        throw new InvalidTransactionException("Account not found.");
    }

    private static void logTransaction(String accNum, String type, double amount, double balance, String message)
    {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String logLine = String.format("%s | Account Number: %s | Transaction Type: %s"
                        + " | Amount: M%.2f | New Balance: M%.2f | Status: %s",
                timestamp, accNum, type, amount, balance, message);

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(TRANSACTION_FILE.toFile(), true)))
        {
            writer.write(logLine);
            writer.newLine();
            writer.flush();
        } catch (IOException e)
        {
            System.err.println("Transaction logging failed for "
                    + TRANSACTION_FILE.toAbsolutePath() + ": " + e.getMessage());
        }
    }
}
