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


}
