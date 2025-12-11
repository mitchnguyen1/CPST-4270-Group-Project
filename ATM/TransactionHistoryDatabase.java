import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TransactionHistoryDatabase {

	//URL String for database
    static final String SUPABASE_URL = Config.get("db.apiURL");

    //API key for database
    static final String API_KEY = Config.get("db.api");
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
