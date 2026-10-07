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
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
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

                    JSONArray data = json.getJSONArray("data");

                    for(int i = 0; i < data.length(); i++)
                    {
                        JSONObject cardJson = data.getJSONObject(i);
                        String type = cardJson.optString("type", "");
                        // Validar requisito: Debe ser tipo Monster
                        if (type.toLowerCase().contains("monster")) {
                            String name = cardJson.getString("name");
                            int atk = cardJson.optInt("atk", 0);
                            int def = cardJson.optInt("def", 0);

                            // Obtener la URL de la primera imagen disponible
                            JSONArray cardImages = cardJson.getJSONArray("card_images");
                            String imageUrl = cardImages.getJSONObject(0).getString("image_url");

                            return new Card(name, atk, def, imageUrl, type);
                        }
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