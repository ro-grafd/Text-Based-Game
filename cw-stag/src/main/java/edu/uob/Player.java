package edu.uob;

import java.util.HashMap;

public class Player {
    String name;
    String presentLocation;
    HashMap<String,Artefact> personalInventory;

    public Player(String name, String presentLocation) {
        this.name = name;
        this.presentLocation = presentLocation;
        this.personalInventory = new HashMap<String, Artefact>();
    }

    public String getName() {
        return name;
    }

    public String getPresentLocation() {
        return presentLocation;
    }

    public void setPresentLocation(String presentLocation) {
        this.presentLocation = presentLocation;
    }

    public HashMap<String,Artefact> getPersonalInventory() {
        return personalInventory;
    }

    void addPersonalArtefact(Artefact artefact) {
        personalInventory.put(artefact.getName(), artefact);
    }

    
}
