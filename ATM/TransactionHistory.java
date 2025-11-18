import java.util.*;
import java.net.http.*;
import java.net.URI;

//The overall class for the transaction history feature
public class TransactionHistory {

    //Adds a new transaction record for the specific account
    public static void recordTransaction(String accountNumber, String transactionType, double amount, double balance) {
        try {
            //Sends the information to Supabase database
            TransactionHistoryDatabase.saveTransaction(
                    accountNumber,
                    transactionType,
                    amount,
                    balance
            );
        //Catches an error if it occurs while saving and displays message
        } catch (Exception e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    //Retrieves the last n transactions for a given account
    public static List<String> getLastTransactions(String accountNumber, int n) {
        //Creates a list to store all transactions belonging to this account
        List<String> allTransactions = new ArrayList<>();
        try {
            //Fetching the information from Supabase database
            String url = TransactionHistoryDatabase.SUPABASE_URL +
                "?customerNumber=eq." + accountNumber +
                "&order=created_at.desc" +
                "&limit=" + n; 

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("apikey", TransactionHistoryDatabase.API_KEY)
                    .header("Authorization", "Bearer " + TransactionHistoryDatabase.API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            //Converts json response to string for simple display
            String json = response.body();

            if (json.equals("[]")) {
                return allTransactions;
            }

            //Creates the display
            
            //Removes the brackets
            json = json.substring(1, json.length() - 1).trim();

            //Splits by "},{" to handle multiple transactions
            String[] entries = json.split("\\},\\s*\\{");


            for (String entry : entries) {

                //Cleans up leftover braces
                entry = entry.replace("{", "").replace("}", "");

                //Extracts fields from the string
                String type = extractValue(entry, "transactionType");
                String amountStr = extractValue(entry, "amount");
                String balanceStr = extractValue(entry, "balance");
                String date = extractValue(entry, "created_at").replace("T", " ").replace("Z", "");

                //Converts strings to numeric types
                double amount = Double.parseDouble(amountStr);
                double balance = Double.parseDouble(balanceStr);

                //Final readable format
                String formatted = String.format(
                    "%s | Amount: $%.2f | Balance: $%.2f | Date: %s", type, amount, balance, date);

                allTransactions.add(formatted);
            }

        //Catches any HTTP or other errors
        } catch (Exception e) {
            System.out.println("Error reading transaction history: " + e.getMessage());
        }

        return allTransactions;
    }

    //Helper function to extract values from JSON-like string
    private static String extractValue(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\":");
            if (start == -1) return "";

            //Jumps past "key"
            start += key.length() + 3; 

            //Determines if field is quoted (string) or a number
            if (json.charAt(start) == '"') {
                int end = json.indexOf("\"", start + 1);
                return json.substring(start + 1, end);
            } else {
                int endComma = json.indexOf(",", start);
                int endBrace = json.length();
                int end = (endComma == -1 ? endBrace : endComma);
                return json.substring(start, end).trim();
            }
        } catch (Exception e) {
            return "";
        }
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
            System.out.println("------------------------------------------\n");
        }
    }
}
