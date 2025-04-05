package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

class MyExtendedSTAGTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
    @Test
    void testGoto() {
        sendCommandToServer("rohit: goto forest");
        String response = sendCommandToServer("rohit: look");
        response = response.toLowerCase();
        assertTrue(response.contains("tall pine tree"), "Failed to move to forest - expected to see 'tall pine tree'");
    }

    @Test
    void testAdventureStory() {
        // Step 1: Start in the cabin and check surroundings
        sendCommandToServer("rohit: look");
        String response = sendCommandToServer("rohit: look");
        response = response.toLowerCase();
        assertTrue(response.contains("locked wooden trapdoor"), "Failed: Expected to see 'locked wooden trapdoor' in the cabin");

        // Step 2: Move to the forest
        sendCommandToServer("rohit: goto forest");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("tall pine tree"), "Failed: Expected to see 'tall pine tree' in the forest");

        // Step 3: Pick up the key
        sendCommandToServer("rohit: get key");
        response = sendCommandToServer("rohit: inv");
        assertTrue(response.contains("key"), "Failed: Key not found in inventory");

        // Step 4: Return to the cabin and unlock trapdoor
        sendCommandToServer("rohit: goto cabin");
        sendCommandToServer("rohit: unlock trapdoor");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("cellar"), "Failed: Expected 'cellar' to be accessible");

        // Step 5: Enter the cellar and encounter the elf
        sendCommandToServer("rohit: goto cellar");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("angry looking elf"), "Failed: Expected to see 'angry looking elf' in the cellar");

        // Step 6: Pick up the coin and pay the elf
        sendCommandToServer("rohit: goto cabin");
        sendCommandToServer("rohit: get coin");
        sendCommandToServer("rohit: goto cellar");
        sendCommandToServer("rohit: pay coin");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("shovel"), "Failed: Expected to see 'shovel' after paying coin");

        // Step 7: Pick up the shovel and axe
        sendCommandToServer("rohit: get shovel");
        sendCommandToServer("rohit: goto cabin");
        sendCommandToServer("rohit: get axe");
        response = sendCommandToServer("rohit: inv");
        assertTrue(response.contains("shovel") && response.contains("axe"), "Failed: Expected both 'shovel' and 'axe' in inventory");

        // Step 8: Cut the tree and pick up the log
        sendCommandToServer("rohit: goto forest");
        sendCommandToServer("rohit: cut tree");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("heavy wooden log"), "Failed: Expected to see 'heavy wooden log' after cutting tree");

        sendCommandToServer("rohit: get log");
        response = sendCommandToServer("rohit: inv");
        assertTrue(response.contains("log"), "Failed: Expected 'log' in inventory");

        // Step 9: Cross the river
        sendCommandToServer("rohit: goto riverbank");
        sendCommandToServer("rohit: bridge log");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("clearing"), "Failed: Expected 'clearing' to be accessible after bridging the river");

        // Step 10: Dig for treasure
        sendCommandToServer("rohit: goto clearing");
        sendCommandToServer("rohit: dig ground with shovel");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("pot of gold"), "Failed: Expected to see 'pot of gold' after digging");

        sendCommandToServer("rohit: get gold");
        response = sendCommandToServer("rohit: inv");
        assertTrue(response.contains("gold"), "Failed: Expected 'gold' in inventory");

        // Step 11: Get and use the horn
        sendCommandToServer("rohit: goto riverbank");
        sendCommandToServer("rohit: get horn");
        sendCommandToServer("rohit: blow horn");
        response = sendCommandToServer("rohit: look");
        assertTrue(response.contains("burly wood cutter"), "Failed: Expected to summon the 'burly wood cutter' after blowing the horn");

        System.out.println("Adventure story test passed successfully!");
    }

    @Test
    void testAdventureStoryWithTwoPlayers() {
        // Initialize players
        String player1 = "rohit";
        String player2 = "assassin";

        // Step 1: Both players start in the cabin and check surroundings
        sendCommandToServer(player1 + ": look");
        String response1 = sendCommandToServer(player1 + ": look");
        response1 = response1.toLowerCase();
        assertTrue(response1.contains("locked wooden trapdoor"), "Failed: Player1 expected to see 'locked wooden trapdoor' in the cabin");

        sendCommandToServer(player2 + ": look");
        String response2 = sendCommandToServer(player2 + ": look");
        response2 = response2.toLowerCase();
        assertTrue(response2.contains("locked wooden trapdoor"), "Failed: Player2 expected to see 'locked wooden trapdoor' in the cabin");

        // Step 2: Player1 moves to forest while Player2 stays in cabin
        sendCommandToServer(player1 + ": goto forest");
        response1 = sendCommandToServer(player1 + ": look");
        assertTrue(response1.contains("tall pine tree"), "Failed: Player1 expected to see 'tall pine tree' in the forest");

        // Step 3: Player1 picks up the key
        sendCommandToServer(player1 + ": get key");
        response1 = sendCommandToServer(player1 + ": inv");
        assertTrue(response1.contains("key"), "Failed: Key not found in Player1's inventory");

        // Step 4: Player2 tries to take potion from cabin
        sendCommandToServer(player2 + ": get potion");
        response2 = sendCommandToServer(player2 + ": inv");
        assertTrue(response2.contains("potion"), "Failed: Potion not found in Player2's inventory");

        // Step 5: Player1 returns to cabin and unlocks trapdoor
        sendCommandToServer(player1 + ": goto cabin");
        sendCommandToServer(player1 + ": unlock trapdoor");
        response1 = sendCommandToServer(player1 + ": look");
        assertTrue(response1.contains("cellar"), "Failed: Player1 expected 'cellar' to be accessible");

        // Step 6: Player2 should also see the unlocked trapdoor
        response2 = sendCommandToServer(player2 + ": look");
        assertTrue(response2.contains(player1), "Failed: Player2 expected to see 'player1'");

        // Step 7: Player1 enters cellar and encounters elf
        sendCommandToServer(player1 + ": goto cellar");
        response1 = sendCommandToServer(player1 + ": look");
        assertTrue(response1.contains("angry looking elf"), "Failed: Player1 expected to see 'angry looking elf' in the cellar");

        // Step 8: Player2 gets coin and follows
        sendCommandToServer(player2 + ": get coin");
        sendCommandToServer(player2 + ": goto cellar");
        response2 = sendCommandToServer(player2 + ": look");
        assertTrue(response2.contains("angry looking elf"), "Failed: Player2 expected to see 'angry looking elf' in the cellar");

        // Step 9: Player2 pays the elf
        sendCommandToServer(player2 + ": pay coin");
        response2 = sendCommandToServer(player2 + ": look");
        assertTrue(response2.contains("shovel"), "Failed: Expected to see 'shovel' after Player2 paying coin");

        // Step 10: Player2 gets shovel
        sendCommandToServer(player2 + ": get shovel");
        response2 = sendCommandToServer(player2 + ": inv");
        assertTrue(response2.contains("shovel"), "Failed: Shovel not found in Player2's inventory");

        // Step 11: Players return to cabin, Player1 gets axe
        sendCommandToServer(player1 + ": goto cabin");
        sendCommandToServer(player2 + ": goto cabin");
        sendCommandToServer(player1 + ": get axe");
        response1 = sendCommandToServer(player1 + ": inv");
        assertTrue(response1.contains("axe"), "Failed: Axe not found in Player1's inventory");

        // Step 12: Both players move to forest, Player1 cuts tree
        sendCommandToServer(player1 + ": goto forest");
        sendCommandToServer(player2 + ": goto forest");
        sendCommandToServer(player1 + ": cut tree");
        response1 = sendCommandToServer(player1 + ": look");
        assertTrue(response1.contains("heavy wooden log"), "Failed: Expected to see 'heavy wooden log' after cutting tree");

        // Step 13: Player1 gets log
        sendCommandToServer(player1 + ": get log");
        response1 = sendCommandToServer(player1 + ": inv");
        assertTrue(response1.contains("log"), "Failed: Expected 'log' in Player1's inventory");

        // Step 14: Players move to riverbank, Player1 bridges river
        sendCommandToServer(player1 + ": goto riverbank");
        sendCommandToServer(player2 + ": goto riverbank");
        sendCommandToServer(player1 + ": bridge log");
        response1 = sendCommandToServer(player1 + ": look");
        assertTrue(response1.contains("clearing"), "Failed: Player1 expected 'clearing' to be accessible after bridging the river");

        // Step 15: Both players can see the clearing now
        response2 = sendCommandToServer(player2 + ": look");
        assertTrue(response2.contains("clearing"), "Failed: Player2 expected to see accessible 'clearing'");

        // Step 16: Player2 moves to clearing and digs with shovel
        sendCommandToServer(player2 + ": goto clearing");
        sendCommandToServer(player2 + ": dig ground with shovel");
        response2 = sendCommandToServer(player2 + ": look");
        assertTrue(response2.contains("pot of gold"), "Failed: Expected to see 'pot of gold' after Player2 digging");

        // Step 17: Player2 gets gold
        sendCommandToServer(player2 + ": get gold");
        response2 = sendCommandToServer(player2 + ": inv");
        assertTrue(response2.contains("gold"), "Failed: Expected 'gold' in Player2's inventory");

        // Step 18: Player1 moves to clearing to see results
        sendCommandToServer(player1 + ": goto clearing");
        response1 = sendCommandToServer(player1 + ": look");
        assertFalse(response1.contains("pot of gold"), "Failed: Gold should have been taken by Player2");

        // Step 19: Player1 gets and uses horn
        sendCommandToServer(player1 + ": goto riverbank");
        sendCommandToServer(player1 + ": get horn");
        sendCommandToServer(player1 + ": blow horn");
        response1 = sendCommandToServer(player1 + ": look");
        assertTrue(response1.contains("burly wood cutter"), "Failed: Expected to summon the 'burly wood cutter' after blowing the horn");

        // Step 20: Player2 should also see the lumberjack if they return to riverbank
        sendCommandToServer(player2 + ": goto riverbank");
        response2 = sendCommandToServer(player2 + ": look");
        assertTrue(response2.contains("burly wood cutter"), "Failed: Player2 expected to see 'burly wood cutter' at riverbank");

        // Check health points for both players (assuming this is part of the game mechanics)
        response1 = sendCommandToServer(player1 + ": health");
        assertTrue(response1.contains("health point"), "Failed: Expected health information for Player1");

        response2 = sendCommandToServer(player2 + ": health");
        assertTrue(response2.contains("health point"), "Failed: Expected health information for Player2");
    }
}
