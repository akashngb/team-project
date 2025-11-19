package data_access;

import entity.ChessPuzzle;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.chess_puzzle.ChessPuzzleDataAccessInterface;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ChessPuzzleDataAccessInterface using RapidAPI.
 */
public class RapidAPIChessPuzzleDataAccess implements ChessPuzzleDataAccessInterface {

    private static final String API_KEY = "e0afce1850mshfd7d6074e38637ep159346jsnc6b463f2ed09";
    private static final String API_HOST = "chess-puzzles.p.rapidapi.com";
    private static final String BASE_URL = "https://chess-puzzles.p.rapidapi.com/";

    @Override
    public List<ChessPuzzle> fetchPuzzles(int count, int rating) {
        List<ChessPuzzle> puzzles = new ArrayList<>();

        try {
            String themes = URLEncoder.encode("[\"middlegame\",\"advantage\"]", "UTF-8");
            String urlString = BASE_URL + "?themes=" + themes +
                    "&rating=" + rating +
                    "&themesType=ALL&playerMoves=4&count=" + count;

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-rapidapi-host", API_HOST);
            conn.setRequestProperty("x-rapidapi-key", API_KEY);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray puzzlesArray = jsonResponse.getJSONArray("puzzles");

                for (int i = 0; i < puzzlesArray.length(); i++) {
                    JSONObject puzzleObj = puzzlesArray.getJSONObject(i);

                    String puzzleId = puzzleObj.getString("puzzleid");
                    String fen = puzzleObj.getString("fen");
                    int puzzleRating = puzzleObj.getInt("rating");

                    JSONArray movesArray = puzzleObj.getJSONArray("moves");
                    List<String> moves = new ArrayList<>();
                    for (int j = 0; j < movesArray.length(); j++) {
                        moves.add(movesArray.getString(j));
                    }

                    JSONArray themesArray = puzzleObj.getJSONArray("themes");
                    List<String> themesList = new ArrayList<>();
                    for (int j = 0; j < themesArray.length(); j++) {
                        themesList.add(themesArray.getString(j));
                    }

                    puzzles.add(new ChessPuzzle(puzzleId, fen, moves, puzzleRating, themesList));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch puzzles: " + e.getMessage(), e);
        }

        return puzzles;
    }
}