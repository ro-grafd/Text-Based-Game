package edu.uob;

import com.sun.jdi.event.ExceptionEvent;

import java.util.*;

// Class for getting all the information of the command
// like all the tokens, raw command, processed command and stuff
public class ExtractCommand {
    private String playerName;
    private String rawCommand;
    private String triggerWord;
    private String cookedCommand;
    private List<String> tokenisedCommand;
    private HashSet<String> triggerWordSet;
    private String artefact;
    private String toReach;

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
        for (String token : tokenisedCommand) {
            if (isExtra(token, entireArtefacts)) {
                entities.add(token);
            }
        }
        if (entities.size() > 1) {
            if (entityName.equals("location")) {
                throw new GameException.NavigationError();
            } else {
                throw new GameException.InventoryManagementError();
            }
        }
        if (entities.isEmpty()) {
            if (entityName.equals("location")) {
                throw new GameException.NavigationError();
            } else {
                throw new GameException.InventoryManagementError();
            }
        }
        if (entityName.equals("location")) {
            this.toReach =  entities.iterator().next();
        } else {
            this.artefact = entities.iterator().next();
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
        boolean changed = true;
        while (changed) {
            changed = false;
            Iterator<String> iterator = words.iterator();
            if (!iterator.hasNext()) continue;
            String current = iterator.next();
            while (iterator.hasNext()) {
                String next = iterator.next();
                StringBuilder combinedToken = new StringBuilder(current);
                combinedToken.append(" ").append(next);
                if (triggerWords.contains(combinedToken.toString())) {
                    iterator.remove(); // Removes 'next'
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
        if (colonIndex != -1)  this.playerName = this.rawCommand.substring(0, colonIndex).toLowerCase();
        if(this.playerName.matches(".*[^a-zA-Z\\s-'].*")) {
            throw new GameException.InvalidName();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    private boolean isBuiltInCommand(String command) {
        switch (command) {
            case "inv", "inventory", "get", "drop", "look", "goto", "health":
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
