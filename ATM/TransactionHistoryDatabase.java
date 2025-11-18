import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TransactionHistoryDatabase {

	//URL String for database
    static final String SUPABASE_URL =
        "https://udvrwcrhhqtqfxcikyvw.supabase.co/rest/v1/transactionHistory";

    //API key for database
    static final String API_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVkdnJ3Y3JoaHF0cWZ4Y2lreXZ3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE1Nzc1NTUsImV4cCI6MjA3NzE1MzU1NX0.7P9QTQ09IQVESQ7jdjuQ5hwaQwlylhdPXYWdwTZpduU";

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void saveTransaction(String accountNumber,
                                       String transactionType,
                                       double amount,
                                       double balanceAfter) {

        //JSON format for the table
        String json = String.format(
            "{\"customerNumber\": %s, " +
            "\"transactionType\": \"%s\", " +
            "\"amount\": %f, " +
            "\"balance\": %f}",
            accountNumber, transactionType, amount, balanceAfter
        );

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(SUPABASE_URL))
            .header("apikey", API_KEY)
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation") 
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        try {
            HttpResponse<String> resp =
                client.send(request, HttpResponse.BodyHandlers.ofString());

           

        } catch (Exception e) {
            System.out.println("Error sending transaction to Supabase: " + e.getMessage());
        }
    }
}
