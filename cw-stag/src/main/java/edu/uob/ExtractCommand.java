package edu.uob;

import com.sun.jdi.event.ExceptionEvent;

import java.util.*;

public class ExtractCommand {
    String playerName;
    String rawCommand;
    String triggerWord;
    String cookedCommand;
    List<String> tokenisedCommand;
    public ExtractCommand(String rawCommand, Set<String> triggerWords) throws GameException.InvalidName {
        this.rawCommand = rawCommand;
        this.extractPlayerName();
        this.removePlayerName();
        this.tokenise(triggerWords);
    }
    private void tokenise(Set<String> triggerWords) {
        tokenisedCommand = new Vector<>(); // Use Vector instead of ArrayList
        String command = this.cookedCommand.trim();
        List<String> words = new Vector<>(); // Temporary list for processing
        StringBuilder word = new StringBuilder();

        // Manually split the string into words
        for (char c : command.toCharArray()) {
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                if (word.length() > 0) {
                    words.add(word.toString());
                    word.setLength(0);
                }
            } else {
                word.append(c);
            }
        }
        if (word.length() > 0) {
            words.add(word.toString());
        }

        // Merging adjacent words based on triggers
        for (int i = 0; i < words.size() - 1; i++) {
            String token = words.get(i);
            String nextToken = words.get(i + 1);

            if (triggerWords.contains(token + " " + nextToken)) {
                words.set(i, token + " " + nextToken);
                words.remove(i + 1);
                i--; // Adjust index after merging
            }
        }

        // Set tokenisedCommand to the processed words
        tokenisedCommand.addAll(words);
    }


    private void removePlayerName()
    {
        this.cookedCommand = this.rawCommand.replaceFirst(this.playerName, "").toLowerCase();
    }
    private void extractPlayerName() throws GameException.InvalidName {
        this.playerName = this.rawCommand.split(":")[0];
        if(this.playerName.matches(".*[^a-zA-Z\\s-'].*")) {
            throw new GameException.InvalidName();
        }
    }

    public String getPlayerName() {
        return playerName;
    }
}
