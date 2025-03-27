package edu.uob;

import com.sun.jdi.event.ExceptionEvent;

public class ExtractCommand {
    String playerName;
    String rawCommand;

    public ExtractCommand(String rawCommand) {
        this.rawCommand = rawCommand;
        this.extractPlayerName();
    }

    private void extractPlayerName() {
        int colonIndex = rawCommand.indexOf(':');
        if(colonIndex != -1) {
            this.playerName = rawCommand.substring(0, colonIndex).trim();
        }else {
//            throw Exception;
        }
    }

    public String getPlayerName() {
        return playerName;
    }
}
