package data_access.wordle;

import use_case.wordle.WordListGateway;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Loads words from resources/wordlists/valid_words.txt and answer_list.txt
 */
public class FileWordListDataAccess implements WordListGateway {
    private final Set<String> validWords = new HashSet<>();
    private final List<String> answers = new ArrayList<>();
    private final Random rng = new Random();

    public FileWordListDataAccess() {
        loadResource("/wordlists/valid_words.txt", validWords, false);
        loadResource("/wordlists/answer_list.txt", answers, true);
        if (answers.isEmpty()) throw new IllegalStateException("No answers loaded (resources/wordlists/answer_list.txt)");
    }

    private void loadResource(String path, Collection<String> out, boolean preserveOrder) {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) return;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String w = line.strip().toLowerCase();
                    if (w.isEmpty()) continue;
                    if (w.length() != 5) continue;
                    out.add(w);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load resource " + path, e);
        }
    }

    @Override
    public boolean isValidWord(String word) {
        if (word == null) return false;
        return validWords.contains(word.toLowerCase());
    }

    @Override
    public String pickAnswer() {
        return answers.get(rng.nextInt(answers.size()));
    }

    @Override
    public List<String> getAllAnswers() {
        return Collections.unmodifiableList(answers);
    }
}
