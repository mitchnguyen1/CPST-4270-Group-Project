import java.io.*;
import java.util.*;

//The overall class for the transaction history feature
public class TransactionHistory {
    //File where are transaction records are stored
    private static final String HISTORY_FILE = "transaction_history.txt";

    //Adds a new transaction record for the specific account
    public static void recordTransaction(String accountNumber, String transactionType, double amount, double balance) {
        //Opens the file in append mode so previous data is not lost
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true); 
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {

            //Creates a formatted record line with all of the necessary deatils    
            String record = String.format("%s | %s | $%.2f | Balance: $%.2f | Date: %s",
                accountNumber, transactionType, amount, balance, new Date().toString());
            //Writes the record as a new line in file
            out.println(record);
        //Catches an error if it occurs while saving and displays messgae
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    //Retrieves the last n transactions for a given account
    public static List<String> getLastTransactions(String accountNumber, int n) {
        //Creates a list to store all transactions belonging to this account
        List<String> allTransactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            //Reads each line and check if it belongs with the correct account number
            while ((line = br.readLine()) != null) {
                if (line.startsWith(accountNumber + " |")) {
                    allTransactions.add(line);
                }
            }
        //Catch for if there are no transactions
        } catch (FileNotFoundException e) {
            System.out.println("No transaction history found yet.");
        //Catches I/O errors with the log
        } catch (IOException e) {
            System.out.println("Error reading transaction history: " + e.getMessage());
        }
        //Calculates where to start so we only get last 'n' entries
        int start = Math.max(0, allTransactions.size() - n);
        //Returns only the most recent transactions
        return allTransactions.subList(start, allTransactions.size());
    }

    //Displays last n transactions
    public static void displayLastTransactions(String accountNumber, int n) {
        //Retrieves the last n records
        List<String> lastTransactions = getLastTransactions(accountNumber, n);
        //If no records exist tell the user 
        if (lastTransactions.isEmpty()) {
            System.out.println("No recent transactions found for this account.");
        } else {
            //Displays each record nicely
            System.out.println("\n--- Last " + n + " Transactions ---");
            for (String record : lastTransactions) {
                System.out.println(record);
            }
            System.out.println("-----------------------------\n");
        }
    }
}
