package edu.uob;

import java.util.HashMap;
import java.util.Map;

// All the relevant information about a player like its name, its current location, its inventory at any
// given instant and obviously health coz if HP fear is not in the game them what's the thrill of even playing??
public class Player {
    private final String name;
    private String presentLocation;
    private final HashMap<String,Artefact> personalInventory;
    private int health;

    public Player(String name, String presentLocation) {
        this.name = name;
        this.presentLocation = presentLocation;
        this.personalInventory = new HashMap<String, Artefact>();
        this.health = 3;
    }

    public void killPlayer(String spawnLocation, Location killedLocation)
    {
        for (Map.Entry<String, Artefact> entry : this.personalInventory.entrySet()) {
            String key = entry.getKey();
            Artefact artefact = entry.getValue();
            killedLocation.getArtefacts().put(key, artefact);
        }
        this.personalInventory.clear();
        killedLocation.getPlayers().remove(this.getName());
        this.presentLocation =  spawnLocation;
        this.health = 3;
    }

    public int getHealth() {
        return health;
    }

    public void changeHealth(boolean harmed) {
        if(harmed){
            this.health--;
        }else if(this.health < 3){
            this.health++;
        }
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
