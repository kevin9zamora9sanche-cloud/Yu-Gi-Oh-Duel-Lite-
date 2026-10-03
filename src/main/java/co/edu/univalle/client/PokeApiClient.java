package co.edu.univalle.client;

import co.edu.univalle.model.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiClient {

    private final HttpClient client;

    public PokeApiClient() {
        // Se crea el cliente HTTP reutilizable para realizar las peticiones
        this.client = HttpClient.newHttpClient();
    }

    public Pokemon consultarPokemon(String nombreOId) throws Exception {
        String parametro = nombreOId.trim().toLowerCase();

        // Se crea el objeto HttpRequest para realizar la petición a PokeAPI
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://pokeapi.co/api/v2/pokemon/" + parametro))
                .build();

        // Ejecutamos la solicitud
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Si el código de respuesta es 200, la petición fue exitosa
        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(response.body());

            String nombre = json.getString("name");

            // Extraer estadísticas iterando sobre el JSONArray 'stats' (igual que en la clase)
            int hp = 0;
            int attack = 0;
            int defense = 0;
            int speed = 0;

            JSONArray statsArray = json.getJSONArray("stats");
            for (int i = 0; i < statsArray.length(); i++) {
                JSONObject statObj = statsArray.getJSONObject(i);
                JSONObject nameJson = statObj.getJSONObject("stat");
                String statName = nameJson.getString("name");
                int baseStat = statObj.getInt("base_stat");

                switch (statName) {
                    case "hp":
                        hp = baseStat;
                        break;
                    case "attack":
                        attack = baseStat;
                        break;
                    case "defense":
                        defense = baseStat;
                        break;
                    case "speed":
                        speed = baseStat;
                        break;
                }
            }

            // Extraer el primer tipo del Pokémon (JSONArray 'types')
            JSONArray typesArray = json.getJSONArray("types");
            String tipoPrimario = "normal";
            if (typesArray.length() > 0) {
                JSONObject typeObj = typesArray.getJSONObject(0).getJSONObject("type");
                tipoPrimario = typeObj.getString("name");
            }

            // Extraer la URL de la imagen frontal (sprites -> front_default)
            JSONObject imageJson = json.getJSONObject("sprites");
            String spriteUrl = imageJson.optString("front_default", "");

            return new Pokemon(nombre, hp, attack, defense, speed, tipoPrimario, spriteUrl);
        } else if (response.statusCode() == 404) {
            throw new IllegalArgumentException("El Pokémon '" + nombreOId + "' no existe.");
        } else {
            throw new RuntimeException("Error en el servidor de PokeAPI (Código: " + response.statusCode() + ")");
        }
    }
}