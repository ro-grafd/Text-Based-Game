package edu.uob;

import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
                }else if(clusterEntityId.equalsIgnoreCase("character"))
                {
                    this.characters.put(leafNodeName, new Character(leafNodeName, leafNodeDescription));
                }else if(clusterEntityId.equalsIgnoreCase("artefact"))
                {
                    this.artefacts.put(leafNodeName, new Artefact(leafNodeName, leafNodeDescription));
                }
            }
        }
    }
}
