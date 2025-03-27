package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;


import java.util.HashMap;
import java.util.HashSet;

public class Location extends GameEntity{
    HashMap<String, Furniture> furniture;
    HashMap<String, Character> characters;
    HashMap<String, Artefact> artefacts;
    HashSet<String> players;
    HashSet<String> accessibleLocations;
    public Location(Node details, Graph location){
        super(details.getId().getId(), details.getAttribute("description"));
        this.furniture = new HashMap<>();
        this.characters = new HashMap<>();
        this.artefacts = new HashMap<>();
        this.players = new HashSet<>();
        this.accessibleLocations = new HashSet<>();
        addClusterEntities(location);
    }
    public void addAccessibleLocation(String location){
        this.accessibleLocations.add(location);
    }

    public HashSet<String> getPlayers(){
        return players;
    }
    private void addClusterEntities(Graph location){
        
    }
}
