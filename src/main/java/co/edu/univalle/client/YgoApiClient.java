package co.edu.univalle.client;

import co.edu.univalle.model.Card;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Cliente HTTP para consumir la API de YGOProDeck.
 */
public class YgoApiClient {

    private static final String RANDOM_CARD_URL = "https://db.ygoprodeck.com/api/v7/randomcard.php";
    private final HttpClient client;

    public YgoApiClient() {
        this.client = HttpClient.newBuilder().build();
    }

    /**
     * Obtiene una carta Monster aleatoria de la API.
     * Reintenta si la carta no es de tipo Monster.
     */
    public Card obtenerCartaMonsterAleatoria() {
        int intentos = 0;
        while (intentos < 10) { // Límite de seguridad para reintentos
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(RANDOM_CARD_URL))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject json = new JSONObject(response.body());

                    String type = json.optString("type", "");
                    // Validar requisito: Debe ser tipo Monster
                    if (type.toLowerCase().contains("monster")) {
                        String name = json.getString("name");
                        int atk = json.optInt("atk", 0);
                        int def = json.optInt("def", 0);

                        // Obtener la URL de la primera imagen disponible
                        JSONArray cardImages = json.getJSONArray("card_images");
                        String imageUrl = cardImages.getJSONObject(0).getString("image_url");

                        return new Card(name, atk, def, imageUrl, type);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al obtener la carta de la API: " + e.getMessage());
            }
            intentos++;
        }
        return null;
    }
}