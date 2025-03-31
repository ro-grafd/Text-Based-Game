package edu.uob;

import com.sun.jdi.event.ExceptionEvent;

import java.util.*;

public class ExtractCommand {
    String playerName;
    String rawCommand;
    String triggerWord;
    String cookedCommand;
    List<String> tokenisedCommand;
    HashSet<String> triggerWordSet;
    String artefact;
    String toReach;
    public ExtractCommand(String rawCommand, Set<String> triggerWords) throws GameException.InvalidName {
        this.rawCommand = rawCommand;
        this.extractPlayerName();
        this.removePlayerName();
        this.tokenise(triggerWords);
    }
    public void checkForLocation(Set<String> allLocations, Set<String> accessibleLocations) throws GameException{
        this.getEntity(allLocations, "location");
        if(!accessibleLocations.contains(this.toReach)){
            throw new GameException.NavigationError();
        }
    }
    public void checkForArtefacts(Set<String> entireArtefacts, Set<String> accessibleArtefacts) throws GameException{
        this.getEntity(entireArtefacts, "artefact");
        boolean itemAvailable = false;
        for (String item : accessibleArtefacts) {
            if (item.equals(this.artefact)) {
                itemAvailable = true;
                break;
            }
        }

        if (!itemAvailable) {
            if (this.triggerWord.equals("get")) {
                throw new GameException.InventoryManagementError();
            } else if (this.triggerWord.equals("drop")) {
                throw new GameException.InventoryManagementError();
            }
        }
    }
    private void getEntity(Set<String> entireArtefacts, String entityName) throws GameException {
        HashSet<String> entities = new HashSet<>();

        // Replace forEach with traditional for loop
        for (String token : tokenisedCommand) {
            if (isExtra(token, entireArtefacts)) {
                entities.add(token);
            }
        }

        // Multiple entities check
        if (entities.size() > 1) {
            if (entityName.equals("location")) {
                throw new GameException.NavigationError();
            } else {
                throw new GameException.InventoryManagementError();
            }
        }

        // Empty entities check
        if (entities.isEmpty()) {
            if (entityName.equals("location")) {
                throw new GameException.NavigationError();
            } else {
                throw new GameException.InventoryManagementError();
            }
        }

        // Set destination or artefact
        if (entityName.equals("location")) {
            this.toReach = (String) entities.iterator().next();
        } else {
            this.artefact = (String) entities.iterator().next();
        }
    }
    public void setTriggerWord(Set<String> givenTriggerWords) throws GameException.TriggerException {
        // search in tokenisedCommand the triggerWord and it should be basic or in the given actions.xml file ???
        // List<String> tokenisedCommand
        int builtInTriggerWord = 0;
        int extractedTriggerWord = 0;
        this.triggerWordSet = new HashSet<String>();
        for(String token : tokenisedCommand) {
            if(isBuiltInCommand(token)) {
                triggerWordSet.add(token);
            }
            if(isExtra(token, givenTriggerWords)) {
                triggerWordSet.add(token);
            }
        }
        if(triggerWordSet.size() > 1) {
            throw new GameException.TriggerException();
        }
        if(triggerWordSet.isEmpty()) {
            throw new GameException.TriggerException();
        }
        this.triggerWord = triggerWordSet.iterator().next();
    }
    public String getTriggerWord()
    {
        return triggerWord;
    }
    private void tokenise(Set<String> triggerWords) {
        // Initialize tokenisedCommand as a LinkedList if it's not already initialized
        tokenisedCommand = new LinkedList<>();
        String command = this.cookedCommand.trim();
        List<String> words = new LinkedList<>(); // Temporary list for processing
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
        boolean changed = true;
        while (changed) {
            changed = false;
            Iterator<String> iterator = words.iterator();
            if (!iterator.hasNext()) continue;

            String current = iterator.next();
            while (iterator.hasNext()) {
                String next = iterator.next();

                // Use StringBuilder instead of string concatenation
                StringBuilder combinedToken = new StringBuilder(current);
                combinedToken.append(" ").append(next);

                if (triggerWords.contains(combinedToken.toString())) {
                    // Remove both tokens
                    iterator.remove(); // Removes 'next'

                    // Need to rebuild the list without 'current' and with the new combined token
                    List<String> newWords = new LinkedList<>();
                    Iterator<String> rebuildIterator = words.iterator();
                    boolean addedCombined = false;

                    while (rebuildIterator.hasNext()) {
                        String token = rebuildIterator.next();
                        if (token == current && !addedCombined) {
                            newWords.add(combinedToken.toString());
                            addedCombined = true;
                            rebuildIterator.remove();
                        }
                    }

                    // Add any remaining tokens
                    for (String token : words) {
                        newWords.add(token);
                    }

                    words = newWords;
                    changed = true;
                    break;
                }

                current = next;
            }
        }

        // Set tokenisedCommand to the processed words
        for (String token : words) {
            tokenisedCommand.add(token);
        }
    }


    private void removePlayerName()
    {
        this.cookedCommand = this.rawCommand.replaceFirst(this.playerName, "").toLowerCase();
        this.cookedCommand = this.cookedCommand.replaceAll("[^a-z\\s]", " ");
    }
    private void extractPlayerName() throws GameException.InvalidName {
        int colonIndex = this.rawCommand.indexOf(':');
        if (colonIndex != -1)  this.playerName = this.rawCommand.substring(0, colonIndex);
        if(this.playerName.matches(".*[^a-zA-Z\\s-'].*")) {
            throw new GameException.InvalidName();
        }
    }

    public String getPlayerName() {
        return playerName;
    }
    private boolean isBuiltInCommand(String command) {
        switch (command) {
            case "inv", "inventory", "get", "drop", "look", "goto":
            {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    private boolean isExtra(String command, Set<String> triggerWords) {
        return triggerWords.contains(command);
    }
    public String getArtefact() {
        return artefact;
    }
    public String getToReach() {
        return toReach;
    }
    public HashSet<String> getTriggerWordSet() {
        return triggerWordSet;
    }
    public List<String> getTokenisedCommand() {
        return tokenisedCommand;
    }
}
