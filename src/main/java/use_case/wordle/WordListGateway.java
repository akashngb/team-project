package use_case.wordle;

import java.util.List;

public interface WordListGateway {
    boolean isValidWord(String word);
    String pickAnswer();
    List<String> getAllAnswers();
}
