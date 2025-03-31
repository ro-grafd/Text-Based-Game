package edu.uob;

import com.alexmerz.graphviz.*;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.*;

public final class GameServer {

    private static final char END_OF_TRANSMISSION = 4;
    File entitiesFile, actionsFile;
    HashMap<String, Location> locations;
    HashMap<String, Player> players;
    HashMap<String, HashSet<Action>> actions;
    String spawnLocation;
    String currPlayer;
    String result;
    ExtractCommand extractCommand;
    ActionHandler actionHandler;
    Action actionToDo;
    List<String> entities;

    public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, SAXException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Instanciates a new server instance, specifying a game with some configuration files
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    */
    public GameServer(File entitiesFile, File actionsFile)  {
        // TODO implement your server logic here
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.instantiateMemory();
        this.readFiles();
    }
    public void readFiles()  {
        try {
            this.readEntitiesFile();
        } catch (FileNotFoundException | ParseException e) {
            throw new RuntimeException(e);
        }
        try {
            this.readActionsFile();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
    public void instantiateMemory() {
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
        this.actions = new HashMap<>();
        this.result = "";
    }
    public void readEntitiesFile() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader fileReader = new FileReader(this.entitiesFile);
        parser.parse(fileReader);
        Graph rootGraph = parser.getGraphs().get(0);
        List<Graph> subGraphs = rootGraph.getSubgraphs();
        List<Graph> locationClusters = subGraphs.get(0).getSubgraphs();
        List<Edge> connections = subGraphs.get(1).getEdges();
        this.spawnLocation = locationClusters.get(0).getNodes(false).get(0).getId().getId().toLowerCase();
        for(Graph cluster: locationClusters){
            Node node = cluster.getNodes(false).get(0);
            this.locations.put(node.getId().getId().toLowerCase(), new Location(node, cluster));
        }
        for(Edge connection: connections){
            Node source = connection.getSource().getNode();
            String sourceName = source.getId().getId().toLowerCase();
            Node destination = connection.getTarget().getNode();
            String destinationName = destination.getId().getId().toLowerCase();
            this.locations.get(sourceName).addAccessibleLocation(destinationName);
        }
    }

    public void readActionsFile() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(this.actionsFile);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();
        for(int i = 1; i < actions.getLength(); i+=2) {
            Action currentAction = new Action((Element)actions.item(i));
            //map action to trigger phrases
            for(String keyPhrase: currentAction.getTriggers()) {
                if(!this.actions.containsKey(keyPhrase)) {
                    this.actions.put(keyPhrase, new HashSet<>());
                }
                this.actions.get(keyPhrase).add(currentAction);
            }
        }
    }
    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * This method handles all incoming game commands and carries out the corresponding actions.</p>
    *
    * @param command The incoming command to be processed
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        try{
            this.extractCommand = new ExtractCommand(command, this.actions.keySet());
            this.currPlayer = extractCommand.getPlayerName();
            if(!this.players.containsKey(currPlayer)) {
                this.players.put(currPlayer, new Player(currPlayer, this.spawnLocation));
                this.locations.get(spawnLocation).getPlayers().add(currPlayer); // not adding player to clusterEntities as player is not entity!
            }
            this.extractCommand.setTriggerWord(this.actions.keySet());
            this.performAction();
        }catch(GameException e){
            return e.getLocalizedMessage();
        }
        return this.result;
    }
//    ./mvnw exec:java@client -Dexec.args="rohit"

    public void performAction()  throws GameException {
        String triggerWord = this.extractCommand.getTriggerWord();
        switch(triggerWord) {
            case "inv":
            case "inventory":
                this.handleInventory();
                break;
            case "get":
                this.handleGet();
                break;
            case "look":
                this.handleLook();
                break;
            case "goto":
                this.handleGoto();
                break;
            case "drop":
                this.handleDrop();
                break;
            default:
                HashSet<Action> permissableActions = new HashSet<>();
                for(String trigger : extractCommand.getTriggerWordSet())
                {
                    permissableActions.addAll(actions.get(trigger));
                }
                this.actionHandler = new ActionHandler(permissableActions, this.extractCommand.getTokenisedCommand(),triggerWord);
                String currPlayerLocation = this.players.get(currPlayer).getPresentLocation();
                this.actionToDo = actionHandler.findToDoAction(this.locations.get(currPlayerLocation), this.players.get(currPlayer).getPersonalInventory());
                this.executeAction();
        }
    }
    public void executeAction() {
        HashSet<String> consumed = this.actionToDo.getEntitiesUsed();
        HashSet<String> produced = this.actionToDo.getEntitiesMade();
        this.handleConsumedProduced(true, consumed);
        this.handleConsumedProduced(false,produced);
        this.result = this.actionToDo.getActionStatement();
    }
    public void handleConsumedProduced(boolean hasConsumed, HashSet<String> consumed) {
        String locationForConsumed;
        if(hasConsumed){
            locationForConsumed = "storeroom";
        }else{
            locationForConsumed = this.players.get(currPlayer).getPresentLocation();
        }
        for(String consumedWord: consumed){
            if(this.players.get(currPlayer).getPersonalInventory().containsKey(consumedWord) && hasConsumed){
                Artefact toRemove = this.players.get(currPlayer).getPersonalInventory().remove(consumedWord);
            }else{
                this.handleEntityState(consumedWord, hasConsumed, locationForConsumed);
            }
        }
    }
    public void handleEntityState(String entityName, boolean hasConsumed, String locationForConsumed) {
        GameEntity consumedEntity = null;
        for (Map.Entry<String, Location> entry : this.locations.entrySet()) {
            String key = entry.getKey();
            Location value = entry.getValue();
            List<String> locationEntities = value.getAvailableEntities();
            locationEntities.remove(key);
            if (locationEntities.contains(entityName)) {
                consumedEntity = value.entityConsumed(entityName);
            }
        }
        String currLocation = this.players.get(currPlayer).getPresentLocation();
        if(this.locations.containsKey(entityName) && !entityName.equalsIgnoreCase(currLocation)) {
            if(!hasConsumed) {
                this.locations.get(currLocation).addAccessibleLocation(entityName);
            } else {
                this.locations.get(currLocation).removeAccessibleLocation(entityName);
            }
        } else {
            this.addEntityToLocation(locationForConsumed, consumedEntity);
        }
    }
    public void addEntityToLocation(String locationForConsumed, GameEntity consumedEntity) {
        Location location = this.locations.get(locationForConsumed);
        if(consumedEntity != null) {
            {
                if(consumedEntity instanceof Artefact) {
                    location.getArtefacts().put(consumedEntity.getName(), (Artefact)consumedEntity);
                }else if(consumedEntity instanceof Character)
                {
                    location.getCharacters().put(consumedEntity.getName(), (Character)consumedEntity);
                }else if(consumedEntity instanceof Furniture)
                {
                    location.getFurniture().put(consumedEntity.getName(), (Furniture)consumedEntity);
                }
            }
        }
    }
    public void handleDrop() throws GameException {
        Set<String> entireArtefacts = this.getEntireGameArtefacts();
        Set<String> accessibleArtefactsInventory = this.players.get(currPlayer).getPersonalInventory().keySet();
        extractCommand.checkForArtefacts(entireArtefacts, accessibleArtefactsInventory);
        Artefact artefactToDrop = this.players.get(currPlayer).getPersonalInventory().get(extractCommand.getArtefact());
        this.players.get(currPlayer).getPersonalInventory().remove(extractCommand.getArtefact());
        String currPlayerLocation = this.players.get(currPlayer).getPresentLocation();
        this.locations.get(currPlayerLocation).getArtefacts().put(artefactToDrop.getName(), artefactToDrop);
        StringBuilder builder = new StringBuilder();
        builder.append("You dropped a ");
        builder.append(artefactToDrop.getName());
        this.result = builder.toString();
    }
    public void handleGoto() throws GameException {
        String currPlayerLocation = this.players.get(currPlayer).getPresentLocation();
        this.extractCommand.checkForLocation(this.locations.keySet(), this.locations.get(currPlayerLocation).getAccessibleLocations());
        this.locations.get(currPlayerLocation).getPlayers().remove(currPlayer);
        String toReach = extractCommand.getToReach();
        this.players.get(currPlayer).setPresentLocation(toReach);
        this.locations.get(toReach).getPlayers().add(currPlayer);
        this.result = this.locations.get(toReach).toString(currPlayer);
    }
    public void handleLook() throws GameException {
        String currPlayerLocation = this.players.get(currPlayer).getPresentLocation();
        this.result = this.locations.get(currPlayerLocation).toString(currPlayer);
    }
    public void handleGet() throws GameException {
        Set<String> entireArtefacts = this.getEntireGameArtefacts();
        String currPlayerLocation = this.players.get(currPlayer).getPresentLocation();
        Set<String> accessibleArtefacts = this.locations.get(currPlayerLocation).getArtefacts().keySet();
        this.extractCommand.checkForArtefacts(entireArtefacts,accessibleArtefacts);
        Artefact artefact = this.locations.get(currPlayerLocation).getArtefacts().get(extractCommand.getArtefact());
        this.locations.get(currPlayerLocation).getArtefacts().remove(extractCommand.getArtefact());
        this.players.get(currPlayer).addPersonalArtefact(artefact);
        StringBuilder builder = new StringBuilder();
        builder.append("You picked up a ");
        builder.append(artefact.getName());
        this.result = builder.toString();
    }
    private Set<String> getEntireGameArtefacts() {
        Set<String> artefacts = new HashSet<>();

        // Iterate through locations
        for (Map.Entry<String, Location> locationEntry : this.locations.entrySet()) {
            Location location = locationEntry.getValue();
            artefacts.addAll(location.getArtefacts().keySet());
        }

        // Iterate through players
        for (Map.Entry<String, Player> playerEntry : this.players.entrySet()) {
            Player player = playerEntry.getValue();
            artefacts.addAll(player.getPersonalInventory().keySet());
        }

        return artefacts;
    }
    public void handleInventory() {
        if(!this.players.get(this.currPlayer).getPersonalInventory().isEmpty())
        {
            StringBuilder stringBuilder = new StringBuilder("You have:\n");
            HashMap<String,Artefact> personalInv = this.players.get(currPlayer).getPersonalInventory();
            for(String artefactName : personalInv.keySet()) {
                stringBuilder.append(artefactName).append("\n");
            }
            this.result = stringBuilder.toString();
        }else {
            this.result = "Got nothing in your inventory mate";
        }
    }
    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Starts a *blocking* socket server listening for new connections.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    /**
    * Do not change the following method signature or we won't be able to mark your submission
    * Handles an incoming connection from the socket server.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
