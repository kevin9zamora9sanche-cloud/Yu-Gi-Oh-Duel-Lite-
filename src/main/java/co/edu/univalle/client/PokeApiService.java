package co.edu.univalle.client;

import co.edu.univalle.model.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiService {

    public Pokemon obtenerPokemon(String nombreOrId) {
        if (nombreOrId == null || nombreOrId.trim().isEmpty()) {
            return null;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://pokeapi.co/api/v2/pokemon/" + nombreOrId.toLowerCase().trim()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());

                String name = json.getString("name");
                int hp = 0, attack = 0, defense = 0, speed = 0;

                JSONArray statsArray = json.getJSONArray("stats");
                for (int i = 0; i < statsArray.length(); i++) {
                    JSONObject statObj = statsArray.getJSONObject(i);
                    JSONObject statInfo = statObj.getJSONObject("stat");
                    String statName = statInfo.getString("name");
                    int baseStat = statObj.getInt("base_stat");

                    if (statName.equals("hp")) hp = baseStat;
                    else if (statName.equals("attack")) attack = baseStat;
                    else if (statName.equals("defense")) defense = baseStat;
                    else if (statName.equals("speed")) speed = baseStat;
                }

                String type = "normal";
                JSONArray typesArray = json.getJSONArray("types");
                if (typesArray.length() > 0) {
                    JSONObject typeObj = typesArray.getJSONObject(0);
                    type = typeObj.getJSONObject("type").getString("name");
                }

                JSONObject imageJson = json.getJSONObject("sprites");
                String spriteUrl = imageJson.optString("front_default", "");

                return new Pokemon(name, hp, attack, defense, speed, type, spriteUrl);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}