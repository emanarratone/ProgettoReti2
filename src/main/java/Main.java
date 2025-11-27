import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        // Valori di input: devono essere scritti come li accetta il sito
        String partenza = "MILANO SUD";      // es: casello in MAIUSCOLO
        String destinazione = "ASTI OVEST";

        String query = String.format(
                "partenza=%s&destinazione=%s",
                URLEncoder.encode(partenza, StandardCharsets.UTF_8),
                URLEncoder.encode(destinazione, StandardCharsets.UTF_8)
        );

        String url = "https://www.infoviaggiando.it/pedaggi/calcolopedaggio?" + query;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("X-Requested-With", "XMLHttpRequest") // imita la chiamata AJAX
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body:   " + response.body());
    }
}
