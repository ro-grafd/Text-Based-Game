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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
//    ActionHandler actionHandler;
    Action actionToDo;
    List<String> entities;

    public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, SAXException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
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
    public GameServer(File entitiesFile, File actionsFile) throws ParseException, IOException, ParserConfigurationException, SAXException {
        // TODO implement your server logic here
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
        this.actions = new HashMap<>();
        this.result = "";
        this.readEntitiesFile();
        this.readActionsFile();
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

    public void readActionsFile() throws IOException, ParseException, ParserConfigurationException, SAXException {
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
        this.extractCommand = new ExtractCommand(command);
        this.currPlayer = extractCommand.getPlayerName();
        if(!this.players.containsKey(this.currPlayer)) {
            this.players.put(currPlayer, new Player(this.currPlayer, this.spawnLocation));
            this.locations.get(spawnLocation).getPlayers().add(this.currPlayer); // not adding player to clusterEntities as player is not entity!
        }
        this.performAction();
        return "";
    }
//    ./mvnw exec:java@client -Dexec.args="rohit"

    public void performAction() {

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
