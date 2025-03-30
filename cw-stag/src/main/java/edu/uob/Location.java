package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;


import java.util.*;

public class Location extends GameEntity{
    HashMap<String, Furniture> furniture;
    HashMap<String, Character> characters;
    HashMap<String, Artefact> artefacts;
    HashSet<String> players;
    HashSet<String> accessibleLocations;
    public Location(Node details, Graph cluster){
        super(details.getId().getId(), details.getAttribute("description"));
        this.furniture = new HashMap<>();
        this.characters = new HashMap<>();
        this.artefacts = new HashMap<>();
        this.players = new HashSet<>();
        this.accessibleLocations = new HashSet<>();
        addClusterEntities(cluster);
    }
    public GameEntity entityConsumed(String command)
    {
        if(this.artefacts.containsKey(command))
        {
            return this.artefacts.remove(command);
        }else if(this.furniture.containsKey(command))
        {
            return this.furniture.remove(command);
        }else if(this.characters.containsKey(command))
        {
            return this.characters.remove(command);
        }
        return null;
    }
    public List<String> getAvailableEntities() {
        List<String> availableEntities = new LinkedList<>();
        availableEntities.add(this.getName());
        availableEntities.addAll(this.artefacts.keySet());
        availableEntities.addAll(this.characters.keySet());
        availableEntities.addAll(this.furniture.keySet());
        return availableEntities;
    }
    public void addAccessibleLocation(String location){
        this.accessibleLocations.add(location);
    }
    public void removeAccessibleLocation(String location){
        this.accessibleLocations.remove(location);
    }
    public HashSet<String> getPlayers(){
        return players;
    }
    public HashSet<String> getAccessibleLocations(){
        return accessibleLocations;
    }
    public HashMap<String, Furniture> getFurniture(){
        return furniture;
    }
    public HashMap<String, Character> getCharacters(){
        return characters;
    }
    public HashMap<String, Artefact> getArtefacts(){
        return artefacts;
    }
    private void addClusterEntities(Graph cluster){
        List<Graph> clusterEntities = cluster.getSubgraphs();
        for(Graph clusterEntity : clusterEntities){
            String clusterEntityId = clusterEntity.getId().getId();
            List<Node> leafNodes = clusterEntity.getNodes(false);
            for(Node leafNode : leafNodes){
                String leafNodeName = leafNode.getId().getId();
                String leafNodeDescription = leafNode.getAttribute("description");
                if(clusterEntityId.equalsIgnoreCase("furniture"))
                {
                    this.furniture.put(leafNodeName, new Furniture(leafNodeName, leafNodeDescription));
                }else if(clusterEntityId.equalsIgnoreCase("characters"))
                {
                    this.characters.put(leafNodeName, new Character(leafNodeName, leafNodeDescription));
                }else if(clusterEntityId.equalsIgnoreCase("artefacts"))
                {
                    this.artefacts.put(leafNodeName, new Artefact(leafNodeName, leafNodeDescription));
                }
            }
        }
    }
    public String toString(String currPlayer) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are in ").append(this.getDescription()).append(".\nYou can see: ");

        // Handle furniture items with comma management
        int furnitureCount = 0;
        for (Map.Entry<String, Furniture> entry : this.furniture.entrySet()) {
            builder.append(entry.getValue().getDescription());
            furnitureCount++;
            if (furnitureCount < this.furniture.size()) {
                builder.append(", ");
            }
        }

        // Add comma between furniture and artefacts only if both exist
        if (!this.furniture.isEmpty() && !this.artefacts.isEmpty()) {
            builder.append(", ");
        }

        // Handle artefact items with comma management
        int artefactCount = 0;
        for (Map.Entry<String, Artefact> entry : this.artefacts.entrySet()) {
            builder.append(entry.getValue().getDescription());
            artefactCount++;
            if (artefactCount < this.artefacts.size()) {
                builder.append(", ");
            }
        }

        // Add comma between artefacts and characters only if both exist
        if ((!this.furniture.isEmpty() || !this.artefacts.isEmpty()) && !this.characters.isEmpty()) {
            builder.append(", ");
        }

        // Handle character items with comma management
        int characterCount = 0;
        for (Map.Entry<String, Character> entry : this.characters.entrySet()) {
            builder.append(entry.getValue().getDescription());
            characterCount++;
            if (characterCount < this.characters.size()) {
                builder.append(", ");
            }
        }

        builder.append("\nOther players at this location:\n");

        // Handle players listing
        for (String player : this.players) {
            if (!player.equals(currPlayer)) {
                builder.append(player).append("\n");
            }
        }

        builder.append("You can access from here: ");

        // Handle accessible locations with comma management
        int locationCount = 0;
        for (String loc : this.accessibleLocations) {
            builder.append(loc);
            locationCount++;
            if (locationCount < this.accessibleLocations.size()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }
}
